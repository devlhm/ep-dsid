package eachare.commands;

import eachare.MessageSender;
import eachare.NeighborList;
import eachare.SharedFiles;

public class CommandFactory {
    private final NeighborList neighbors;
    private final SharedFiles sharedFiles;
    private final MessageSender messageSender;

    public CommandFactory(NeighborList neighbors, SharedFiles sharedFiles, MessageSender messageSender) {
        this.neighbors = neighbors;
        this.sharedFiles = sharedFiles;
        this.messageSender = messageSender;
    }

    public Command createCommand(int commandIdx) {
        return switch(commandIdx) {
            case 1 -> new ListPeers(neighbors, messageSender);
            case 2 -> new GetPeers(neighbors, messageSender);
            case 3 -> new ListLocalFiles(sharedFiles);
            case 4 -> new SearchFiles(neighbors, messageSender, sharedFiles);
            case 9 -> new Bye(neighbors, messageSender);
            default -> null;
        };
    }
}
