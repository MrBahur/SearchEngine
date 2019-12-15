package Model.Parser;

import opennlp.tools.stemmer.PorterStemmer;

public class Stemmer {
    public static PorterStemmer p = new PorterStemmer();

    public static String stem(String s) {
        return p.stem(s.toLowerCase());
    }

    public static void main(String[] args) {
        System.out.println(Stemmer.stem("ASDASD"));
        System.out.println(Stemmer.stem("Stemmer"));
        System.out.println(Stemmer.stem("implementing"));
        System.out.println(Stemmer.stem("the"));
        System.out.println(Stemmer.stem("Porter"));
        System.out.println(Stemmer.stem("Stemming"));
        System.out.println(Stemmer.stem("Algorithm"));
    }
}
