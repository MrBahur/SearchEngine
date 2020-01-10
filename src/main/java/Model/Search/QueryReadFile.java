package Model.Search;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * the way we represent a file in the memory
 * iterate over Docs in each file
 */

public class QueryReadFile implements Iterable<MyQuery> {
    private String path;
    private FileIterator<MyQuery> iterator;
    private ArrayList<MyQuery> queries;
    private int currentIndex;

    /**
     * Constructor for QueryReadFile
     *
     * @param path path for the file
     */

    public QueryReadFile(String path) {
        this.path = path;
        queries = new ArrayList<>();
        fillQueriesFromFile(path);
        iterator = new FileIterator<>();
        iterator.list = this.queries;
        currentIndex = 0;
    }

    /**
     * debug method
     */
    public void printQuery() {
        for (MyQuery x : this.queries) {
            x.printQuery();
        }
    }

    /**
     * create the Queries array to iterrate over
     *
     * @param path path for file
     */
    private void fillQueriesFromFile(String path) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(path));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            boolean inQuery = false;
            while ((line = reader.readLine()) != null) {
                if (line.equals("<top>")) {
                    inQuery = true;
                } else if (line.equals("</top>")) {
                    this.queries.add(new MyQuery(stringBuilder.toString()));
                    stringBuilder.delete(0, stringBuilder.length());
                    inQuery = false;
                } else if (inQuery) {
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
     * @return Iterator of queries
     */
    @Override
    public Iterator<MyQuery> iterator() {
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
     * main test for this class
     *
     * @param args none
     */
    public static void main(String[] args) {
        QueryReadFile q = new QueryReadFile("d:\\documents\\users\\matanana\\Downloads\\03 queries.txt");
        q.printQuery();
    }
}
