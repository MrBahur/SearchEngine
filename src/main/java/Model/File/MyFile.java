package Model.File;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

public class MyFile implements Iterable<MyDocument> {
    private String path;
    private FileIterator<MyDocument> iterator;
    private ArrayList<MyDocument> documents;
    private int currentIndex;

    public MyFile(String path) {
        this.path = path;
        documents = new ArrayList<>();
        //TODO fill documents with docs from file
        fillDocsFromFile(path);
        iterator = new FileIterator<>();
        iterator.list = this.documents;
        currentIndex = 0;
    }
    public void printFile(){
        for (MyDocument x : this.documents) {
            System.out.println(x.getDocNumber());
        }
    }

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
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Iterator<MyDocument> iterator() {
        return iterator;
    }

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

    //TODO Implement Test for File.
    //test for File
    public static void main(String[] args) {
        MyFile m = new MyFile("F:\\Study\\SearchEngine\\corpus\\FB396001\\FB396001");
        for (MyDocument x : m.documents) {
            System.out.println(x.getDocNumber());
        }
    }
}
