package eachare.commands;

import eachare.*;
import eachare.repository.NeighborList;

import java.util.Scanner;

public class ListPeers implements Command {

    private final NeighborList neighbors;
    private final MessageSender messageSender;

    public ListPeers(NeighborList neighbors, MessageSender messageSender) {
        this.neighbors = neighbors;
        this.messageSender = messageSender;
    }

    @Override
    public void execute() {
        printNeighbors();

        Scanner sc = new Scanner(System.in);
        while (true) {
            int commandIdx;
            if (sc.hasNextInt()) {
                commandIdx = sc.nextInt();

                if (commandIdx == 0) break;

                if (commandIdx > 0 && commandIdx <= neighbors.getAll().size()) {
                    Peer peer = neighbors.get(commandIdx - 1);

                    boolean success = messageSender.trySend(new Message(MessageType.HELLO), peer.getIpAddress(), peer.getPort());

                    PeerStatus newStatus = success ? PeerStatus.ONLINE : PeerStatus.OFFLINE;

                    neighbors.updateStatusByAddress(peer.getIpAddress(), peer.getPort(), newStatus);

                    printNeighbors();
                } else {
                    System.out.println("Indice inválido! Escolha um número entre 0 e " + neighbors.getAll().size());
                }
            } else {
                System.out.println("Entrada inválida! Digite um número.");
                sc.next();
            }
        }
    }

    private void printNeighbors() {
        System.out.println("Lista de peers:");
        System.out.println("\t[0] voltar para o menu anterior");

        int index = 1;
        for (Peer peer : neighbors.getAll()) {
            System.out.println("\t[" + index + "] " + peer.getIpAddress() + ":" + peer.getPort() + " " + peer.getStatus());
            index++;
        }
    }
}
