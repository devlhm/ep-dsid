package eachare.commands;

import java.io.File;

public class ListLocalFiles implements Command{

    private final String shareDirpath;

    public ListLocalFiles(String shareDirpath) {
        this.shareDirpath = shareDirpath;
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
