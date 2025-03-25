package eachare;

import eachare.commands.Command;
import eachare.commands.ListLocalFiles;
import eachare.commands.ListPeers;

import java.util.Scanner;

public class CommandHandler {

    private final NeighborList neighbors;
    private final String shareDirPath;
    private final MessageSender messageSender;

    public CommandHandler(NeighborList neighbors, String shareDirPath, MessageSender messageSender) {
        this.neighbors = neighbors;
        this.shareDirPath = shareDirPath;
        this.messageSender = messageSender;
    }

    public void start() {
        showMenu();

        Scanner sc = new Scanner(System.in);

        while(true) {
            int commandIdx = sc.nextInt();
            Command command = getCommand(commandIdx);

            if(command == null)
                System.err.println("Comando não encontrado. Tente novamente.");
            else {
                command.execute();
                if(commandIdx == 9)
                    break;
                showMenu();
            }
        }
    }

    private Command getCommand(int commandIdx) {

        Command command = null;

        switch(commandIdx) {
            case 1 -> command = new ListPeers(neighbors, messageSender);
            case 2 -> {}
            case 3 -> command = new ListLocalFiles(shareDirPath, messageSender);
            case 4 -> {}
            case 5 -> {}
            case 6 -> {}
            case 9 -> {}
        }

        return command;
    }

    public static void showMenu() {
        System.out.println("Escolha um comando:" +
                "\t[1] Listar peers\n" +
                "\t[2] Obter peers\n" +
                "\t[3] Listar arquivos locais\n" +
                "\t[4] Buscar arquivos\n" +
                "\t[5] Exibir estatisticas\n" +
                "\t[6] Alterar tamanho de chunk\n" +
                "\t[9] Sair");
    }
}
