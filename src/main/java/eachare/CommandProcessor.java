package eachare;

import eachare.commands.*;

import java.util.Scanner;

public class CommandProcessor {

    private final CommandFactory commandFactory;

    public CommandProcessor(NeighborList neighbors, String shareDirPath, MessageSender messageSender) {
        this.commandFactory = new CommandFactory(neighbors, shareDirPath, messageSender);
    }

    public void start() {
        showMenu();

        Scanner sc = new Scanner(System.in);

        while(true) {
            int commandIdx = sc.nextInt();
            Command command = commandFactory.createCommand(commandIdx);

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
