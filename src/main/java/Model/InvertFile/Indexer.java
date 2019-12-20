package Model.InvertFile;

import Model.File.Name;
import Model.File.Phrase;
import Model.File.Term;
import Model.File.Word;
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
    private Map<String, Integer> phrasesDocs;
    private boolean toStem;

    public Indexer(boolean toStem) {
        this(16777216, 4096, toStem);
    }

    public Indexer(int initialWordSize, int numOfDocsInMemory, boolean toStem) {
        documents = new HashMap<>(NUM_OF_DOCS, 1);
        currentDoc = 0;
        words = new HashMap<>(initialWordSize, 4);
        postingFiles = new HashMap<>(numOfDocsInMemory);
        phrasesDocs = new HashMap<>();
        this.toStem = toStem;
    }

    public void removeSinglePhrases() {
        for (Map.Entry<String, Integer> entry : phrasesDocs.entrySet()) {
            words.remove(entry.getKey());
        }
    }

    public void addWord(Term p) {
        if (p instanceof Phrase) {
            if (words.containsKey(p.toString())) {
                if (phrasesDocs.containsKey(p.toString())) {
                    if (!phrasesDocs.get(p.toString()).equals(currentDoc)) {
                        phrasesDocs.remove(p.toString());
                    }
                }
            } else {
                phrasesDocs.put(p.toString(), currentDoc);
            }
        }
        if (p instanceof Word) {
            if (words.containsKey(p.toString().toUpperCase())) {
                Integer num = words.get(p.toString().toUpperCase());
                words.remove(p.toString().toUpperCase());
                words.put(p.toString(), num);
            }
        }
        if (p instanceof Name) {
            if (words.containsKey(p.toString().toLowerCase())) {
                p = new Word(p.toString(), toStem);
            }
        }
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
            postingFiles.get(p).addLast(new Pair<>(pair.getKey(), pair.getValue() + 1));
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
        } else if (currentDoc == 472525) {
            //write to disc
            System.out.println("Wrote to disc, doc number:" + currentDoc);
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
