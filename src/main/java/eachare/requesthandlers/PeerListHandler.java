package eachare.requesthandlers;

import eachare.*;

import java.util.ArrayList;
import java.util.List;

public class PeerListHandler {
    private static int peerListExecutionTimes = 0;

    public static void execute(Message message, NeighborList neighbors){
        List<String> args = new ArrayList<>(message.getArgs());

        if (peerListExecutionTimes == 0) peerListExecutionTimes = neighbors.getOnlineNumber();
        args.removeFirst();

        neighbors.updateStatusByAddress(message.getOriginAddress(), message.getOriginPort(), PeerStatus.ONLINE);

        for (String string : args) {
            String[] peersData = string.split(":");

            if (neighbors.containsAddress(peersData[0], Integer.parseInt(peersData[1]))) {
                neighbors.updateStatusByAddress(peersData[0], Integer.parseInt(peersData[1]), PeerStatus.valueOf(peersData[2]));
            } else {
                neighbors.add(new Peer(peersData[0], Integer.parseInt(peersData[1]), PeerStatus.valueOf(peersData[2])));
            }
        }

        peerListExecutionTimes--;
        if (peerListExecutionTimes == 0) CommandHandler.showMenu();
        }
}
