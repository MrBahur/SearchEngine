package Model.ReadFile;

import Model.File.MyDocument;
import Model.File.MyFile;

import java.io.File;
import java.util.*;

public class ReadFile<T> implements Iterable<T> {
    private String path;
    private String[] directories;
    private int currentDir;
    private StringIterator<String> iterator;

    public String getPath() {
        return path;
    }

    private class StringIterator<E> implements Iterator<E> {
        private E[] list;

        @Override
        public boolean hasNext() {
            return currentDir < list.length;
        }

        @Override
        public E next() {
            int x;
            synchronized (this) {
                x = currentDir++;
            }
            return list[x];
        }
    }

    @Override
    public Iterator iterator() {
        return this.iterator;
    }

    public ReadFile(String path) {
        this.path = path;
        directories = new File(path).list();
        currentDir = 0;
        iterator = new StringIterator<>();
        iterator.list = directories;
    }


    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        ReadFile<String> r = new ReadFile<>("F:\\Study\\SearchEngine\\corpus");
        for (String s : r) {
            MyFile f = new MyFile(r.path + "\\" + s + "\\" + s);
            for (MyDocument d : f) {

            }
        }
        long finish = System.currentTimeMillis();
        System.out.println("Time Elapsed =" + ((finish - start) / 1000.0) + "seconds");
    }
}