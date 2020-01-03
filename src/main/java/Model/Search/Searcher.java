package Model.Search;


import Model.File.Term;
import javafx.util.Pair;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Searcher {

    private boolean toStem;
    private QueryParser queryParser;
    private Map<String, Pair<Integer, Integer>> dictionary;//Term -> <amount in corpus Map , pointer to posting file>
    private Ranker ranker;


    public Searcher(boolean toStem, String path, Map<String, Pair<Integer, Integer>> dictionary) {
        this.toStem = toStem;
        this.dictionary = dictionary;
        this.queryParser = new QueryParser(toStem, path, dictionary);
        this.ranker = new Ranker(toStem);
    }


    public ArrayList<Pair<String, Double>> search(String s) {
        try {
            queryParser.parse(s);
            Map<Term, Integer> query = queryParser.getQuery();
            Set<String> docs = getDocsFromQuery(query);
            return rank(query, docs);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private ArrayList<Pair<String, Double>> rank(Map<Term, Integer> query, Set<String> unRankedDocs) {
        ArrayList<Pair<String, Double>> arrayList = new ArrayList<>();
        for (String docID : unRankedDocs) {
            Double rank = ranker.getRank(query, docID);
            arrayList.add(new Pair<>(docID, rank));
        }
        arrayList.sort(Comparator.comparingDouble(p -> -1 * p.getValue()));
        return arrayList;
    }

    private Set<String> getDocsFromQuery(Map<Term, Integer> query) throws IOException {
        Set<String> docs = new HashSet<>();
        for (Map.Entry<Term, Integer> entry : query.entrySet()) {
            int row = dictionary.get(entry.getKey().toString()).getValue();
            String postingFileName = getPostingFileName(entry.getKey());
            BufferedReader reader = new BufferedReader(new FileReader(((toStem) ? "S" : "") + "PostingFile" + "\\" + postingFileName));
            String sRow = "";
            for (int i = 0; i < row; i++) {
                sRow = reader.readLine();
            }
            String[] splitted = sRow.substring(sRow.indexOf("->") + 2).split(";");
            for (String s1 : splitted) {
                docs.add(s1.substring(0, s1.indexOf("=")));
            }
        }
        return docs;
    }


    private String getPostingFileName(Term key) {
        char c = key.toString().charAt(0);
        if (c >= 'a' && c <= 'z') {
            return "" + c + ".txt";
        } else if (c >= 'A' && c <= 'Z') {
            return c + "@.txt";
        } else return "0.txt";
    }

}
