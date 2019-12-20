package Model.InvertFile;

import Model.File.Name;
import Model.File.Phrase;
import Model.File.Term;
import Model.File.Word;
import javafx.util.Pair;

import Model.File.Date;
import Model.File.Number;
import Model.File.Range;
import Model.File.Selection;

import java.util.*;

public class Indexer {
    public static final int NUM_OF_DOCS = 472525;
    private Map<String, Pair<Integer, Integer>> documents;// doc ID to <max_tf,Number of unique words>
    private Integer currentDoc;
    private String currentDocID;
    private Map<String, Integer> words;//words to amount in corpus Map
    private Map<Term, LinkedList<Pair<String, Integer>>> postingFiles;
    private Map<String, Integer> phrasesDocs;
    private boolean toStem;
    public static int numberCounter = 0;

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
        Set<String> set = new HashSet<>();
        for (Map.Entry<String, Integer> entry : words.entrySet()) {
            if (entry.getValue() == 1) {
                set.add(entry.getKey());
            }
        }
        for (String s : set) {
            words.remove(s);
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
            if (p instanceof Number) {
                numberCounter++;
            }
        }
        if (!postingFiles.containsKey(p)) {
            postingFiles.put(p, new LinkedList<>());
            postingFiles.get(p).addLast(new Pair<>(currentDocID, 1));
        }
        if (!postingFiles.get(p).getLast().getKey().equals(currentDocID)) {
            postingFiles.get(p).addLast(new Pair<>(currentDocID, 1));
        } else {
            Pair<String, Integer> pair = postingFiles.get(p).getLast();
            postingFiles.get(p).removeLast();
            postingFiles.get(p).addLast(new Pair<>(pair.getKey(), pair.getValue() + 1));

        }
        Pair<Integer, Integer> pair = documents.get(currentDocID);
        if (postingFiles.get(p).getLast().getValue() == 1) {
            documents.replace(currentDocID, new Pair<>(pair.getKey(), pair.getValue() + 1));
        } else if (postingFiles.get(p).getLast().getValue() > pair.getKey()) {
            documents.replace(currentDocID, new Pair<>(pair.getKey() + 1, pair.getValue()));
        }
    }

    public void addDoc(String doc) {
        documents.put(doc, new Pair<>(0, 0));
        currentDocID = doc;
        currentDoc++;
        if (currentDoc % 4000 == 0) {
            //write to disc
            System.out.println("Wrote to disc, doc number:" + currentDoc);
            postingFiles = new HashMap<>(postingFiles.size());
//            System.gc();
//            System.gc();
        } else if (currentDoc == NUM_OF_DOCS) {
            //write to disc
            System.out.println("Wrote to disc, doc number:" + currentDoc);
        }
    }

    public Map getWords() {
        return words;
    }

    public static void main(String[] args) {

    }
}
