package eachare;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.List;

public class Base64FileUtils {

    // Codifica o conteúdo de um arquivo em Base64
    public static String encodeFileToBase64(Path filePath) throws IOException {
        byte[] fileBytes = Files.readAllBytes(filePath);
        return Base64.getEncoder().encodeToString(fileBytes);
    }

    // Decodifica o conteúdo Base64 e grava em um arquivo
    public static void decodeBase64ToFile(String base64Content, Path outputPath) throws IOException {
        if (base64Content == null || base64Content.isEmpty()) {
            Files.write(outputPath, new byte[0]);
            return;
        }
        byte[] fileBytes = Base64.getDecoder().decode(base64Content);
        Files.write(outputPath, fileBytes);
    }

    public static void decodeAndCombineChunksToFile(List<String> base64Chunks, Path outputFilePath) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(outputFilePath.toFile())) {
            for (String base64Chunk : base64Chunks) {
                try {
                    byte[] decodedChunkBytes = Base64.getDecoder().decode(base64Chunk);
                    fos.write(decodedChunkBytes);
                } catch (IllegalArgumentException e) {
                    throw new IOException("Falha ao decodificar o pedaço Base64: '" + base64Chunk + "'", e);
                }
            }
        }
    }

    public static byte[] decodeAndCombineChunks(List<String> base64Chunks) throws IOException {
        ByteArrayOutputStream combinedBytesStream = new ByteArrayOutputStream();

        for (String base64Chunk : base64Chunks) {
            try {
                byte[] decodedChunkBytes = Base64.getDecoder().decode(base64Chunk);
                combinedBytesStream.write(decodedChunkBytes);
            } catch (IllegalArgumentException e) {
                System.err.println("Erro ao decodificar o pedaço: '" + base64Chunk + "'. Erro: " + e.getMessage());
                throw new IOException("Falha ao decodificar um pedaço Base64.", e);
            }
        }

        return combinedBytesStream.toByteArray();
    }


    public static String encodeChunkFileToBase64(Path filePath, long chunkSize, int chunkIndex) throws IOException {
        if (chunkSize <= 0) {
            throw new IllegalArgumentException("O tamanho do chunk (chunkSize) deve ser um valor positivo.");
        }
        if (chunkIndex < 0) {
            throw new IllegalArgumentException("O índice do chunk (chunkIndex) não pode ser negativo.");
        }

        try (InputStream inputStream = Files.newInputStream(filePath)) {
            long fileSize = Files.size(filePath);

            if (fileSize == 0) {
                if (chunkIndex == 0) {
                    return "";
                } else {
                    System.err.println("Requisição de chunk inválida (" + chunkIndex + ") para arquivo vazio: " + filePath);
                    return null;
                }
            }

            long startByte = (long) chunkIndex * chunkSize;

            if (startByte >= fileSize) {
                System.err.println("Índice do chunk fora dos limites do arquivo.");
                return null;
            }

            long skipped = inputStream.skip(startByte);
            if (skipped != startByte) {
                throw new IOException("Não foi possível pular para a posição correta no arquivo.");
            }
            int bytesToRead = (int) Math.min(chunkSize, fileSize - startByte);
            byte[] chunkBytes = new byte[bytesToRead];

            int bytesRead = inputStream.read(chunkBytes, 0, bytesToRead);

            if (bytesRead != bytesToRead) {
                throw new IOException("Não foi possível ler o chunk completo do arquivo.");
            }

            return Base64.getEncoder().encodeToString(chunkBytes);
        }
    }
}