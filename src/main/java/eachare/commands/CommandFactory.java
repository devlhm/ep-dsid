package eachare.commands;

import eachare.MessageSender;
import eachare.NeighborList;

public class CommandFactory {
    private final NeighborList neighbors;
    private final String shareDirPath;
    private final MessageSender messageSender;

    public CommandFactory(NeighborList neighbors, String shareDirPath, MessageSender messageSender) {
        this.neighbors = neighbors;
        this.shareDirPath = shareDirPath;
        this.messageSender = messageSender;
    }

    public Command createCommand(int commandIdx) {
        return switch(commandIdx) {
            case 1 -> new ListPeers(neighbors, messageSender);
            case 2 -> new GetPeers(neighbors, messageSender);
            case 3 -> new ListLocalFiles(shareDirPath);
            case 9 -> new Bye(neighbors, messageSender);
            default -> null;
        };
    }
}
