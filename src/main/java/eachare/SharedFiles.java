package eachare;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SharedFiles {

    private final List<File> files = new ArrayList<>(); // local files
    private final List<NetworkFile> networkFiles; // shared files
    private final java.util.concurrent.atomic.AtomicInteger peersNumberShareFiles =
            new java.util.concurrent.atomic.AtomicInteger(0); // número de peers que compartilharam arquivos
    private final String sharedDirPath;

    public SharedFiles(String sharedDirPath) {
        this.networkFiles = Collections.synchronizedList(new ArrayList<>());
        this.sharedDirPath = sharedDirPath;

        getInitialFiles();
    }

    public String getSharedDirPath() {
        return sharedDirPath;
    }

    public void getAllFilesName (){
        for (File file : files) {
            System.out.println(file.getName());
        }
    }

    public void getInitialFiles() {
        File directory = new File(sharedDirPath);
        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();  // Lista arquivos e diretórios
            if (files != null) {
                for (File file : files) {
                    if (!this.files.contains(file)) {
                        this.files.add(file);
                    }
                }
            }
        } else {
            System.err.println("O diretório não existe ou não é um diretório válido.");
        }
    }

    public void addFile(Path fileName) {
        File file = new File(sharedDirPath + "/" + fileName);
        if (!files.contains(file)) {
            files.add(file);
        }
    }

    public void removeFile(Path fileName) {
        File file = new File(sharedDirPath + "/" + fileName);
        files.remove(file);
    }

    public void updateFile(Path fileName) {
        File file = new File(sharedDirPath + "/" + fileName);
        if (files.contains(file)) {
            files.remove(file);
            files.add(file);
        }
    }

    public List<File> getFiles() {
        return files;
    }

    public List<NetworkFile> getNetworkFiles() {
        synchronized (networkFiles) {
            return new ArrayList<>(networkFiles);
        }
    }

    public void addNetworkFile(String fileName, Peer sender, Long fileSize) {
        synchronized (networkFiles) {
            for (NetworkFile networkFile : networkFiles) {
                if (networkFile.getFileName().equals(fileName) && networkFile.getFileSize().equals(fileSize)) {
                    networkFile.addSender(sender);
                    return;
                }
            }
            networkFiles.add(new NetworkFile(new File(fileName), sender, fileSize));
        }
    }

    public void clearNetworkFiles() {
        networkFiles.clear();
    }

    public void setPeersNumberShareFiles(int peersNumberShareFiles) {
        this.peersNumberShareFiles.set(peersNumberShareFiles);
    }

    public int decrementAndGetPeersNumberShareFiles() {
        return this.peersNumberShareFiles.decrementAndGet();
    }

    public boolean initializeCounterIfZero(int expectedPeers) {
        return this.peersNumberShareFiles.compareAndSet(0, expectedPeers);
    }

    public File getFileByName(String fileName) {
        for (File file : files) {
            if (file.getName().equals(fileName)) {
                return file;
            }
        }
        return null;
    }
}
