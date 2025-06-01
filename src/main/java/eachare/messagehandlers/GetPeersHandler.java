package eachare.messagehandlers;

import eachare.*;
import eachare.repository.NeighborList;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GetPeersHandler implements MessageHandler {

    private final MessageSender messageSender;
    private final NeighborList neighbors;

    public GetPeersHandler(MessageSender messageSender, NeighborList neighbors) {
        this.messageSender = messageSender;
        this.neighbors = neighbors;
    }

    public void execute(Message message){
        List<String> args = new ArrayList<>();

        if (neighbors.containsAddress(message.getOriginAddress(), message.getOriginPort()))
            args.addFirst(String.valueOf(neighbors.size() - 1));
        else args.addFirst(String.valueOf(neighbors.size()));

        for (Peer peer : neighbors.getAll()){
            if (!Objects.equals(peer.getIpAddress(), message.getOriginAddress()) || (peer.getPort() != message.getOriginPort())){
                args.addLast(peer.toString());
            }
        }

        messageSender.trySend(new Message(MessageType.PEER_LIST, args), message.getOriginAddress(), message.getOriginPort());

    }
}
