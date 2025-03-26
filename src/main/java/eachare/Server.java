package eachare;

import eachare.requesthandlers.ByeHandler;
import eachare.requesthandlers.HelloHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;

public class Server implements Runnable {

    private final int port;
    private final String ipAddress;
    private final NeighborList neighbors;
    private final Clock clock;

    private static ServerSocket socket;

    public Server(int port, String ipAddress, NeighborList neighbors, Clock clock) {
        this.port = port;
        this.ipAddress = ipAddress;
        this.neighbors = neighbors;
        this.clock = clock;
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

    private void onMessageReceived(Message message) {
        System.out.println("Mensagem recebida: \"" + message.toString().trim() + "\"");

        clock.increment();

        switch (message.getType()) {
            case HELLO -> {
                HelloHandler.execute(message, neighbors);
            }
            case GET_PEERS -> {
                // TODO: adicionar GetPeersHandler
            }
            case BYE -> {
                ByeHandler.execute(message, neighbors);
            }
        }
    }
}
