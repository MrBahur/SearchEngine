package Model.Search;


import Model.File.Term;
import javafx.util.Pair;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Searcher {

    private boolean toStem;
    private QueryParser queryParser;
    private Map<String, Pair<Integer, Integer>> dictionary;//Term -> <amount in corpus Map , pointer to posting file>



    public Searcher(boolean toStem, String path, Map<String, Pair<Integer, Integer>> dictionary) {
        this.toStem = toStem;
        this.dictionary = dictionary;
        this.queryParser = new QueryParser(toStem, path, dictionary);
    }

    public void search(String s) throws IOException {
        Set<String> docs = new HashSet<>();
        queryParser.parse(s);
        Map<Term, Integer> phrases = queryParser.getQuery();
        for (Map.Entry<Term, Integer> entry : phrases.entrySet()) {
            int row = dictionary.get(entry.getKey().toString()).getValue();
            String postingFileName = getPostingFileName(entry.getKey());
            BufferedReader reader = new BufferedReader(new FileReader(((toStem) ? "S" : "") + "PostingFile" + "\\" + postingFileName));
            String sRow = "";
            for (int i = 0; i < row; i++) {
                sRow = reader.readLine();
            }
            String[] splitted = sRow.substring(sRow.indexOf("->")).split(";");
            for (String s1 : splitted) {
                docs.add(s1.substring(0, s1.indexOf("=")));
            }
        }
    }


    private String getPostingFileName(Term key) {
        char c = key.toString().charAt(0);
        if (c >= 'a' && c <= 'z') {
            return "" + c;
        } else if (c >= 'A' && c <= 'Z') {
            return c + "@";
        } else return "0";
    }

}
