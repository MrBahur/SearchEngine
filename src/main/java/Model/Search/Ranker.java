package Model.Search;

import Model.File.Term;
import javafx.util.Pair;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Ranker {
    private Map<String, Integer> docToNumOfTerms;
    private Map<String, Pair<Integer, Integer>> documents;// doc ID -> <max_tf,Number of unique words>
    private boolean toStem;

    public Ranker(boolean toStem) {
        this.toStem = toStem;
        docToNumOfTerms = new HashMap<>();
        documents = new HashMap<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(((toStem) ? "S" : "") + "PostingFile" + "\\" + "docsToNumOfTerms.txt"));
            String line = reader.readLine();
            do {
                String[] tmp = line.split("->");
                docToNumOfTerms.put(tmp[0], Integer.parseInt(tmp[1]));
                line = reader.readLine();
            } while (line != null);
            reader = new BufferedReader(new FileReader(((toStem) ? "S" : "") + "PostingFile" + "\\" + "DocumentsInfo.txt"));
            line = reader.readLine();
            do {
                String[] tmp1 = line.split("->");
                String[] tmp2 = tmp1[1].split("=");
                documents.put(tmp1[0], new Pair<>(Integer.parseInt(tmp2[0]), Integer.parseInt(tmp2[1])));
                line = reader.readLine();
            } while (line != null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public double getRank(Map<Term, Integer> query, String docID) {
        double rank = 0.0;
        int maxTf = getMaxTF(docID);
        double k = 1.2;
        double b = 0.75;
        double avgdl = 214;
        double numOfTerms = getNumOfTerms(docID);
        double numOfDocs = 472525;
        for (Map.Entry<Term, Integer> entry : query.entrySet()) {
            int numOfDocsForTerm = getNumOfDocs(entry.getKey().toString());
            double idf = Math.log((numOfDocs - numOfDocsForTerm + 0.5) / numOfDocsForTerm + 0.5);
            double BM25 = ((numOfTerms / maxTf) * (k + 1) / ((numOfTerms / maxTf) + k * (1 - b + b * (numOfTerms / avgdl))));
            rank += (idf * BM25);
        }
        return rank;
    }

    private double getNumOfTerms(String docID) {
        return docToNumOfTerms.get(docID);
    }

    private int getMaxTF(String docID) {
        return documents.get(docID).getKey();
    }

    private int getNumOfDocs(String term) {
        int numOfDocs = 0;
        try {

            String postingFileName = getPostingFileName(term);
            BufferedReader reader = new BufferedReader(new FileReader(((toStem) ? "S" : "") + "PostingFile" + "\\" + postingFileName));
            String line;
            do {
                line = reader.readLine();
                if (line == null) {
                    return 0;
                }
            } while (!line.startsWith(term));
            numOfDocs = line.split(";").length;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return numOfDocs;
    }

    private String getPostingFileName(String term) {
        char c = term.charAt(0);
        if (c >= 'a' && c <= 'z') {
            return "" + c+".txt";
        } else if (c >= 'A' && c <= 'Z') {
            return c + "@.txt";
        } else return "0.txt";
    }
}
