package eachare.messagehandlers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import eachare.*;

public class DownloadHandler implements MessageHandler {
    private final SharedFiles sharedFiles;
    private final MessageSender messageSender;

    public DownloadHandler(SharedFiles sharedFiles, MessageSender messageSender) {
        this.sharedFiles = sharedFiles;
        this.messageSender = messageSender;
    }

    public void execute(Message message) {
        String encodedFile = "";
        String fileName = message.getArgs().getFirst();
        File file = sharedFiles.getFileByName(fileName);

        List<String> args = new ArrayList<>();

        try {
            encodedFile = Base64FileUtils.encodeFileToBase64(Path.of(file.getPath()));
        } catch (IOException e) {
            System.err.println("Erro ao ler ou codificar o arquivo: " + e.getMessage());
        }

        args.addFirst(fileName);
        args.add("0");
        args.add("0");
        args.addLast(encodedFile);

        messageSender.trySend(new Message(MessageType.FILE, args), message.getOriginAddress(), message.getOriginPort());
    }
}
