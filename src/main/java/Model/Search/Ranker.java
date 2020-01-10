package Model.Search;

import Model.File.Name;
import Model.File.Phrase;
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
    private Map<String, Double> termToIDF;// Term -> IDF
    private boolean toStem;

    public Ranker(boolean toStem) {
        this.toStem = toStem;
        docToNumOfTerms = new HashMap<>();
        documents = new HashMap<>();
        termToIDF = new HashMap<>();
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
            reader = new BufferedReader(new FileReader(((toStem) ? "S" : "") + "PostingFile" + "\\" + "termToIDF.txt"));
            line = reader.readLine();
            do {
                String[] tmp = line.split("->");
                termToIDF.put(tmp[0], Double.parseDouble(tmp[1]));
                line = reader.readLine();
            } while (line != null);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private double getNumOfTerms(String docID) {
        return docToNumOfTerms.get(docID);
    }

    private int getMaxTF(String docID) {
        return documents.get(docID).getKey();
    }

    public double getRank(String docID, int numOfATimesInDoc, Term term) {
        int isPhrase = 0;
        if (term instanceof Phrase || term instanceof Name) {
            isPhrase = 1;
        }
        int maxTf = 1;
        double k = 0.01;
        double b = 0.1;
        double avgDocLength = 250;
        double numOfTerms = getNumOfTerms(docID);
        double idf = termToIDF.get(term.toString());
        double BM25 = ((numOfATimesInDoc * 1.0 / maxTf * 1.0) * (k + 1) / ((numOfATimesInDoc * 1.0 / maxTf * 1.0) + k *
                (1 - b + b * (numOfTerms / avgDocLength))));
        BM25 = 0.85 * BM25 + 0.15 * isPhrase;
        return idf * BM25;
    }
}
