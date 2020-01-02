package Model.Search;

import Model.File.Term;
import javafx.util.Pair;

import java.util.HashMap;
import java.util.Map;

public class QueryIndexer {
    private Map<String, Pair<Integer, Integer>> dictionary;//Term -> <amount in corpus Map , pointer to posting file>
    private Map<Term, Integer> query;

    public QueryIndexer(Map<String, Pair<Integer, Integer>> dictionary) {
        this.dictionary = dictionary;
    }

    public Map<Term, Integer> getQuery() {
        return query;
    }

    public void addTerm(Term term) {
        if (dictionary.containsKey(term.toString())) {
            if (!query.containsKey(term)) {
                query.put(term, 1);
            } else {
                query.replace(term, query.get(term) + 1);
            }
        }
    }

    public void clean() {
        query = new HashMap<>();
    }
}
