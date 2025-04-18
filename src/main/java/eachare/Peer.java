package eachare;

public class Peer {

    private final String ipAddress;
    private final int port;
    private PeerStatus status = PeerStatus.OFFLINE;

    public Peer(String ipAddress, int port) {
        this.ipAddress = ipAddress;
        this.port = port;
    }

    public Peer(String ipAddress, int port, PeerStatus status) {
        this.ipAddress = ipAddress;
        this.port = port;
        this.status = status;
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
    }
}
