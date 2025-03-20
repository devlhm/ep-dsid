package eachare;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class Main {
    public static void main(String[] args) {
        if(args.length != 3) {
            System.out.println("Uso: [NOME DO EXECUTAVEL] <endereco>:<porta> <vizinhos.txt> <diretorio_compartilhado>");
            System.exit(1);
        }

        String[] socketParams = args[0].split(":");
        String socketAddress = socketParams[0];
        int socketPort = Integer.parseInt(socketParams[1]);

        String peersFilePath = args[1];

        String sharedDirPath = args[2];

        NeighborList neighbors = new NeighborList(getInitialNeighbors(peersFilePath));

        Server server = new Server(socketPort, socketAddress, neighbors);
        server.open();

        Thread serverThread = new Thread(server);
        serverThread.start();

        validateDirectory(sharedDirPath);

        CommandHandler commandHandler = new CommandHandler(neighbors, sharedDirPath);
        commandHandler.start();

    }

    private static List<Peer> getInitialNeighbors(String peersFilePath) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(peersFilePath));

            List<Peer> neighbors = new ArrayList<>();

            String line = br.readLine();

            while(line != null) {
                String[] peerParams = line.split(":");
                Peer neighbor = new Peer(peerParams[0], Integer.parseInt(peerParams[1]));
                neighbors.add(neighbor);
                line = br.readLine();
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
