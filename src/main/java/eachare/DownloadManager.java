package eachare;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class DownloadManager {

    private static class FileAssemblyData {
        final String fileName;
        final long fileSize;    // Definido por prepareDownload
        final int totalChunks;
        final List<String> receivedBase64Chunks;
        final AtomicInteger receivedChunkCount = new AtomicInteger(0);

        FileAssemblyData(String fileName, long fileSize, int totalChunks) {
            this.fileName = fileName;
            this.fileSize = fileSize;
            this.totalChunks = totalChunks;

            // Se este construtor é chamado, totalChunks deve ser > 0
            // (arquivos vazios com totalChunks=0 seriam tratados antes de chamar o DM)
            if (totalChunks <= 0) {
                // Isso seria um erro de lógica se o DM for invocado.
                System.err.println("Alerta: FileAssemblyData criado com totalChunks <= 0 para " + fileName);
                this.receivedBase64Chunks = new ArrayList<>(); // Evitar NullPointerException, mas indica problema
            } else {
                this.receivedBase64Chunks = new ArrayList<>(Collections.nCopies(totalChunks, null));
            }
        }
    }

    private final SharedFiles sharedFiles;
    private FileAssemblyData fileAssemblyData = null;
    private volatile CountDownLatch currentDownloadLatch;
    private volatile String currentlyDownloadingFileName;


    public DownloadManager(SharedFiles sharedFiles) {
        this.sharedFiles = sharedFiles;
    }

    public void prepareDownload(String fileName, long fileSize, int totalChunks) {
        if (totalChunks <= 0) {
            System.err.println("Erro em prepareDownload: totalChunks inválido (" + totalChunks + ") para " + fileName +
                    ". Arquivos vazios devem ser tratados antes de chamar o DownloadManager ou ter totalChunks=1 para um chunk vazio.");
            return;
        }
        this.currentlyDownloadingFileName = fileName;
        this.currentDownloadLatch = new CountDownLatch(1);
        fileAssemblyData = new FileAssemblyData(fileName, fileSize, totalChunks);
//        System.out.println("Preparando para baixar " + fileName + " (" + totalChunks + " chunks, " + fileSize + " bytes)");
    }

    public void handleReceivedChunk(Message message) {
        List<String> args = message.getArgs();
        // Formato esperado da mensagem FILE: fileName, chunkSize, chunkIndex, base64Data
        if (args.size() != 4) {
            System.err.println("Erro: Mensagem FILE com argumentos insuficientes. " + args);
            return;
        }

        String fileName = args.getFirst();
        int chunkIndex, chunkSize;
        String base64ChunkData = args.get(3);

        try {
            chunkSize = Integer.parseInt(args.get(1));
            chunkIndex = Integer.parseInt(args.get(2));
        } catch (NumberFormatException e) {
            System.err.println("Erro ao parsear chunkIndex da mensagem FILE para " + fileName + ": " + e.getMessage());
            return;
        }


        if (fileAssemblyData == null) {
            System.err.println("Erro: Recebido chunk para " + fileName +
                    " mas o download não foi preparado ou já foi finalizado/removido. Ignorando chunk.");
            return;
        }

        synchronized (fileAssemblyData) {
            // Validação do chunkIndex contra o totalChunks definido em prepareDownload
            if (chunkIndex < 0 || chunkIndex >= fileAssemblyData.totalChunks) {
                System.err.println("Erro: Índice de chunk " + chunkIndex + " inválido para " + fileName +
                        ". Esperado entre 0 e " + (fileAssemblyData.totalChunks - 1) + ". Ignorando chunk.");
                return;
            }

            // Verificar se o chunk já foi recebido
            if (fileAssemblyData.receivedBase64Chunks.get(chunkIndex) != null) {
//                System.out.println("Chunk " + chunkIndex + " para o arquivo " + fileName + " já recebido. Ignorando duplicata.");
                return;
            }

            fileAssemblyData.receivedBase64Chunks.set(chunkIndex, base64ChunkData);
            int count = fileAssemblyData.receivedChunkCount.incrementAndGet();

//            System.out.println("Chunk " + chunkIndex + "/" + (fileAssemblyData.totalChunks - 1) +
//                    " (" + count + "/" + fileAssemblyData.totalChunks + ") recebido para " + fileName +
//                    " de " + message.getOriginAddress() + ":" + message.getOriginPort());

            // Verificar conclusão
            if (count == fileAssemblyData.totalChunks) {
//                System.out.println("Todos os chunks recebidos para " + fileName + ". Montando arquivo...");
                assembleFile(fileAssemblyData);
//                System.out.println("\nDownload do arquivo " + fileName + " finalizado.");

                if (this.currentDownloadLatch != null && fileAssemblyData.fileName.equals(this.currentlyDownloadingFileName)) {
                    this.currentDownloadLatch.countDown();
                }

                this.fileAssemblyData = null;
                this.currentlyDownloadingFileName = null;
            }
        }
    }

    private void assembleFile(FileAssemblyData assemblyData) {
        Path outputPath = Paths.get(sharedFiles.getSharedDirPath(), assemblyData.fileName);
        try {
            Base64FileUtils.decodeAndCombineChunksToFile(assemblyData.receivedBase64Chunks, outputPath);

//            System.out.println("Arquivo " + assemblyData.fileName + " montado e salvo com sucesso em " + outputPath);

        } catch (IOException e) {
            System.err.println("Erro ao montar ou salvar o arquivo " + assemblyData.fileName + ": " + e.getMessage());
            try {
                Files.deleteIfExists(outputPath);
            } catch (IOException ex) {
                System.err.println("Erro ao tentar deletar arquivo parcial " + outputPath + ": " + ex.getMessage());
            }
        } catch (IllegalArgumentException e) {
            System.err.println("Erro ao decodificar Base64 para o arquivo " + assemblyData.fileName + ": " + e.getMessage());
            try {
                Files.deleteIfExists(outputPath);
            } catch (IOException ex) {
                System.err.println("Erro ao tentar deletar arquivo parcial " + outputPath + " após falha na decodificação: " + ex.getMessage());
            }
        }
    }

    public void awaitCurrentDownloadCompletion() throws InterruptedException {
        if (currentDownloadLatch != null) {
//            System.out.println("Aguardando finalização do download de: " + this.currentlyDownloadingFileName);
            currentDownloadLatch.await();
//            System.out.println("Sinal de conclusão do download de " + this.currentlyDownloadingFileName + " recebido.");
        }
    }
}