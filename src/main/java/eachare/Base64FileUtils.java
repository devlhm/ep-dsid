package eachare;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

public class Base64FileUtils {

    // Codifica o conteúdo de um arquivo em Base64
    public static String encodeFileToBase64(Path filePath) throws IOException {
        byte[] fileBytes = Files.readAllBytes(filePath);
        return Base64.getEncoder().encodeToString(fileBytes);
    }

    // Decodifica o conteúdo Base64 e grava em um arquivo
    public static void decodeBase64ToFile(String base64Content, Path outputPath) throws IOException {
        byte[] fileBytes = Base64.getDecoder().decode(base64Content);
        Files.write(outputPath, fileBytes);
    }
}