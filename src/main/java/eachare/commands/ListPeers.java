package eachare.commands;

import eachare.NeighborList;

public class ListPeers implements Command {

    private final NeighborList neighbors;

    public ListPeers(NeighborList neighbors) {
        this.neighbors = neighbors;
    }

    @Override
    public void execute() {

    }
}
