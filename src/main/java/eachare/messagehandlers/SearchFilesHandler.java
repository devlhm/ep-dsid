package eachare.messagehandlers;

import eachare.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SearchFilesHandler implements MessageHandler{
    private final SharedFiles sharedFiles;
    private final MessageSender messageSender;

    public SearchFilesHandler(SharedFiles sharedFiles, MessageSender messageSender) {
        this.sharedFiles = sharedFiles;
        this.messageSender = messageSender;
    }

    public void execute(Message message) {
        List<String> args = new ArrayList<>();
        args.addFirst(String.valueOf(sharedFiles.getFiles().size()));

        for (File file : sharedFiles.getFiles()) {
            args.addLast(file.getName() + ":" + file.length());
        }

        messageSender.trySend(new Message(MessageType.LS_LIST, args), message.getOriginAddress(), message.getOriginPort());
    }
}
