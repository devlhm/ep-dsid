package eachare;

import eachare.commands.*;

import java.util.InputMismatchException;
import java.util.Scanner;

public class CommandProcessor {

    private final CommandFactory commandFactory;

    public CommandProcessor(NeighborList neighbors, SharedFiles sharedFiles, MessageSender messageSender, Chunk chunk, DownloadManager downloadManager) {
        this.commandFactory = new CommandFactory(neighbors, sharedFiles, messageSender, chunk, downloadManager);
    }

    public void run() {
        showMenu();

        Scanner sc = new Scanner(System.in);

        while(true) {
            try {
                int commandIdx = sc.nextInt();
                Command command = commandFactory.createCommand(commandIdx);

                if(command == null)
                    System.err.println("Comando não encontrado. Tente novamente.");
                else {
                    command.execute();
                    if(commandIdx == 9)
                        break;
                    if(commandIdx != 2 && commandIdx != 4) showMenu();
                }
            } catch(InputMismatchException ex) {
                System.err.println("Entrada inválida. Digite um número.");
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
