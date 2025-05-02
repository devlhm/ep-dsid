package eachare.messagehandlers;

import eachare.MessageSender;
import eachare.MessageType;
import eachare.NeighborList;

public class MessageHandlerFactory {

    private final NeighborList neighbors;
    private final MessageSender messageSender;

    public MessageHandlerFactory(NeighborList neighbors, MessageSender messageSender) {
        this.neighbors = neighbors;
        this.messageSender = messageSender;
    }

    public MessageHandler createHandler(MessageType type) {
        return switch(type) {
            case HELLO -> new HelloHandler(neighbors);
            case GET_PEERS -> new GetPeersHandler(messageSender, neighbors);
            case PEER_LIST -> new PeerListHandler(neighbors);
			default -> null;
		};
    }
}
