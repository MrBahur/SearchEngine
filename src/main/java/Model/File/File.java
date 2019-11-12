package Model.File;

import java.util.ArrayList;
import java.util.Iterator;

public class File implements Iterable<Document> {
    private String fileName;
    private FileIterator<Document> iterator;
    private ArrayList<Document> documents;

    //TODO Implement Iterator of Documents.
    @Override
    public Iterator<Document> iterator() {
        return iterator;
    }

    //TODO Implement Test for File.
    //test for File
    public static void main(String[] args) {

    }
    private class FileIterator<Document> implements Iterator<Document>{

        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public Document next() {
            return null;
        }
    }
}
