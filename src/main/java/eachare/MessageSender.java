package eachare;

import java.io.*;
import java.net.Socket;

public class MessageSender {

    private final Clock clock;
    private final String originAddress;
    private final int originPort;

    public MessageSender(Clock clock, String originAddress, int originPort) {
        this.clock = clock;
        this.originAddress = originAddress;
        this.originPort = originPort;
    }

    public boolean trySend(Message message, String destinationAddress, int destinationPort) {

        try (Socket socket = new Socket(destinationAddress, destinationPort)) {
            clock.onSendMessage(message);

            message.setClockValue(clock.getValue());
            message.setOriginAddress(originAddress);
            message.setOriginPort(originPort);

            showMessage(message, destinationAddress, destinationPort);

            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println(message);
            return true;
        } catch (Exception e) {
            System.err.println("Erro enviando mensagem para " + destinationAddress + ":" + destinationPort);
            return false;
        }
    }

    private void showMessage(Message message, String destinationAddress, int destinationPort){
        System.out.println("Encaminhando mensagem \"" + message.toString().trim() + "\" para " + destinationAddress + ":" + destinationPort);
    }
}
