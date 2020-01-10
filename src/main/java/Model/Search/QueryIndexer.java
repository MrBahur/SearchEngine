package Model.Search;

import Model.File.Name;
import Model.File.Phrase;
import Model.File.Term;
import Model.File.Word;
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
        } else {
            if (term instanceof Name) {
                if (dictionary.containsKey(term.toString().toLowerCase())) {
                    Word w = new Word(term.toString().toLowerCase());
                    if (!query.containsKey(w)) {
                        query.put(w, 1);
                    } else {
                        query.replace(w, query.get(w) + 1);
                    }
                }
            }
            if (term instanceof Word) {
                if (dictionary.containsKey(term.toString().toUpperCase())) {
                    Name n = new Name(term.toString().toUpperCase(), false);
                    if (!query.containsKey(n)) {
                        query.put(n, 1);
                    } else {
                        query.replace(n, query.get(n) + 1);
                    }
                }
            }
            if (term instanceof Phrase) {
                for (String s :term.toString().split("-")) {
                    Word w = new Word(s);
                    addTerm(w);
                }
            }
        }
    }

    public void clean() {
        query = new HashMap<>();
    }
}
