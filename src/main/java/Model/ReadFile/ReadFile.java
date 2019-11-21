package Model.ReadFile;

import Model.File.MyFile;

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
        long start = System.currentTimeMillis();
        ReadFile r = new ReadFile("F:\\Study\\SearchEngine\\corpus");
        for (String s : r.directories) {
            MyFile f = new MyFile(r.path + "\\" + s + "\\" + s);
            //f.printFile();
        }
        long finish = System.currentTimeMillis();
        System.out.println("Time Elapsed =" + (finish - start));
    }
}