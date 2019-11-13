package Model.InvertFile;

import Model.File.Phrase;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InvertFile {
    private int[][] matrix;
    private MyDictionary words;
    private Map<Integer,String> documents;
    private int currentSizeRows;
    private int currentSizeColumns;

    public InvertFile(int currentSizeRows, int currentSizeColumns) {
        this.currentSizeRows = currentSizeRows;
        this.currentSizeColumns = currentSizeColumns;
        matrix = new int[currentSizeRows][currentSizeColumns];
        documents = new ConcurrentHashMap<>(currentSizeRows);
        words = new MyDictionary();
    }
    public InvertFile(){
        this(512,512);
    }

    private void extendRow(){

    }

    private void extendColumn(){

    }

    public void addWord(Phrase p, String doc){

    }

    public void addDoc(String doc){

    }

}
