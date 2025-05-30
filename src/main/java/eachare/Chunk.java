package eachare;

import java.util.Scanner;

public class Chunk {
    private int chunkSize;

    public Chunk () {
        setDefaultChunkSize();
    }

    public int getChunkSize() {
        return chunkSize;
    }

    public void setChunkSizeTo() {
        System.out.println("Digite o novo tamanho do chunk:");
        Scanner sc = new Scanner(System.in);
        int newChunkSize = sc.nextInt();
        if (newChunkSize <= 0) {
            System.out.println("Tamanho inválido. O tamanho deve ser maior que zero.");
            setChunkSizeTo();
        } else {
            this.chunkSize = newChunkSize;
            System.out.println("\tTamanho do chunk alterado: " + newChunkSize);
        }
    }

    public void setDefaultChunkSize() {
        this.chunkSize = 256;
    }
}
