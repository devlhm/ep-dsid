package eachare;

import java.util.Scanner;

public class Chunk {
    private int chunkSize = 256;

    public synchronized int getChunkSize() {
        return chunkSize;
    }

    public synchronized void setChunkSize(int chunkSize) {
        this.chunkSize = chunkSize;
    }
}
