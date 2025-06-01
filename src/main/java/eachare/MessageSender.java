package eachare;

import java.io.*;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MessageSender {

    private final Clock clock;
    private final String originAddress;
    private final int originPort;

    private final Map<String, Socket> activeSockets = new ConcurrentHashMap<>();
    private final Map<String, PrintWriter> outputStreams = new ConcurrentHashMap<>();


    public MessageSender(Clock clock, String originAddress, int originPort) {
        this.clock = clock;
        this.originAddress = originAddress;
        this.originPort = originPort;
    }

    private String getKey(String address, int port) {
        return address + ":" + port;
    }

    private PrintWriter getPrintWriter(String destinationAddress, int destinationPort) throws IOException {
        String peerKey = getKey(destinationAddress, destinationPort);
        Socket socket = activeSockets.get(peerKey);
        PrintWriter writer = outputStreams.get(peerKey);

        // Se o ‘socket’ não existe, está fechado, não conectado, ou o writer não existe
        if (socket == null || socket.isClosed() || !socket.isConnected() || writer == null || writer.checkError()) {
            closeConnection(peerKey); // false para não logar como fechamento explícito
            socket = new Socket(destinationAddress, destinationPort);
            socket.setKeepAlive(true);
            activeSockets.put(peerKey, socket);
            writer = new PrintWriter(socket.getOutputStream(), true);
            outputStreams.put(peerKey, writer);
        }

        return writer;
    }

    public boolean trySend(Message message, String destinationAddress, int destinationPort) {
        clock.onSendMessage(message);
        message.setClockValue(clock.getValue());
        message.setOriginAddress(originAddress);
        message.setOriginPort(originPort);

        String peerKey = getKey(destinationAddress, destinationPort);

        try {
            PrintWriter out = getPrintWriter(destinationAddress, destinationPort);

            showMessage(message, destinationAddress, destinationPort);
            out.println(message.toString().trim());

            if (out.checkError()) {
                closeConnection(peerKey);
                return false;
            }

            if (message.getType() == MessageType.BYE) {
                closeConnection(peerKey);
            }
            // Para outras mensagens (DL, FILE), a conexão permanece aberta para reutilização.
            return true;
        } catch (Exception e) {
            System.err.println("Erro enviando mensagem para " + destinationAddress + ":" + destinationPort);
            closeConnection(peerKey);
            return false;
        }
    }

    public void closeConnection(String destinationAddress, int destinationPort) {
        String peerKey = getKey(destinationAddress, destinationPort);
        closeConnection(peerKey);
    }

    private void closeConnection(String peerKey) {
        Socket socket = activeSockets.remove(peerKey);
        PrintWriter writer = outputStreams.remove(peerKey);

        if (writer != null) {
            writer.close(); // Isso também deve fechar o outputStream do socket
        }
        if (socket != null && !socket.isClosed()) {
            try {
                socket.close();
            } catch (IOException e) {
                System.err.println("Erro ao fechar socket para " + peerKey + " " + e);
            }
        }
    }

    public void closeAllConnections() {
        for (String peerKey : activeSockets.keySet()) {
            closeConnection(peerKey);
        }
        activeSockets.clear();
        outputStreams.clear();
    }

    private void showMessage(Message message, String destinationAddress, int destinationPort){
        String messageContent = message.toString().trim();
        if (message.getType() == MessageType.FILE && messageContent.length() > 60) {
            messageContent = messageContent.substring(0, 60);
        }
        System.out.println("Encaminhando mensagem \"" + messageContent + "\" para " + destinationAddress + ":" + destinationPort);
    }
}
