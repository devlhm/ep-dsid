package eachare.commands;

import eachare.NeighborList;
import eachare.Peer;

public class ListPeers implements Command {

    private final NeighborList neighbors;

    public ListPeers(NeighborList neighbors) {
        this.neighbors = neighbors;
    }

    @Override
    public void execute() {
        System.out.println("[0] voltar para o menu anterior");

        int index = 1;
        for (Peer peer : neighbors.getAll()) {
            System.out.println("[" + index + "] " + peer.getIpAddress() + ":" + peer.getPort() + " " + peer.getStatus());
            index++;
        }
    }
}
