package eachare;

public class Message {
    private final int originPort;
    private final String originAddress;
    private final int clock;
    private final MessageType type;

    public Message(int originPort, String originAddress, int clock, MessageType type) {
        this.originPort = originPort;
        this.originAddress = originAddress;
        this.clock = clock;
        this.type = type;
    }

    @Override
    public String toString() {
        return originAddress + ":" + originPort + " " + clock + " " + type.toString();
    }
}
