package eachare;

import java.util.ArrayList;
import java.util.List;

public class NeighborList {
    private final List<Peer> neighbors = new ArrayList<>();

    public NeighborList(List<Peer> neighbors) {
        this.neighbors.addAll(neighbors);
    }

    public void add(Peer neighbor) {
        neighbors.add(neighbor);
    }

    public List<Peer> getAll() {
        return neighbors;
    }

    public Peer get(int idx) {
        return neighbors.get(idx);
    }

    public void updateAt(Peer neighbor, int idx) {
        neighbors.set(idx, neighbor);
    }
}
