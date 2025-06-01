package eachare;

import eachare.repository.NeighborList;

import java.io.BufferedReader;
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
        SharedFiles sharedFiles = new SharedFiles(args[2]);

        NeighborList neighbors = getInitialNeighbors(peersFilePath);

        Clock clock = new Clock();
        Chunk chunk = new Chunk();

        MessageSender messageSender = new MessageSender(clock, socketAddress, socketPort);
        DownloadManager downloadManager = new DownloadManager(sharedFiles);

        Server server = new Server(socketPort, socketAddress, neighbors, clock, sharedFiles, downloadManager, messageSender);
        DirectoryWatcher dirWatcher = new DirectoryWatcher(sharedFiles);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            dirWatcher.stop();
            server.close();
            messageSender.closeAllConnections();
        }));

        server.open();

        Thread watcherThread = new Thread(dirWatcher);
        Thread serverThread = new Thread(server);

        watcherThread.start();
        serverThread.start();

        CommandProcessor commandProcessor = new CommandProcessor(neighbors, sharedFiles, server.getMessageSender(), chunk, downloadManager);
        commandProcessor.run();

        dirWatcher.stop();
        server.close();
        messageSender.closeAllConnections();

        try {
            watcherThread.join(5000);
            serverThread.join(5000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
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
}
