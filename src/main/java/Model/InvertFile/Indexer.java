package Model.InvertFile;

import Model.File.Phrase;
import Model.File.Term;
import javafx.util.Pair;

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
    private Map<String, Integer> words;//words to amount in corpus Map
    private Map<Term, LinkedList<Pair<Integer, Integer>>> postingFiles;
    private Map<Phrase, Integer> phrasesDocs;

    public Indexer() {
        this(16777216, 4096);
    }

    public Indexer(int initialWordSize, int numOfDocsInMemory) {
        documents = new HashMap<>(NUM_OF_DOCS, 1);
        currentDoc = 0;
        words = new HashMap<>(initialWordSize, 4);
        postingFiles = new HashMap<>(numOfDocsInMemory);
        phrasesDocs = new HashMap<>();
        //currentWord = 1;
        //this.currentSizeRows = currentSizeRows;
        //this.currentSizeColumns = currentSizeColumns;
        //words = new MyDictionary();
        //matrix = new int[currentSizeRows][currentSizeColumns];
    }

    //TBD Repair it to hold tf-idf for every doc and word
    public void addWord(Term p) {
        if (words.containsKey(p.toString())) {
            Integer amount = words.get(p.toString());
            words.replace(p.toString(), amount + 1);
        } else {
            words.put(p.toString(), 1);
        }
        if (!postingFiles.containsKey(p)) {
            postingFiles.put(p, new LinkedList<>());
            postingFiles.get(p).addLast(new Pair<>(currentDoc, 1));
        } else {
            Pair<Integer, Integer> pair = postingFiles.get(p).getLast();
            postingFiles.get(p).removeLast();
            postingFiles.get(p).addLast(new Pair<>(pair.getKey(), pair.getValue()+1));
        }

    }

    public void addDoc(String doc) {
        documents.put(currentDoc, doc);
        currentDoc++;
        if (currentDoc % 4000 == 0) {
            //write to disc
            System.out.println("Wrote to disc, doc number:" + currentDoc);
            postingFiles = new HashMap<>(postingFiles.size());
//            System.gc();
//            System.gc();
        } else if (currentDoc == 472525 + 1) {
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
