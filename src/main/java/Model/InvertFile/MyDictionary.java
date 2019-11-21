package Model.InvertFile;

import Model.File.Name;
import Model.File.Phrase;
import Model.File.Word;
import javafx.util.Pair;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MyDictionary {
    private Integer nextPhrase;
    private Map<Phrase, Pair<Integer,Integer>> wordsList;//first number is the num in the invertFile table, second
    // number is the amount in corpus

    public MyDictionary() {
        nextPhrase = 1;
        wordsList = new ConcurrentHashMap<>(512);
    }

    public Integer insertWord(Phrase toInsert){
        // make this thread safe!!
        if(!wordsList.containsKey(toInsert)){
            int x;
            synchronized (this) {
                x = nextPhrase++;
            }
            wordsList.put(toInsert,new Pair<>(x,1));
        }
        else if (toInsert instanceof Name || toInsert instanceof Word){
            //check if Name or Word and according to this check if need to remove and re insert
        }
        else{
            synchronized (this) {
                Pair<Integer, Integer> old = wordsList.get(toInsert);
                Pair<Integer, Integer> x = new Pair<>(old.getKey(), old.getValue() + 1);
                wordsList.replace(toInsert, x);
            }
        }
        return null;//need to return the index that the Phrase sit in
    }
}
