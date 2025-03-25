package eachare.commands;

import eachare.MessageSender;

import java.io.File;

public class ListLocalFiles implements Command{

    private final String shareDirpath;
    private final MessageSender messageSender;

    public ListLocalFiles(String shareDirpath, MessageSender messageSender) {
        this.shareDirpath = shareDirpath;
        this.messageSender = messageSender;
    }

    @Override
    public void execute() {
        File directory = new File(shareDirpath);
        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();  // Lista arquivos e diretórios
            if (files != null) {
                for (File file : files) {
                    System.out.println(file.getName());
                }
            }
        } else {
            System.out.println("O diretório não existe ou não é um diretório válido.");
        }
    }
}
