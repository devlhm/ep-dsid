package eachare.messagehandlers;

import eachare.*;

public class ReceivedFileHandler implements MessageHandler {

    private final DownloadManager downloadManager;

    public ReceivedFileHandler(DownloadManager downloadManager) {
        this.downloadManager = downloadManager;
    }

    @Override
    public void execute(Message message) {
        downloadManager.handleReceivedChunk(message);
    }

}
