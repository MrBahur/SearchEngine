package Model.File;

import java.util.ArrayList;
import java.util.Iterator;

public class MyFile implements Iterable<MyDocument> {
    private String path;
    private FileIterator<MyDocument> iterator;
    private ArrayList<MyDocument> documents;
    private int currentIndex;

    public MyFile(String path){
        this.path = path;
        documents = new ArrayList<>();
        //TODO fill documents with docs from file
        iterator = new FileIterator<>();
        iterator.list = this.documents;
        currentIndex = 0;
    }

    @Override
    public Iterator<MyDocument> iterator() {
        return iterator;
    }

    private class FileIterator<T> implements Iterator<T>{
        ArrayList<T> list;
        @Override
        public boolean hasNext() {
            return currentIndex<list.size();
        }

        @Override
        public T next() {
            return list.get(currentIndex);
        }
    }
    //TODO Implement Test for File.
    //test for File
    public static void main(String[] args) {

    }
}
