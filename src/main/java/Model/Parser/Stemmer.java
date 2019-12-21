package Model.Parser;

import opennlp.tools.stemmer.PorterStemmer;

/**
 * a Stemmer we are using
 */
public class Stemmer {
    private static PorterStemmer p = new PorterStemmer();

    /**
     * static function that stem word for us
     *
     * @param s the word ed need to stem
     * @return stemmed word in lower case with only a-zA-Z letters
     */
    public static String stem(String s) {
        return p.stem(s.replaceAll("[^a-zA-Z]", "").toLowerCase());
    }

    public static void main(String[] args) {
//        System.out.println(Stemmer.stem("ASDASD"));
//        System.out.println(Stemmer.stem("Stemmer"));
//        System.out.println(Stemmer.stem("implementing"));
//        System.out.println(Stemmer.stem("the"));
//        System.out.println(Stemmer.stem("Porter"));
//        System.out.println(Stemmer.stem("Stemming"));
//        System.out.println(Stemmer.stem("Algorithm"));
        String sentence = "Example: Do you really think it is weakness that yields to temptation? I tell you that there are terrible " +
                "temptations which it requires strength, strength and courage to yield to ~ Oscar Wilde";
        for (String s : sentence.split(" ")) {
            System.out.print(s + " -> ");
            System.out.println(stem(s));
        }
    }
}