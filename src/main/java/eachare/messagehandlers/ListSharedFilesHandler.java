package eachare.messagehandlers;

import eachare.*;
import eachare.repository.NeighborList;

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
        sharedFiles.initializeCounterIfZero(neighbors.getOnlineNumber());

        int countAfterDecrement = sharedFiles.decrementAndGetPeersNumberShareFiles();

        Peer sender = neighbors.getByAddress(message.getOriginAddress(), message.getOriginPort());
        for (String arg : args) {
            try {
                String[] fileData = arg.split(":");
                String fileName = fileData[0];
                long fileSize = Long.parseLong(fileData[1]);
                sharedFiles.addNetworkFile(fileName, sender, fileSize);
            } catch (Exception e) {
                System.err.println("Erro ao processar o arquivo recebido do peer "
                        + (sender != null ? sender.getIpAddress() + ":" + sender.getPort() : "desconhecido")
                        + " para o argumento '" + arg + "': " + e.getMessage());
                e.printStackTrace(System.err);
            }
        }

        if (countAfterDecrement == 0) {
            showNetworkFiles(sharedFiles.getNetworkFiles());
        } else if (countAfterDecrement < 0) {
            System.err.println("Alerta: Contador de peers ficou negativo (" + countAfterDecrement + ").");
            sharedFiles.setPeersNumberShareFiles(0);
        }
    }

    private void showNetworkFiles(List<NetworkFile> arquivos) {
        System.out.println("Arquivos encontrados na rede:");

        int maxIndexWidth = String.valueOf(arquivos.size()).length() + 2;
        String formatHeader = "%-" + maxIndexWidth + "s %-20s | %-8s | %s%n";
        String formatRow = "%-" + maxIndexWidth + "s %-20s | %-8d | %s%n";

        System.out.printf(formatHeader, "", "Nome", "Tamanho", "Peers");
        System.out.printf(formatHeader, "[0]", "<Cancelar>", "", "");

        int index = 1;
        for (NetworkFile arquivo : arquivos) {
            String peers = arquivo.getSenders().stream()
                    .map(sender -> sender.getIpAddress() + ":" + sender.getPort())
                    .reduce((a, b) -> a + ", " + b)
                    .orElse("");
            System.out.printf(
                    formatRow,
                    "[" + index++ + "]",
                    arquivo.getFile().getName(),
                    arquivo.getFileSize(),
                    peers
            );
        }

        System.out.println("\nDigite o numero do arquivo para fazer o download: ");
    }
}
