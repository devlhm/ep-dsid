package eachare.commands;

import eachare.*;
import eachare.repository.DownloadStatRepository;
import eachare.repository.NeighborList;

public class CommandFactory {
    private final NeighborList neighbors;
    private final SharedFiles sharedFiles;
    private final MessageSender messageSender;
    private final Chunk chunk;
    private final DownloadManager downloadManager;
    private final DownloadStatRepository downloadStatRepository;

    public CommandFactory(NeighborList neighbors, SharedFiles sharedFiles, MessageSender messageSender, Chunk chunk, DownloadManager downloadManager, DownloadStatRepository downloadStatRepository) {
        this.neighbors = neighbors;
        this.sharedFiles = sharedFiles;
        this.messageSender = messageSender;
        this.chunk = chunk;
        this.downloadManager = downloadManager;
		this.downloadStatRepository = downloadStatRepository;
	}

    public Command createCommand(int commandIdx) {
        return switch(commandIdx) {
            case 1 -> new ListPeers(neighbors, messageSender);
            case 2 -> new GetPeers(neighbors, messageSender);
            case 3 -> new ListLocalFiles(sharedFiles);
            case 4 -> new SearchFiles(neighbors, messageSender, sharedFiles, chunk, downloadManager);
            case 5 -> new ShowStats(downloadStatRepository);
            case 6 -> new ChangeChunk(chunk);
            case 9 -> new Bye(neighbors, messageSender);
            default -> null;
        };
    }
}
