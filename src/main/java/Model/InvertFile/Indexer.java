package Model.InvertFile;

import Model.File.Term;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Indexer {
    private int[][] matrix;
    private MyDictionary words;
    private Map<Integer, String> documents;
    private int currentSizeRows;
    private int currentSizeColumns;
    private Integer currentDoc;
    private Integer currentWord;


    public Indexer(int currentSizeRows, int currentSizeColumns) {
        this.currentSizeRows = currentSizeRows;
        this.currentSizeColumns = currentSizeColumns;
        matrix = new int[currentSizeRows][currentSizeColumns];
        documents = new ConcurrentHashMap<>(currentSizeRows);
        words = new MyDictionary();
        currentDoc = 1;
        currentWord = 1;
    }

    public Indexer() {
        this(2048, 16384);
    }

    private void extendRow() {
        int[][] tmpMatrix = new int[currentSizeRows * 2][currentSizeColumns];
        for (int i = 0; i < currentSizeRows; i++) {
            for (int j = 0; j < currentSizeColumns; j++) {
                tmpMatrix[i][j] = matrix[i][j];
            }
        }
        matrix = tmpMatrix;
        currentSizeRows = currentSizeRows * 2;
    }

    private void extendColumn() {
        int[][] tmpMatrix = new int[currentSizeRows][currentSizeColumns * 2];
        for (int i = 0; i < currentSizeRows; i++) {
            for (int j = 0; j < currentSizeColumns; j++) {
                tmpMatrix[i][j] = matrix[i][j];
            }
        }
        matrix = tmpMatrix;
        currentSizeColumns = currentSizeColumns * 2;
    }

    public void addWord(Term p) {
        Integer wordIndex = words.insertWord(p);
        if (wordIndex > currentWord) {
            currentWord = wordIndex;
        }
        if (currentWord == currentSizeRows - 1) {
            synchronized (this) {
                extendRow();
            }
        }
        synchronized (this) {

            matrix[wordIndex][currentDoc]++;
        }
    }

    public void addDoc(String doc) {
        if (currentDoc == currentSizeColumns - 1) {
            synchronized (this) {
                extendColumn();
            }
        }
        documents.put(currentDoc++, doc);
    }

    public MyDictionary getWords() {
        return words;
    }

    public Map<Integer, String> getDocuments() {
        return documents;
    }

    public static void main(String[] args) {
        Indexer indexer = new Indexer(2, 2);
        indexer.extendRow();
        System.out.println(indexer.currentSizeRows);
        indexer.extendColumn();
        System.out.println(indexer.currentSizeColumns);

    }
}
