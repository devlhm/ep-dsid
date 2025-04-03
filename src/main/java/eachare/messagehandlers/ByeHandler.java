package eachare.messagehandlers;

import eachare.Message;
import eachare.NeighborList;
import eachare.PeerStatus;

public class ByeHandler implements MessageHandler {

    private final NeighborList neighbors;

    public ByeHandler(NeighborList neighbors) {
        this.neighbors = neighbors;
    }

    public void execute(Message message){
        if(neighbors.containsAddress(message.getOriginAddress(), message.getOriginPort())) {
            neighbors.updateStatusByAddress(message.getOriginAddress(), message.getOriginPort(), PeerStatus.OFFLINE);
        }
    }
}
