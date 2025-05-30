package eachare.commands;

import eachare.Chunk;

public class ChangeChunk implements Command {
    private final Chunk chunk;

    public ChangeChunk(Chunk chunk) {
        this.chunk = chunk;
    }

    @Override
    public void execute() {
        chunk.setChunkSizeTo();
    }

}
