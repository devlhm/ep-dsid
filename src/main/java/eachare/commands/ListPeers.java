package eachare.commands;

import eachare.CommandHandler;
import eachare.NeighborList;
import eachare.Peer;
import java.util.Scanner;

public class ListPeers implements Command {

    private final NeighborList neighbors;

    public ListPeers(NeighborList neighbors) {
        this.neighbors = neighbors;
    }

    @Override
    public void execute() {
        System.out.println("[0] voltar para o menu anterior");

        int index = 1;
        for (Peer peer : neighbors.getAll()) {
            System.out.println("[" + index + "] " + peer.getIpAddress() + ":" + peer.getPort() + " " + peer.getStatus());
            index++;
        }

        Scanner sc = new Scanner(System.in);
        while (true) {
            int commandIdx;
            if (sc.hasNextInt()) {
                commandIdx = sc.nextInt();

                if (commandIdx == 0) break;

                if (commandIdx > 0 && commandIdx <= neighbors.getAll().size()) {
                    Peer peer = neighbors.get(commandIdx - 1);
                    System.out.println("encaminhando mensagem " + peer.getIpAddress() + ":" + peer.getPort());

                    //TODO: alterar para o envio de mensagem pelo socket
                } else {
                    System.out.println("Indice inválido! Escolha um número entre 1 e " + neighbors.getAll().size());
                }
            } else {
                System.out.println("Entrada inválida! Digite um número.");
                sc.next();
            }
        }

        CommandHandler.showMenu();
    }
}
