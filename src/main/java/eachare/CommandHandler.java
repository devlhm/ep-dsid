package eachare;

import eachare.commands.Command;
import eachare.commands.ListLocalFiles;
import eachare.commands.ListPeers;

import java.util.Scanner;

public class CommandHandler {

    private final NeighborList neighbors;
    private final String shareDirPath;

    public CommandHandler(NeighborList neighbors, String shareDirPath) {
        this.neighbors = neighbors;
        this.shareDirPath = shareDirPath;
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
            }
        }
    }

    private Command getCommand(int commandIdx) {

        Command command = null;

        switch(commandIdx) {
            case 1:
                command = new ListPeers(neighbors);
            case 2:
                break;
            case 3:
                command = new ListLocalFiles(shareDirPath);
            case 4:
                break;
            case 5:
                break;
            case 6:
                break;
            case 9:
                break;
            default:
                break;
        }

        return command;
    }

    public static void showMenu() {
        System.out.println("Escolha um comando:\n" +
                "\t[1] Listar peers\n" +
                "\t[2] Obter peers\n" +
                "\t[3] Listar arquivos locais\n" +
                "\t[4] Buscar arquivos\n" +
                "\t[5] Exibir estatisticas\n" +
                "\t[6] Alterar tamanho de chunk\n" +
                "\t[9] Sair");
    }
}
