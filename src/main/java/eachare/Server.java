package eachare;

import eachare.messagehandlers.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;

public class Server implements Runnable {

    private final int port;
    private final String ipAddress;
    private final NeighborList neighbors;
    private final Clock clock;
    private final MessageSender messageSender;
    private final MessageHandlerFactory messageHandlerFactory;

    private ServerSocket socket;

    public Server(int port, String ipAddress, NeighborList neighbors, Clock clock) {
        this.port = port;
        this.ipAddress = ipAddress;
        this.neighbors = neighbors;
        this.clock = clock;
        this.messageSender = new MessageSender(clock, ipAddress, port);
        this.messageHandlerFactory = new MessageHandlerFactory(neighbors, messageSender);
    }

    public void open() {
        try {
            socket = new ServerSocket(port, 3, InetAddress.getByName(ipAddress));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void close() {
        try {
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        while(!socket.isClosed()) {
            try {
                Socket client = socket.accept();
                BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));

                onMessageReceived(new Message(in.readLine()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public MessageSender getMessageSender (){
        return this.messageSender;
    }

    private void onMessageReceived(Message message) {

        showMessage(message);
        clock.increment();

        MessageHandler handler = messageHandlerFactory.createHandler(message.getType());
        handler.execute(message);
    }

    private void showMessage (Message message){
        if(message.getType() != MessageType.PEER_LIST) System.out.println("Mensagem recebida: \"" + message.toString().trim() + "\"");
        else System.out.println("Resposta recebida: \"" + message.toString().trim() + "\"");
    }
}
