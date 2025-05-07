package eachare.commands;

import eachare.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SearchFiles implements Command{
    private final NeighborList neighbors;
    private final MessageSender messageSender;
    private final SharedFiles sharedFiles;

    public SearchFiles(NeighborList neighbors, MessageSender messageSender, SharedFiles sharedFiles) {
        this.neighbors = neighbors;
        this.messageSender = messageSender;
        this.sharedFiles = sharedFiles;
    }

    @Override
    public void execute() {
        if (neighbors.getOnlineNumber() <= 0 ) {
            System.out.println("Nenhum peer online.");
            CommandProcessor.showMenu();
            return;
        }
        for (Peer peer : neighbors.getAll()) {
            if (peer.getStatus() == PeerStatus.ONLINE) {
                messageSender.trySend(new Message(MessageType.LS), peer.getIpAddress(), peer.getPort());
            }
        }

        startDownloadSelection();
    }

    private void startDownloadSelection() {

        Scanner sc = new Scanner(System.in);

        int selectedIndex = sc.nextInt();

        if (selectedIndex == 0) {
            sharedFiles.clearNetworkFiles();
            CommandProcessor.showMenu();
            return;
        }

        if (selectedIndex < 1 || selectedIndex > sharedFiles.getNetworkFiles().size()) {
            System.out.println("Seleção inválida. Tente novamente.");
            startDownloadSelection();
            return;
        }

        List<String> args = new ArrayList<>();

        NetworkFile selectedFile = sharedFiles.getNetworkFiles().get(selectedIndex - 1);

        System.out.println("Arquivo escolhido " + selectedFile.getFile().getName());

        args.addFirst(selectedFile.getFile().getName());
        args.add("0");
        args.add("0");

        messageSender.trySend(new Message(MessageType.DL, args), selectedFile.getPeer().getIpAddress(), selectedFile.getPeer().getPort());
    }
}