package eachare.messagehandlers;

import eachare.*;

import java.io.IOException;
import java.nio.file.Path;

public class RecivedFileHandler implements MessageHandler {

    private final SharedFiles sharedFiles;

    public RecivedFileHandler(SharedFiles sharedFiles) {
        this.sharedFiles = sharedFiles;
    }

    @Override
    public void execute(Message message) {
        String fileName = message.getArgs().getFirst();

        String filePath = sharedFiles.getSharedDirPath() + "/" + fileName;

        String encodedFile = message.getArgs().size() > 3 ? message.getArgs().get(3) : null;

        try {
            Base64FileUtils.decodeBase64ToFile(encodedFile, Path.of(filePath));
        } catch (IOException e) {
            System.err.println("Erro ao decodificar ou salvar o arquivo: " + e.getMessage());
        }

        System.out.println("\nDownload do arquivo " + fileName + " finalizado.");

        CommandProcessor.showMenu();
        sharedFiles.clearNetworkFiles();
    }

}
