package Model.InvertFile;

import Model.File.Name;
import Model.File.Phrase;
import Model.File.Word;
import javafx.util.Pair;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MyDictionary {
    private int nextPhrase;
    private Map<Phrase, Pair<Integer,Integer>> wordsList;

    public MyDictionary() {
        nextPhrase = 1;
        wordsList = new ConcurrentHashMap<>(512);
    }

    public void insertWord(Phrase toInsert){
        // make this thread safe!!
        if(!wordsList.containsKey(toInsert)){
            wordsList.put(toInsert,new Pair<>(nextPhrase++,1));
        }
        else if (toInsert instanceof Name || toInsert instanceof Word){
            //check if Name or Word and according to this check if need to remove and re insert
        }
        else{
            Pair<Integer, Integer> old = wordsList.get(toInsert);
            Pair<Integer, Integer> x = new Pair<>(old.getKey(),old.getValue()+1);
            wordsList.replace(toInsert,x);
        }
    }
}
