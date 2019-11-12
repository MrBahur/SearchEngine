package Model.InvertFile;

import Model.File.Name;
import Model.File.Phrase;
import Model.File.Word;
import javafx.util.Pair;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MyDictionary {
    private int nextPhrase;
    private Map<Phrase, Pair<Integer,Integer>> wordsList;

    public MyDictionary() {
        nextPhrase = 1;
        wordsList = Collections.synchronizedMap(new HashMap<>());
    }

    public void insertWord(Phrase toInsert){
        if(toInsert instanceof Name || toInsert instanceof Word){

        }
        if(!wordsList.containsKey(toInsert)){
            wordsList.put(toInsert,new Pair<>(nextPhrase++,1));
        }
        else{

        }
    }
}
