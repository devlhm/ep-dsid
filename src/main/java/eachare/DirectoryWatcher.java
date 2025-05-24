package eachare;

import java.io.IOException;
import java.nio.file.*;
import static java.nio.file.StandardWatchEventKinds.*;

public class DirectoryWatcher implements Runnable {
    private final SharedFiles sharedFiles;
    private volatile boolean running = true;
    private WatchService watchService;

    public DirectoryWatcher(SharedFiles sharedFiles) {
        this.sharedFiles = sharedFiles;
    }

    @Override
    public void run() {
        Path path = Paths.get(sharedFiles.getSharedDirPath());

        try (WatchService watchService = FileSystems.getDefault().newWatchService()) {
            path.register(watchService, ENTRY_CREATE, ENTRY_MODIFY, ENTRY_DELETE);
            this.watchService = watchService;
            while (running) {
                try {
                    WatchKey key = watchService.take();
                    for (WatchEvent<?> event : key.pollEvents()) {
                        WatchEvent.Kind<?> kind = event.kind();
                        Path fileName = (Path) event.context();

                        if (!fileName.toString().endsWith("~")) {
                            if (kind == ENTRY_CREATE) {
                                sharedFiles.addFile(fileName);
                            } else if (kind == ENTRY_MODIFY) {
                                sharedFiles.updateFile(fileName);
                            } else if (kind == ENTRY_DELETE) {
                                sharedFiles.removeFile(fileName);
                            }
                        }
                    }
                    boolean valid = key.reset();
                    if (!valid) {
                        break;
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (ClosedWatchServiceException e) {
                    break;
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException("Erro ao configurar o WatchService para o diretório: " + path, e);
        }
    }
        public void stop() {
        running = false;
        try {
            watchService.close();
        } catch (IOException e) {
            System.err.println("Erro ao fechar o WatchService: " + e.getMessage());
        }
    }
}
