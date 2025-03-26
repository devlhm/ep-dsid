package eachare.requesthandlers;

import eachare.Message;
import eachare.NeighborList;
import eachare.PeerStatus;

public class ByeHandler {

    public static void execute(Message message, NeighborList neighbors){
        if(neighbors.containsAddress(message.getOriginAddress(), message.getOriginPort())) {
            neighbors.updateStatusByAddress(message.getOriginAddress(), message.getOriginPort(), PeerStatus.OFFLINE);
        }
    }
}
