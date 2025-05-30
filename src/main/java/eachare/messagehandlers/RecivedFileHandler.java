package eachare.messagehandlers;

import eachare.*;

public class RecivedFileHandler implements MessageHandler {

    private final SharedFiles sharedFiles;
    private final DownloadManager downloadManager;

    public RecivedFileHandler(SharedFiles sharedFiles, DownloadManager downloadManager) {
        this.sharedFiles = sharedFiles;
        this.downloadManager = downloadManager;
    }

    @Override
    public void execute(Message message) {
        downloadManager.handleReceivedChunk(message);
    }

}
