package eachare;

import java.util.ArrayList;
import java.util.List;

public class NeighborList {

    private final List<Peer> neighbors = new ArrayList<>();

    public void add(Peer neighbor) {
        neighbors.add(neighbor);
        System.out.println("Adicionando novo peer " + neighbor.getIpAddress() + ":" + neighbor.getPort() + " status " + neighbor.getStatus());
    }

    public List<Peer> getAll() {
        return neighbors;
    }

    public Peer get(int idx) {
        return neighbors.get(idx);
    }

    public int size() {
        return neighbors.size();
    }

    public void updateStatusByAddress(String address, int port, PeerStatus status) {
        for(Peer neighbor : neighbors) {
            if(neighbor.getIpAddress().equals(address) && neighbor.getPort() == port)
                neighbor.setStatus(status);
        }

        System.out.println("Atualizando peer " + address + ":" + port + " status " + status.toString());
    }

    public boolean containsAddress(String address, int port) {
        for(Peer neighbor : neighbors) {
            if(neighbor.getIpAddress().equals(address) && neighbor.getPort() == port)
                return true;
        }

        return false;
    }
}
