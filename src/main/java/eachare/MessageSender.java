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
            clock.increment();

            message.setClockValue(clock.getValue());
            message.setOriginAddress(originAddress);
            message.setOriginPort(originPort);

            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            out.write(message.toString());
            out.flush();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
