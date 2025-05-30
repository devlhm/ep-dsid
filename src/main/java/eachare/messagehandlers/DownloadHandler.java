package eachare.messagehandlers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
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

    @Override
    public void execute(Message message) {
        List<String> dlArgs = message.getArgs();
        if (dlArgs.size() < 3) {
            System.err.println("Erro: Mensagem DL com argumentos insuficientes: " + dlArgs);
            return;
        }

        String fileName = dlArgs.getFirst();
        int requestedChunkSize;
        int chunkIndex;

        try {
            requestedChunkSize = Integer.parseInt(dlArgs.get(1));
            chunkIndex = Integer.parseInt(dlArgs.get(2));
        } catch (NumberFormatException e) {
            System.err.println("Erro ao parsear argumentos da mensagem DL para " + fileName + ": " + e.getMessage());
            return;
        }

        File localFile = sharedFiles.getFileByName(fileName);
        if (localFile == null || !localFile.exists() || !localFile.isFile()) {
            System.err.println("Arquivo " + fileName + " não encontrado ou não é um arquivo válido nos arquivos locais para atender requisição DL.");
            return;
        }

        long fileSize = localFile.length();
        int chunkSize = (int) Math.min(requestedChunkSize, fileSize - (long) chunkIndex * requestedChunkSize);

        String base64ChunkData;
        Path filePath = Paths.get(sharedFiles.getSharedDirPath(), fileName);

        try {
            if (chunkIndex < 0 || (fileSize == 0 && chunkIndex > 0) ) {
                System.err.println("Índice de chunk inválido (" + chunkIndex + ") para arquivo " + fileName + " com " + "tamanho " + fileSize);
                return;
            }
            base64ChunkData = Base64FileUtils.encodeChunkFileToBase64(filePath, requestedChunkSize, chunkIndex);
            if (base64ChunkData == null && fileSize > 0) {
                System.err.println("Não foi possível codificar o chunk " + chunkIndex + " para o arquivo " + fileName);
                return;
            }
            if (base64ChunkData == null) {
                base64ChunkData = "";
            }

        } catch (IOException | IllegalArgumentException e) {
            System.err.println("Erro ao ler e codificar chunk " + chunkIndex + " do arquivo " + fileName + ": " + e.getMessage());
            return;
        }

        List<String> fileMsgArgs = new ArrayList<>();
        fileMsgArgs.add(fileName);                     // Arg 0: fileName
        fileMsgArgs.add(String.valueOf(chunkSize));    // Arg 1: chunkSize
        fileMsgArgs.add(String.valueOf(chunkIndex));   // Arg 2: chunkIndex
        fileMsgArgs.add(base64ChunkData);              // Arg 3: base64EncodedChunkData

        Message fileMessage = new Message(MessageType.FILE, fileMsgArgs);
//        System.out.println("Enviando chunk " + chunkIndex + " do arquivo " + fileName + " para " + message.getOriginAddress() + ":" + message.getOriginPort());
        messageSender.trySend(fileMessage, message.getOriginAddress(), message.getOriginPort());
    }
}
