package eachare.messagehandlers;

import eachare.*;

import java.io.File;
import java.util.List;

public class ListSharedFilesHandler implements MessageHandler{
    private final SharedFiles sharedFiles;
    private final NeighborList neighbors;

    public ListSharedFilesHandler(SharedFiles sharedFiles, NeighborList neighbors) {
        this.sharedFiles = sharedFiles;
        this.neighbors = neighbors;
    }

    public void execute(Message message) {
        List<String> args = message.getArgs();
        args.removeFirst();

        if (sharedFiles.getPeersNumberShareFiles() == 0) {
            sharedFiles.setPeersNumberShareFiles(neighbors.getOnlineNumber());
        }

        sharedFiles.decrementPeersNumberShareFiles();

        Peer sender = neighbors.getByAddress(message.getOriginAddress(), message.getOriginPort());

        for (String arg : args) {
            try {
                String[] fileData = arg.split(":");
                String fileName = fileData[0];
                long fileSize = Long.parseLong(fileData[1]);

                File file = new File(fileName);
                NetworkFile networkFile = new NetworkFile(file, sender, fileSize);
                sharedFiles.addNetworkFile(networkFile);
            } catch (Exception e) {
                System.err.println("Erro ao processar o arquivo recebido: " + e.getMessage());
            }
        }

        if (sharedFiles.getPeersNumberShareFiles() == 0) {
            showNetworkFiles(sharedFiles.getNetworkFiles());
        }
    }

    private void showNetworkFiles(List<NetworkFile> arquivos) {
        System.out.println("Arquivos encontrados na rede:");

        int maxIndexWidth = String.valueOf(arquivos.size()).length() + 2;
        String formatHeader = "%-" + maxIndexWidth + "s %-20s | %-8s | %s%n";
        String formatRow = "%-" + maxIndexWidth + "s %-20s | %-8d | %s%n";

        System.out.printf(formatHeader, "", "Nome", "Tamanho", "Peer");
        System.out.printf(formatHeader, "[0]", "<Cancelar>", "", "");

        int index = 1;
        for (NetworkFile arquivo : arquivos) {
            System.out.printf(
                    formatRow,
                    "[" + index++ + "]",
                    arquivo.getFile().getName(),
                    arquivo.getFileSize(),
                    arquivo.getPeer().getIpAddress() + ":" + arquivo.getPeer().getPort()
            );
        }

        System.out.println("\nDigite o numero do arquivo para fazer o download: ");
    }
}
