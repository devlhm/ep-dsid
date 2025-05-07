package eachare.commands;

import eachare.SharedFiles;

public class ListLocalFiles implements Command{

    private final SharedFiles sharedFiles;

    public ListLocalFiles(SharedFiles sharedFiles) {
        this.sharedFiles = sharedFiles;
    }

    @Override
    public void execute() {
        sharedFiles.getAllFilesName();
    }
}
