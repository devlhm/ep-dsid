package eachare;

import eachare.messagehandlers.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;

public class Server implements Runnable {

    private final int port;
    private final String ipAddress;
    private final Clock clock;
    private final MessageSender messageSender;
    private final MessageHandlerFactory messageHandlerFactory;

    private ServerSocket socket;

    public Server(int port, String ipAddress, NeighborList neighbors, Clock clock) {
        this.port = port;
        this.ipAddress = ipAddress;
        this.clock = clock;
        this.messageSender = new MessageSender(clock, ipAddress, port);
        this.messageHandlerFactory = new MessageHandlerFactory(neighbors, messageSender);
    }

    public void open() {
        try {
            socket = new ServerSocket(port, 3, InetAddress.getByName(ipAddress));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void close() {
        try {
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        while(!socket.isClosed()) {
            try {
                Socket client = socket.accept();
                new Thread(() -> {
					try (
                            Socket c = client;
                            BufferedReader in = new BufferedReader(new InputStreamReader(c.getInputStream()))
                    ) {
                        String line = in.readLine();

                        if(line != null)
						    onMessageReceived(new Message(line));

					} catch (IOException e) {
                        System.err.println("Erro ao ler mensagem: " + e.getMessage());
					}
				}).start();
            } catch (IOException e) {
                if(!socket.isClosed())
                    System.err.println("Erro ao aceitar conexão: " + e.getMessage());
            }
        }
    }

    public MessageSender getMessageSender (){
        return this.messageSender;
    }

    private void onMessageReceived(Message message) {

        showMessage(message);
        clock.increment();

        MessageHandler handler = messageHandlerFactory.createHandler(message.getType());
        handler.execute(message);
    }

    private void showMessage (Message message){
        if(message.getType() != MessageType.PEER_LIST) System.out.println("Mensagem recebida: \"" + message.toString().trim() + "\"");
        else System.out.println("Resposta recebida: \"" + message.toString().trim() + "\"");
    }
}
