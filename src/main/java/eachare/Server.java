package eachare;

import eachare.requesthandlers.ByeHandler;
import eachare.requesthandlers.GetPeersHandler;
import eachare.requesthandlers.HelloHandler;
import eachare.requesthandlers.PeerListHandler;

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

    private static ServerSocket socket;

    public Server(int port, String ipAddress, NeighborList neighbors, Clock clock) {
        this.port = port;
        this.ipAddress = ipAddress;
        this.neighbors = neighbors;
        this.clock = clock;
        this.messageSender = new MessageSender(clock, ipAddress, port);
    }

    public void open() {
        try {
            socket = new ServerSocket(port, 3, InetAddress.getByName(ipAddress));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void close() {
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
        //System.out.println("Mensagem recebida: \"" + message.toString().trim() + "\"");

        clock.increment();

        switch (message.getType()) {
            case HELLO -> {
                showMessage(message);
                HelloHandler.execute(message, neighbors);
            }
            case GET_PEERS -> {
                showMessage(message);
                GetPeersHandler.execute(message, messageSender, neighbors);
            }
            case BYE -> {
                showMessage(message);
                ByeHandler.execute(message, neighbors);
            }
            case PEER_LIST -> {
                showMessage(message);
                PeerListHandler.execute(message, neighbors);
            }
        }
    }

    private void showMessage (Message message){
        if(message.getType() != MessageType.PEER_LIST) System.out.println("Mensagem recebida: \"" + message.toString().trim() + "\"");
        else System.out.println("Resposta recebida: \"" + message.toString().trim() + "\"");
    }
}
