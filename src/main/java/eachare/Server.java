package eachare;

import java.io.IOException;
import java.net.*;

public class Server implements Runnable {

    private final int port;
    private final String ipAddress;
    private final NeighborList neighbors;

    private ServerSocket socket;

    public Server(int port, String ipAddress, NeighborList neighbors) {
        this.port = port;
        this.ipAddress = ipAddress;
        this.neighbors = neighbors;
    }

    public void open() {
        try {
            socket = new ServerSocket(port, 3, InetAddress.getByName(ipAddress));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        while(!socket.isClosed()) {
            try {
                Socket client = socket.accept();

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
