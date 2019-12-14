package Model.InvertFile;

import Model.File.Term;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Indexer {
    //private int[][] matrix;
    //private MyDictionary words;
    //private int currentSizeRows;
    //private int currentSizeColumns;
    //private Integer currentWord;
    private static final int NUM_OF_DOCS = 472527;
    private Map<Integer, String> documents;// doc index to doc number
    private Integer currentDoc;
    private Map<Term, Integer> words;//words to amount in corpus Map
    private Map<Term, LinkedList<Integer>> postingFiles;

    public Indexer() {
        this(8388608, 4096);
    }

    public Indexer(int initialWordSize, int numOfDocsInMemory) {
        documents = new HashMap<>(NUM_OF_DOCS);
        currentDoc = 1;
        words = new HashMap<>(initialWordSize);
        postingFiles = new HashMap<>(numOfDocsInMemory);
        //currentWord = 1;
        //this.currentSizeRows = currentSizeRows;
        //this.currentSizeColumns = currentSizeColumns;
        //words = new MyDictionary();
        //matrix = new int[currentSizeRows][currentSizeColumns];
    }

    public void addWord(Term p) {
        if (words.containsKey(p)) {
            Integer amount = words.get(p);
            words.replace(p, amount + 1);
        } else {
            words.put(p, 1);
        }
        if (!postingFiles.containsKey(p)) {
            postingFiles.put(p, new LinkedList<>());
        }
        postingFiles.get(p).add(currentDoc);
    }

    public void addDoc(String doc) {
        documents.put(currentDoc, doc);
        currentDoc++;
        if (currentDoc % 4000 == 0) {
            //write to disc
            postingFiles = new HashMap<>(postingFiles.size());
//            System.gc();
//            System.gc();
        }
        else if(currentDoc == 472525+1)
        {
            //write to disc
            System.out.println(currentDoc);
        }
    }
//    private void extendColumn() {
//        int[][] tmpMatrix = new int[currentSizeRows][currentSizeColumns * 2];
//        for (int i = 0; i < currentSizeRows; i++) {
//            for (int j = 0; j < currentSizeColumns; j++) {
//                tmpMatrix[i][j] = matrix[i][j];
//            }
//        }
//        matrix = tmpMatrix;

//        currentSizeColumns = currentSizeColumns * 2;
//    }
//    private void extendRow() {
//        int[][] tmpMatrix = new int[currentSizeRows * 2][currentSizeColumns];
//        for (int i = 0; i < currentSizeRows; i++) {
//            for (int j = 0; j < currentSizeColumns; j++) {
//                tmpMatrix[i][j] = matrix[i][j];
//            }
//        }
//        matrix = tmpMatrix;
//        currentSizeRows = currentSizeRows * 2;

//

//    }

    public Map getWords() {
        return words;
    }

    public Map<Integer, String> getDocuments() {
        return documents;
    }

    public static void main(String[] args) {
//        Indexer indexer = new Indexer(2, 2);
//        indexer.extendRow();
//        System.out.println(indexer.currentSizeRows);
//        indexer.extendColumn();
//        System.out.println(indexer.currentSizeColumns);

    }
}
