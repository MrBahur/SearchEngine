package Model.File;

import java.util.ArrayList;
import java.util.Iterator;

public class MyFile implements Iterable<MyDocument> {
    private String filePath;
    private FileIterator<MyDocument> iterator;
    private ArrayList<MyDocument> documents;
    private int currentDoc;

    public MyFile(String filePath) {
        this.filePath = filePath;
        this.documents = new ArrayList<>();
        //TODO fill documents with docs from file
        this.iterator = new FileIterator<>();
        iterator.list = documents;

    }

    @Override
    public Iterator<MyDocument> iterator() {
        return iterator;
    }

    private class FileIterator<T> implements Iterator<T> {
        ArrayList<T> list;

        @Override
        public boolean hasNext() {
            return currentDoc < documents.size();
        }

        @Override
        public T next() {
            T x;
            synchronized (this) {
                x = list.get(currentDoc);
                currentDoc++;
            }
            return x;
        }
    }

    //TODO Implement Test for File.
    //test for File
    public static void main(String[] args) {

    }
}
