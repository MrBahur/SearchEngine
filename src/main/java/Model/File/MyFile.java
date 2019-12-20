package Model.File;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * the way we represent a file in the memory
 * iterate over Docs in each file
 */

public class MyFile implements Iterable<MyDocument> {
    private String path;
    private FileIterator<MyDocument> iterator;
    private ArrayList<MyDocument> documents;
    private int currentIndex;

    /**
     * Constructor for files
     * used only by FileReader
     *
     * @param path path for the file
     */

    public MyFile(String path) {
        this.path = path;
        documents = new ArrayList<>();
        fillDocsFromFile(path);
        iterator = new FileIterator<>();
        iterator.list = this.documents;
        currentIndex = 0;
    }

    /**
     * debug method
     */
    public void printFile() {
        for (MyDocument x : this.documents) {
            x.printDoc();
        }
    }

    /**
     * create the Docs array to iterrate over
     *
     * @param path path for file
     */
    private void fillDocsFromFile(String path) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(path));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            boolean inFile = false;
            while ((line = reader.readLine()) != null) {
                if (line.equals("<DOC>")) {
                    inFile = true;
                } else if (line.equals("</DOC>")) {
                    this.documents.add(new MyDocument(stringBuilder.toString()));
                    stringBuilder.delete(0, stringBuilder.length());
                    inFile = false;
                } else if (inFile) {
                    stringBuilder.append(line);
                    stringBuilder.append("\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * iterator getter for java interface
     *
     * @return Iterator of docs
     */
    @Override
    public Iterator<MyDocument> iterator() {
        return iterator;
    }

    /**
     * a class for inside iterator
     *
     * @param <T> must be String i think
     */
    private class FileIterator<T> implements Iterator<T> {
        ArrayList<T> list;

        @Override
        public boolean hasNext() {
            return currentIndex < list.size();
        }

        @Override
        public T next() {
            return list.get(currentIndex++);
        }
    }


    /**
     * main test for MyFile
     *
     * @param args none
     */
    public static void main(String[] args) {
        MyFile m = new MyFile("F:\\Study\\SearchEngine\\corpus\\FB396001\\FB396001");
        for (MyDocument x : m.documents) {
            System.out.println(x.getDocNumber());
        }
    }
}
