package eachare.requesthandlers;

import eachare.Message;
import eachare.NeighborList;
import eachare.Peer;
import eachare.PeerStatus;

import java.util.ArrayList;
import java.util.List;

public class PeerListHandler {
    public static void execute(Message message, NeighborList neighbors){
        List<String> args = new ArrayList<>(message.getArgs());
        args.removeFirst();

        for (String string : args) {
            String[] peersData = string.split(":");

            if (neighbors.containsAddress(peersData[0], Integer.parseInt(peersData[1]))) {
                neighbors.updateStatusByAddress(peersData[0], Integer.parseInt(peersData[1]), PeerStatus.valueOf(peersData[2]));
            } else {
                neighbors.add(new Peer(peersData[0], Integer.parseInt(peersData[1]), PeerStatus.valueOf(peersData[2])));
            }
        }
    }
}
