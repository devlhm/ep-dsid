package eachare;

import java.io.File;
import java.util.Objects;

public class NetworkFile {
    private final File file;
    private final Peer sender;
    private final Long fileSize;

    public NetworkFile(File file, Peer sender, Long fileSize) {
        this.file = file;
        this.sender = sender;
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
        return sender;
    }

    public Long getFileSize() {
        return fileSize;
    }


}
