package eachare;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;


public class Main {
    public static void main(String[] args) {
        if(args.length != 3) {
            System.out.println("Uso: [NOME DO EXECUTAVEL] <endereco>:<porta> <vizinhos.txt> <diretorio_compartilhado>");
            System.exit(1);
        }

        String[] socketParams = args[0].split(":");

        if (socketParams.length != 2) {
            System.out.println("Endereço deve estar no formato <ip>:<porta>");
            System.exit(1);
        }

        String socketAddress = socketParams[0];
        int socketPort = Integer.parseInt(socketParams[1]);

        String peersFilePath = args[1];
        String sharedDirPath = args[2];

        NeighborList neighbors = getInitialNeighbors(peersFilePath);
        validateDirectory(sharedDirPath);

        Clock clock = new Clock();

        Server server = new Server(socketPort, socketAddress, neighbors, clock);

        Runtime.getRuntime().addShutdownHook(new Thread(server::close));

        server.open();

        Thread serverThread = new Thread(server);
        serverThread.start();

        CommandProcessor commandProcessor = new CommandProcessor(neighbors, sharedDirPath, server.getMessageSender());
        commandProcessor.run();

        server.close();
        try {
            serverThread.join();
        } catch (InterruptedException ignored) {}
    }

    private static NeighborList getInitialNeighbors(String peersFilePath) {
        try(BufferedReader br = new BufferedReader(new FileReader(peersFilePath))) {

            NeighborList neighbors = new NeighborList();

            String line = br.readLine();

            while(line != null) {
                String[] peerParams = line.split(":");

                if (peerParams.length != 2) {
                    System.err.println("Linha inválida no arquivo de vizinhos: " + line);
                    continue;
                }

                try {
                    Peer neighbor = new Peer(peerParams[0], Integer.parseInt(peerParams[1]));
                    neighbors.add(neighbor);
                    line = br.readLine();
                } catch(NumberFormatException ex) {
                    System.err.println("Erro ao converter a porta " + peerParams[1] + " para inteiro.");
                }
            }

            return neighbors;

        } catch (IOException e) {
            System.err.println("Arquivo de vizinhos nao encontrado ou erro na leitura!");
            System.exit(1);
            return null;
        }
    }

    private static void validateDirectory(String shareDirPath) {
        File directory = new File(shareDirPath);
        if (!directory.exists() || !directory.isDirectory()) {
            System.err.println("Erro: O diretório '" + shareDirPath + "' não é válido.");
            System.exit(1);
        }
    }
}
