package Model.ReadFile;

import java.io.File;

public class ReadFile {
    private String path;
    private String[] directories;
    private int currentDir;

    public ReadFile(String path) {
        this.path = path;
        directories = new File(path).list();
        currentDir = 0;
    }


    public static void main(String[] args) {
        ReadFile r = new ReadFile("F:\\Study\\SearchEngine\\corpus");
        for (String s : r.directories) {
            File f = new File(r.path + "\\" + s);
            String[] p = f.list();
            if (p[0].equals(s)) {
                System.out.println("good");
            } else
                System.out.println("bad");
        }
    }
}