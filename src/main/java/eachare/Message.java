package eachare;

import java.util.ArrayList;
import java.util.List;

public class Message {

    private String originAddress = "";
    private int originPort = 0;
    private int clockValue = 0;
    private final MessageType type;
    private List<String> args = new ArrayList<>();

    public Message(MessageType type) {
        this.type = type;
    }

    public Message(MessageType type, List<String> args) {
        this.type = type;
        this.args = args;
    }

    public Message(String encoded) {
        try {
            String[] messageParams = encoded.split("[ :]");

            this.originPort = Integer.parseInt(messageParams[0]);
            this.originAddress = messageParams[1];
            this.clockValue = Integer.parseInt(messageParams[2]);
            this.type = MessageType.valueOf(messageParams[3]);
        } catch(Exception ex) {
            throw new RuntimeException("Error decoding message: " + encoded);
        }
    }

    public String getOriginAddress() {
        return originAddress;
    }

    public void setOriginAddress(String originAddress) {
        this.originAddress = originAddress;
    }

    public int getOriginPort() {
        return originPort;
    }

    public void setOriginPort(int originPort) {
        this.originPort = originPort;
    }

    public int getClockValue() {
        return clockValue;
    }

    public void setClockValue(int clockValue) {
        this.clockValue = clockValue;
    }

    public MessageType getType() {
        return type;
    }

    public List<String> getArgs() {
        return args;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb
                .append(originAddress).append(":").append(originPort)
                .append(" ").append(clockValue)
                .append(" ").append(type.toString());

        for(String arg : args) {
            sb.append(" ").append(arg);
        }

        sb.append('\n');

        return sb.toString();
    }
}
