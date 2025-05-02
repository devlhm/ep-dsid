package eachare;

public class Peer {

    private final String ipAddress;
    private final int port;
    private PeerStatus status = PeerStatus.OFFLINE;
    private int clockValue = 0;

    public Peer(String encoded) {

        try {
            String[] peersData = encoded.split(":");

            ipAddress = peersData[0];
            port = Integer.parseInt(peersData[1]);
            status = PeerStatus.valueOf(peersData[2]);
            clockValue = Integer.parseInt(peersData[3]);
        } catch(Exception ex) {
            throw new RuntimeException("Erro decodificando Peer: " + encoded);
        }
    }

    public Peer(String ipAddress, int port) {
        this.ipAddress = ipAddress;
        this.port = port;
    }

    public Peer(String ipAddress, int port, PeerStatus status, int clockValue) {
        this.ipAddress = ipAddress;
        this.port = port;
        this.status = status;
        this.clockValue = clockValue;
    }

    public int getClockValue() {
        return clockValue;
    }

    public void setClockValue(int clockValue) {
        this.clockValue = clockValue;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public int getPort() {
        return port;
    }

    public PeerStatus getStatus() {
        return status;
    }

    public void setStatus(PeerStatus status) {
        this.status = status;
        System.out.println("Atualizando peer " + ipAddress + ":" + port + " status " + status.toString());
    }

    public void onMessageReceived(Message message) {
        clockValue = Math.max(clockValue, message.getClockValue());
        if(message.getType() == MessageType.BYE)
            setStatus(PeerStatus.OFFLINE);
        else setStatus(PeerStatus.ONLINE);
    }

    @Override
    public String toString() {
        return String.join(":",
                getIpAddress(),
                String.valueOf(getPort()),
                getStatus().name(),
                String.valueOf(getClockValue())
        );
    }
}
