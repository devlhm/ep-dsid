package eachare.commands;

import eachare.*;
import eachare.repository.NeighborList;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SearchFiles implements Command{
    private final NeighborList neighbors;
    private final MessageSender messageSender;
    private final SharedFiles sharedFiles;
    private final Chunk chunk;
    private final DownloadManager downloadManager;


    public SearchFiles(NeighborList neighbors, MessageSender messageSender, SharedFiles sharedFiles, Chunk chunk, DownloadManager downloadManager) {
        this.neighbors = neighbors;
        this.messageSender = messageSender;
        this.sharedFiles = sharedFiles;
        this.chunk = chunk;
        this.downloadManager = downloadManager;
    }

    @Override
    public void execute() {
        if (neighbors.getOnlineNumber() <= 0 ) {
            System.out.println("Nenhum peer online.");
            CommandProcessor.showMenu();
            return;
        }

        sharedFiles.clearNetworkFiles();

        for (Peer peer : neighbors.getAll()) {
            if (peer.getStatus() == PeerStatus.ONLINE) {
                messageSender.trySend(new Message(MessageType.LS), peer.getIpAddress(), peer.getPort());
            }
        }

        sharedFiles.setPeersNumberShareFiles(neighbors.getOnlineNumber());

        NetworkFile selectedFile = selectFileToDownload();

        if(selectedFile == null) {
            CommandProcessor.showMenu();
            return;
        }

        System.out.println("Arquivo escolhido " + selectedFile.getFileName());

        downloadFile(selectedFile);
    }

    private NetworkFile selectFileToDownload() {

        Scanner sc = new Scanner(System.in);
        NetworkFile selectedFile = null;
        List<NetworkFile> currentNetworkFiles;

        while (true) {
            try {
                if (!sc.hasNextLine()) {
                    System.err.println("Nenhuma entrada detectada. Retornando ao menu.");
                    break;
                }
                String lineInput = sc.nextLine();
                int selectedIndex = Integer.parseInt(lineInput.trim());

                if (selectedIndex == 0) {
                    sharedFiles.clearNetworkFiles();
                    break;
                }

                currentNetworkFiles = sharedFiles.getNetworkFiles();
                if (selectedIndex > 0 && selectedIndex <= currentNetworkFiles.size()) {
                    selectedFile = currentNetworkFiles.get(selectedIndex - 1);
                    break;
                } else {
                    System.err.println("Seleção inválida. Tente novamente. Arquivos disponíveis: " + currentNetworkFiles.size());
                }
            } catch (NumberFormatException e) {
                System.err.println("Entrada inválida. Por favor, digite um número.");
            } catch (IndexOutOfBoundsException e) {
                System.err.println("Número selecionado fora do intervalo. Tente novamente.");
            }
        }

        return selectedFile;
    }

    private void downloadFile(NetworkFile selectedFile) {
        long fileSize = selectedFile.getFileSize();
        long chunkSizeLong = chunk.getChunkSize();
        long totalChunks;

        if (fileSize == 0) {
            totalChunks = 1;
        } else {
            totalChunks = (fileSize + chunkSizeLong - 1) / chunkSizeLong;
            if (totalChunks == 0 && fileSize > 0) {
                totalChunks = 1;
            }
        }

        downloadManager.prepareDownload(selectedFile.getFileName(), fileSize, (int) totalChunks);

        List<Peer> peersList = selectedFile.getSenders();
        if (peersList.isEmpty()) {
            System.err.println("Nenhum peer disponível para baixar o arquivo: " + selectedFile.getFileName());
            sharedFiles.clearNetworkFiles();
            CommandProcessor.showMenu();
            return;
        }

        int peerListIndex = 0;
        for (int currentChunkIndex = 0; currentChunkIndex < totalChunks; currentChunkIndex++) {
            Peer peer = peersList.get(peerListIndex % peersList.size());

            List<String> dlArgs = new ArrayList<>();
            dlArgs.add(selectedFile.getFileName());
            dlArgs.add(String.valueOf(chunkSizeLong));
            dlArgs.add(String.valueOf(currentChunkIndex));

            messageSender.trySend(new Message(MessageType.DL, dlArgs), peer.getIpAddress(), peer.getPort());
            peerListIndex++;
        }

        try {
            downloadManager.awaitCurrentDownloadCompletion();
        } catch (InterruptedException e) {
            System.err.println("Espera pelo download do arquivo " + selectedFile.getFileName() + " foi interrompida.");
            Thread.currentThread().interrupt();
        } finally {
            sharedFiles.clearNetworkFiles();
            CommandProcessor.showMenu();
        }
    }
}