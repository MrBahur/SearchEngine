package Model;

import Model.Parser.Parser;
import javafx.util.Pair;

import java.util.HashMap;
import java.util.Map;

public class Model {
    private Parser parser;
    private Map<String, Pair<Integer, Integer>> dictionary;//Term -> <amount in corpus Map , pointer to posting file>
    private Map<String, Pair<Integer, Integer>> documents;// doc ID -> <max_tf,Number of unique words>

    public Model() {
        dictionary = new HashMap<>();
        documents = new HashMap<>();
    }

    public Map<String, Pair<Integer, Integer>> getDictionary() {
        return dictionary;
    }

    public void setDictionary(Map<String, Pair<Integer, Integer>> dictionary) {
        this.dictionary = dictionary;
    }

    public Map<String, Pair<Integer, Integer>> getDocuments() {
        return documents;
    }

    public void setDocuments(Map<String, Pair<Integer, Integer>> documents) {
        this.documents = documents;
    }


    public Parser getParser() {
        return parser;
    }

    public void setParser(String path, boolean toStem) {
        this.parser = new Parser(path, toStem);
    }
}
