package eachare;

import eachare.commands.*;

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
                if(commandIdx != 2) showMenu();
            }
        }
    }

    private Command getCommand(int commandIdx) {

        Command command = null;

        switch(commandIdx) {
            case 1 -> command = new ListPeers(neighbors, messageSender);
            case 2 -> command = new GetPeers(neighbors, messageSender);
            case 3 -> command = new ListLocalFiles(shareDirPath);
            case 4 -> {}
            case 5 -> {}
            case 6 -> {}
            case 9 -> command = new Bye(neighbors, messageSender);
        }

        return command;
    }

    public static void showMenu() {
        System.out.println("""
                Escolha um comando:
                \t[1] Listar peers
                \t[2] Obter peers
                \t[3] Listar arquivos locais
                \t[4] Buscar arquivos
                \t[5] Exibir estatisticas
                \t[6] Alterar tamanho de chunk
                \t[9] Sair""");
    }
}
