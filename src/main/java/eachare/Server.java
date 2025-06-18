package eachare;

import eachare.messagehandlers.*;
import eachare.repository.NeighborList;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;

public class Server implements Runnable {

    private final int port;
    private final String ipAddress;
    private final Clock clock;
    private final MessageSender messageSender;
    private final MessageHandlerFactory messageHandlerFactory;
    private final NeighborList neighbors;

    private ServerSocket serverSocket;
    private volatile boolean running = true;

    public Server(int port, String ipAddress, NeighborList neighbors, Clock clock, SharedFiles sharedFiles, DownloadManager downloadManager, MessageSender messageSender) {
        this.port = port;
        this.ipAddress = ipAddress;
        this.clock = clock;
        this.messageSender = messageSender;
        this.messageHandlerFactory = new MessageHandlerFactory(neighbors, messageSender, sharedFiles, downloadManager);
        this.neighbors = neighbors;
    }

    public void open() {
        try {
            serverSocket = new ServerSocket(port, 50, InetAddress.getByName(ipAddress));
            running = true;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void close() {
        running = false;
        try {
            serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        while (running && serverSocket != null && !serverSocket.isClosed()) {
            try {
                Socket clientSocket = serverSocket.accept(); // Aguarda uma conexão

                // Cria uma nova thread para lidar com este cliente de forma persistente
                new Thread(() -> handleClient(clientSocket)).start();

            } catch (SocketException e) {

				if (running && (serverSocket == null || !serverSocket.isClosed())) {
					// Outra SocketException inesperada
					System.err.println("SocketException ao aceitar conexão no servidor: " + e.getMessage());
				}
			} catch (IOException e) {
                if (running) { // Logar apenas se o servidor deveria estar rodando
                    System.err.println("Erro de I/O ao aceitar conexão no servidor: " + e.getMessage());
                }
            }
        }
    }

    private void handleClient(Socket clientSocket) {
        String peerAddress = null;
        int peerPort = -1;
        try (Socket s = clientSocket;
             BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()))) {

            String line;
            // Loop para ler múltiplas mensagens da mesma conexão
            while (running && !s.isClosed() && (line = in.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue; // Ignora linhas vazias
                }
                try {
                    Message message = new Message(line);
                    peerAddress = message.getOriginAddress();
                    peerPort = message.getOriginPort();
                    onMessageReceived(message);
                    // Se a mensagem for BYE, o MessageHandler correspondente ou o MessageSender do outro lado
                    // podem fechar a conexão. O loop aqui terminará se in.readLine() retornar null.
                } catch (RuntimeException e) {
                    System.err.println("Erro ao ler mensagem: " + e.getMessage());
                }
            }
        } catch (SocketException e) {
            // Comum se o cliente fechar a conexão abruptamente
            if (peerAddress != null && peerPort != -1) {
                messageSender.closeConnection(peerAddress, peerPort);
                System.err.println("Conexão com " + peerAddress + ":" + peerPort + " encerrada (SocketException): " + e.getMessage());
            } else {
                System.err.println("Conexão com " + clientSocket.getRemoteSocketAddress() + " encerrada (SocketException): " + e.getMessage());
            }
        } catch (IOException e) {
            System.err.println("Erro de I/O ao comunicar com " + clientSocket.getRemoteSocketAddress() + e.getMessage());
        }
    }

    public MessageSender getMessageSender (){
        return this.messageSender;
    }

    private void onMessageReceived(Message message) {

        showMessage(message);
        clock.onReceiveMessage(message);
        neighbors.onMessageReceived(message);

        MessageHandler handler = messageHandlerFactory.createHandler(message.getType());
        if(handler != null)
            handler.execute(message);
    }

    private void showMessage (Message message){
        if(message.getType() != MessageType.PEER_LIST && message.getType() != MessageType.LS_LIST && message.getType() != MessageType.FILE) {
            System.out.println("Mensagem recebida: \"" + message.toString().trim() + "\"");
        }
        else if (message.getType() == MessageType.FILE){
            String messageContent = message.toString().trim();

            if (message.getType() == MessageType.FILE && messageContent.length() > 200) {
                messageContent = messageContent.substring(0, 60);
            }
            System.out.println("Resposta recebida: \"" + messageContent + "\"");
        } else System.out.println("Resposta recebida: \"" + message.toString().trim() + "\"");
    }
}
