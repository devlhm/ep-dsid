package eachare.messagehandlers;

import eachare.MessageSender;
import eachare.MessageType;
import eachare.NeighborList;
import eachare.SharedFiles;

public class MessageHandlerFactory {

    private final NeighborList neighbors;
    private final MessageSender messageSender;
    private final SharedFiles sharedFiles;

    public MessageHandlerFactory(NeighborList neighbors, MessageSender messageSender, SharedFiles sharedFiles) {
        this.neighbors = neighbors;
        this.messageSender = messageSender;
        this.sharedFiles = sharedFiles;
    }

    public MessageHandler createHandler(MessageType type) {
        return switch(type) {
            case HELLO -> new HelloHandler(neighbors);
            case GET_PEERS -> new GetPeersHandler(messageSender, neighbors);
            case PEER_LIST -> new PeerListHandler(neighbors);
            case LS -> new SearchFilesHandler(sharedFiles, messageSender);
            case LS_LIST -> new ListSharedFilesHandler(sharedFiles, neighbors);
            case DL -> new DownloadHandler(sharedFiles, messageSender);
            case FILE -> new RecivedFileHandler(sharedFiles);
			default -> null;
		};
    }
}
