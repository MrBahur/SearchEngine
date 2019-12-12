package Model.InvertFile;

import Model.File.Name;
import Model.File.Term;
import Model.File.Word;
import javafx.util.Pair;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MyDictionary {
    private Integer nextPhrase;
    private Map<Term, Pair<Integer, Integer>> wordsList;//first number is the num in the invertFile table, second
    // number is the amount in corpus

    public MyDictionary() {
        nextPhrase = 1;
        wordsList = new ConcurrentHashMap<>(512);
    }

    public Integer insertWord(Term toInsert) {
        // make this thread safe!!
        Integer index = 0;
        if (!wordsList.containsKey(toInsert)) {
            synchronized (this) {
                index = nextPhrase++;
            }
            wordsList.put(toInsert, new Pair<>(index, 1));
        } else if (toInsert instanceof Name || toInsert instanceof Word) {
            //check if Name or Word and according to this check if need to remove and re insert
        } else {
            synchronized (this) {
                Pair<Integer, Integer> old = wordsList.get(toInsert);
                Pair<Integer, Integer> x = new Pair<>(old.getKey(), old.getValue() + 1);
                wordsList.replace(toInsert, x);
                index = old.getKey();
            }
        }
        return index;//need to return the index that the Phrase sit in
    }
    public int getSize(){
        return wordsList.size();
    }
}
