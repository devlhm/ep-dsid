package eachare.commands;

import eachare.*;
import eachare.repository.NeighborList;

public class GetPeers implements Command {

    private final NeighborList neighbors;
    private final MessageSender messageSender;

    public GetPeers(NeighborList neighbors, MessageSender messageSender) {
        this.neighbors = neighbors;
        this.messageSender = messageSender;
    }

    @Override
    public void execute() {
        if(neighbors.size() == 0) {
            System.out.println("Nenhum vizinho conhecido.");
            CommandProcessor.showMenu();
        }
        else for (Peer peer : neighbors.getAll())
            messageSender.trySend(new Message(MessageType.GET_PEERS), peer.getIpAddress(), peer.getPort());
    }
}
