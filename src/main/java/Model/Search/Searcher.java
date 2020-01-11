package Model.Search;


import Model.File.Term;
import javafx.util.Pair;

import java.io.*;
import java.util.*;

public class Searcher {

    private boolean toStem;
    private QueryParser queryParser;
    private Map<String, Pair<Integer, Integer>> dictionary;//Term -> <amount in corpus Map , pointer to posting file>
    private Ranker ranker;
    private Map<String, LinkedList<Pair<String, Integer>>> docsToPhrases;
    public static SemanticSearcher semanticSearcher = new SemanticSearcher();
    private String path;


    public Searcher(boolean toStem, String path, Map<String, Pair<Integer, Integer>> dictionary) {
        this.toStem = toStem;
        this.dictionary = dictionary;
        this.queryParser = new QueryParser(toStem, path, dictionary);
        this.ranker = new Ranker(toStem, path);
        this.docsToPhrases = new HashMap<>();
        this.path = path;
        getDocsToPhraseFromDisk(path);
    }

    public LinkedList<Pair<String, Integer>> searchForPhrases(String docID) {
        return docsToPhrases.getOrDefault(docID, null);
    }

    private void getDocsToPhraseFromDisk(String path) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(path + "\\DocsToPhrases.txt"));
            String line = reader.readLine();
            while (line != null) {
                String[] splitted = line.split("->");
                docsToPhrases.put(splitted[0], new LinkedList<>());
                if (splitted.length > 1) {
                    for (String s : splitted[1].split(";")) {
                        docsToPhrases.get(splitted[0]).add(new Pair<>(s.split("=")[0], Integer.parseInt(s.split("=")[1])));
                    }
                }
                line = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Pair<Integer, ArrayList<Pair<String, Double>>>> search(File queryFile, boolean semantic) {
        ArrayList<Pair<Integer, ArrayList<Pair<String, Double>>>> results = new ArrayList<>();
        QueryReadFile queries = new QueryReadFile(queryFile.getPath());
        for (MyQuery q : queries) {
            System.out.println(q.getQueryNum());
            System.out.println("_____________________");
            long start = System.currentTimeMillis();
            String toAdd = "";
            if (semantic) {
                StringBuilder stringBuilder = new StringBuilder();
                for (String word : q.getQuery().split(" ")) {
                    try {
                        for (String s : semanticSearcher.getTerms(word.toLowerCase(), 5)) {
                            stringBuilder.append(s).append(" ");
                        }
                    } catch (com.medallia.word2vec.Searcher.UnknownWordException e) {
                        System.out.println(word);
                    }
                }
                toAdd = stringBuilder.toString();
            }
            results.add(new Pair<>(q.getQueryNum(), search(q.getQuery() + q.getQuery() + q.getQuery() + q.getQuery() +
                    q.getQuery() + q.getQuery() + q.getQuery() + q.getQuery() + q.getQuery() + q.getQuery() + q.getQuery()
                    + q.getQuery() + q.getQuery() + q.getQuery() + q.getQuery() + q.getQuery() + q.getQuery() + q.getQuery()
                    + q.getQuery() + q.getQuery() + q.getQuery() + q.getQuery() + q.getQuery() + q.getQuery() + q.getQuery()
                    + q.getQuery() + q.getQuery() + q.getQuery() + q.getQuery() + q.getQuery() + q.getQuery() + q.getDesc()
                    + toAdd)));
            long finish = System.currentTimeMillis();
            System.out.println("time in S: " + ((finish - start) / 1000.0));
        }
        return results;
    }

    public ArrayList<Pair<String, Double>> search(String s) {
        Map<String, Double> docsToRankMap = new HashMap<>();
        try {
            queryParser.parse(s);
            Map<Term, Integer> query = queryParser.getQuery();
            for (Map.Entry<Term, Integer> entry : query.entrySet()) {
                rank(entry, docsToRankMap);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        ArrayList<Map.Entry<String, Double>> tmpArrayList = new ArrayList<>(docsToRankMap.entrySet());
        tmpArrayList.sort(Comparator.comparingDouble(p -> -1 * p.getValue()));
        ArrayList<Pair<String, Double>> docsToReturn = new ArrayList<>();
        for (int i = 0; i < 50 && i < tmpArrayList.size(); i++) {
            docsToReturn.add(new Pair<>(tmpArrayList.get(i).getKey(), tmpArrayList.get(i).getValue()));
        }
        return docsToReturn;
    }

    private void rank(Map.Entry<Term, Integer> entry, Map<String, Double> docsRanks) throws IOException {
        List<Pair<String, Integer>> lineInPostingFile = new LinkedList<>();
        String postingFileName = getPostingFileName(entry.getKey());
        BufferedReader reader = new BufferedReader(new FileReader(path + "\\" + postingFileName));
        int row = dictionary.get(entry.getKey().toString()).getValue();
        String sRow = "";
        for (int i = 0; i <= row; i++) {
            sRow = reader.readLine();
        }
        String[] docs = sRow.split("->")[1].split(";");
        for (String s : docs) {
            String[] tmp = s.split("=");
            lineInPostingFile.add(new Pair<>(tmp[0], Integer.parseInt(tmp[1])));
        }
        for (Pair<String, Integer> p : lineInPostingFile) {
            double rank = ranker.getRank(p.getKey(), p.getValue(), entry.getKey());
            rank *= Math.sqrt(entry.getValue());
            if (!docsRanks.containsKey(p.getKey())) {
                docsRanks.put(p.getKey(), rank);
            } else {
                double oldRank = docsRanks.get(p.getKey());
                docsRanks.replace(p.getKey(), oldRank + rank);
            }
        }
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
