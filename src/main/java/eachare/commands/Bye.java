package eachare.commands;

import eachare.*;
import eachare.repository.NeighborList;

public class Bye implements Command{

    private final NeighborList neighbors;
    private final MessageSender messageSender;

    public Bye(NeighborList neighbors, MessageSender messageSender) {
        this.neighbors = neighbors;
        this.messageSender = messageSender;
    }

    @Override
    public void execute() {
        System.out.println("Saindo . . .");
        for (Peer peer : neighbors.getAll())
            if (peer.getStatus() == PeerStatus.ONLINE)
                messageSender.trySend(new Message(MessageType.BYE), peer.getIpAddress(), peer.getPort());
        messageSender.closeAllConnections();
    }
}
