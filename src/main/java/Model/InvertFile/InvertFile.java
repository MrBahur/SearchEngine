package Model.InvertFile;

import Model.File.Phrase;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InvertFile {
    private int[][] matrix;
    private MyDictionary words;
    private Map<Integer, String> documents;
    private int currentSizeRows;
    private int currentSizeColumns;
    private Integer currentDoc;
    private Integer currentWord;

    public InvertFile(int currentSizeRows, int currentSizeColumns) {
        this.currentSizeRows = currentSizeRows;
        this.currentSizeColumns = currentSizeColumns;
        matrix = new int[currentSizeRows][currentSizeColumns];
        documents = new ConcurrentHashMap<>(currentSizeRows);
        words = new MyDictionary();
        currentDoc = 1;
        currentWord = 1;
    }

    public InvertFile() {
        this(512, 512);
    }

    private void extendRow() {

    }

    private void extendColumn() {

    }

    public void addWord(Phrase p) {
        if (currentWord == currentSizeRows - 1) {
            synchronized (this) {
                extendRow();
            }
        }
        synchronized (this) {
            Integer wordIndex = words.insertWord(p);
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
}
