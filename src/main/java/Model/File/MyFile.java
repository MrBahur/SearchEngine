package Model.File;

import java.util.ArrayList;
import java.util.Iterator;

public class MyFile implements Iterable<MyDocument> {
    private String fileName;
    private FileIterator<MyDocument> iterator;
    private ArrayList<MyDocument> documents;

    //TODO Implement Iterator of Documents.
    @Override
    public Iterator<MyDocument> iterator() {
        return iterator;
    }


    private class FileIterator<T> implements Iterator<T>{

        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public T next() {
            return null;
        }
    }
    //TODO Implement Test for File.
    //test for File
    public static void main(String[] args) {

    }
}
