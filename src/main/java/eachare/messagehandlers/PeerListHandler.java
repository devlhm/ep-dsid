package eachare.messagehandlers;

import eachare.*;
import eachare.repository.NeighborList;

import java.util.ArrayList;
import java.util.List;

public class PeerListHandler implements MessageHandler {
    private static int peerListExecutionTimes = 0;

    private final NeighborList neighbors;

    public PeerListHandler(NeighborList neighbors) {
        this.neighbors = neighbors;
    }

    public void execute(Message message) {
        List<String> args = new ArrayList<>(message.getArgs());

        if (peerListExecutionTimes == 0) peerListExecutionTimes = neighbors.getOnlineNumber();
        args.removeFirst();

        for (String string : args) {
            try {
                Peer receivedPeer = new Peer(string);
                Peer localPeer = neighbors.getByAddress(receivedPeer.getIpAddress(), receivedPeer.getPort());

                if (localPeer != null) {
                    if (localPeer.getClockValue() < receivedPeer.getClockValue()) {
                        neighbors.updateByAddress(receivedPeer);
                    }
                } else
                    neighbors.add(receivedPeer);
            } catch(Exception ex) {
                System.err.println(ex.getMessage());
            }
        }

        peerListExecutionTimes--;
        if (peerListExecutionTimes == 0) CommandProcessor.showMenu();
    }
}
