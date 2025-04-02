package eachare.commands;

import eachare.*;

public class GetPeers implements Command {

    private final NeighborList neighbors;
    private final MessageSender messageSender;

    public GetPeers(NeighborList neighbors, MessageSender messageSender) {
        this.neighbors = neighbors;
        this.messageSender = messageSender;
    }

    @Override
    public void execute() {
        for (Peer peer : neighbors.getAll()) {
            messageSender.trySend(new Message(MessageType.GET_PEERS), peer.getIpAddress(), peer.getPort());
        }
    }
}
