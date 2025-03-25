package eachare.requesthandlers;

import eachare.Message;
import eachare.NeighborList;
import eachare.Peer;
import eachare.PeerStatus;

public class HelloHandler {

    public static void execute(Message message, NeighborList neighbors) {
        if(neighbors.containsAddress(message.getOriginAddress(), message.getOriginPort())) {
            neighbors.updateStatusByAddress(message.getOriginAddress(), message.getOriginPort(), PeerStatus.ONLINE);
        } else {
            Peer peer = new Peer(message.getOriginAddress(), message.getOriginPort(), PeerStatus.ONLINE);
            neighbors.add(peer);
        }
    }
}
