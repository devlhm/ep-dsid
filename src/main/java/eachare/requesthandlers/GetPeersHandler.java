package eachare.requesthandlers;

import eachare.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GetPeersHandler {

    public static void execute(Message message, MessageSender messageSender, NeighborList neighbors){
        List<String> args = new ArrayList<>();

        if (neighbors.containsAddress(message.getOriginAddress(), message.getOriginPort())) args.addFirst(String.valueOf(neighbors.size() - 1));
        else args.addFirst(String.valueOf(neighbors.size()));

        for (Peer peer : neighbors.getAll()){
            if (!Objects.equals(peer.getIpAddress(), message.getOriginAddress()) || (peer.getPort() != message.getOriginPort())){
                args.addLast(String.join(":", peer.getIpAddress(), String.valueOf(peer.getPort()), peer.getStatus().name(), "0"));
            }
        }

        messageSender.trySend(new Message(MessageType.PEER_LIST, args), message.getOriginAddress(), message.getOriginPort());

    }
}
