package Model.Search;

import com.medallia.word2vec.Word2VecModel;
import com.medallia.word2vec.Searcher;
import com.medallia.word2vec.Searcher.Match;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SemanticSearcher {
    private Searcher semanticSearcher;

    public SemanticSearcher() {
        try {
            File f = new File("word2vec.c.output.model.txt");
            Word2VecModel model = Word2VecModel.fromTextFile(f);
            semanticSearcher = model.forSearch();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<String> getTerms(String term, int number) throws Searcher.UnknownWordException {
        ArrayList<String> toReturn = new ArrayList<String>();

        List<Match> matches = semanticSearcher.getMatches(term, number);
        for (Match match : matches) {
            toReturn.add(match.match());
        }
        return toReturn;
    }

}
