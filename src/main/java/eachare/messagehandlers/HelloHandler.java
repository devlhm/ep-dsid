package eachare.messagehandlers;

import eachare.Message;
import eachare.repository.NeighborList;
import eachare.Peer;
import eachare.PeerStatus;

public class HelloHandler implements MessageHandler {

    private final NeighborList neighbors;

    public HelloHandler(NeighborList neighbors) {
        this.neighbors = neighbors;
    }

    public void execute(Message message) {
        if(!neighbors.containsAddress(message.getOriginAddress(), message.getOriginPort())) {
            Peer peer = new Peer(message.getOriginAddress(), message.getOriginPort(), PeerStatus.ONLINE, message.getClockValue());
            neighbors.add(peer);
        }
    }
}
