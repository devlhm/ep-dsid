package eachare.commands;

import eachare.Chunk;

import java.util.Scanner;

public class ChangeChunk implements Command {
    private final Chunk chunk;

    public ChangeChunk(Chunk chunk) {
        this.chunk = chunk;
    }

    @Override
    public void execute() {

        while(true) {
            System.out.println("Digite o novo tamanho do chunk:");
            Scanner sc = new Scanner(System.in);
            int newChunkSize = sc.nextInt();
            if (newChunkSize <= 0) {
                System.out.println("Tamanho inválido. O tamanho deve ser maior que zero.");
            } else {
                this.chunk.setChunkSize(newChunkSize);
                System.out.println("\tTamanho do chunk alterado: " + newChunkSize);
                break;
            }
        }
    }

}
