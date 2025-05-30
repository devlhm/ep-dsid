package eachare.messagehandlers;

import eachare.*;

public class MessageHandlerFactory {

    private final NeighborList neighbors;
    private final MessageSender messageSender;
    private final SharedFiles sharedFiles;
    private final DownloadManager downloadManager;

    public MessageHandlerFactory(NeighborList neighbors, MessageSender messageSender, SharedFiles sharedFiles, DownloadManager downloadManager) {
        this.neighbors = neighbors;
        this.messageSender = messageSender;
        this.sharedFiles = sharedFiles;
        this.downloadManager = downloadManager;
    }

    public MessageHandler createHandler(MessageType type) {
        return switch(type) {
            case HELLO -> new HelloHandler(neighbors);
            case GET_PEERS -> new GetPeersHandler(messageSender, neighbors);
            case PEER_LIST -> new PeerListHandler(neighbors);
            case LS -> new SearchFilesHandler(sharedFiles, messageSender);
            case LS_LIST -> new ListSharedFilesHandler(sharedFiles, neighbors);
            case DL -> new DownloadHandler(sharedFiles, messageSender);
            case FILE -> new RecivedFileHandler(sharedFiles, downloadManager);
			default -> null;
		};
    }
}
