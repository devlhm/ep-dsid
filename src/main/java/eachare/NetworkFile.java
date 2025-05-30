package eachare;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class NetworkFile {
    private final File file;
    private final List<Peer> senders; // List of peers that have this file
    private final Long fileSize;

    public NetworkFile(File file, Peer sender, Long fileSize) {
        this.file = file;
        this.senders = new ArrayList<>();
        this.senders.add(sender);
        this.fileSize = fileSize;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        NetworkFile that = (NetworkFile) obj;
        return Objects.equals(file, that.file);
    }

    @Override
    public int hashCode() {
        return Objects.hash(file);
    }

    public File getFile() {
        return file;
    }

    public Peer getPeer() {
        return senders.getFirst();
    }

    public Long getFileSize() {
        return fileSize;
    }

    public String getFileName() {
        return file.getName();
    }

    public void addSender(Peer sender) {
        if (!senders.contains(sender)) {
            senders.add(sender);
        }
    }

    public List<Peer> getSenders() {
        return senders;
    }
}
