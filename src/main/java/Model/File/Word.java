package Model.File;

import Model.Parser.Stemmer;

/**
 * the way we represent Word in emory
 */
public class Word extends Term {
    private String value;

    /**
     * Constructor for Word
     *
     * @param value the word
     */
    public Word(String value) {
        this.value = GarbageRemove.remove(value.toLowerCase());
    }

    /**
     * Constructor for Word
     *
     * @param value the word
     * @param stem  if need to stem or not
     */
    public Word(String value, boolean stem) {
        if (stem) {
            this.value = Stemmer.stem(value);
        } else {
            this.value = GarbageRemove.remove(value.toLowerCase());
        }
    }

    /**
     * return the Word as String
     *
     * @return the Name
     */
    @Override
    public String toString() {
        return value;
    }

    /**
     * java equals
     *
     * @param other other
     * @return return true if same Word, false otherwise
     */
    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Word)) {
            return false;
        }
        Word w = (Word) other;
        return this.value.equals(w.value);
    }

    /**
     * java hashcode for inserting to hash DS
     *
     * @return in represnting the name
     */
    @Override
    public int hashCode() {
        return value.hashCode();
    }

    /**
     * return the word as string
     *
     * @return ...
     */
    public String getValue() {
        return value;
    }

    /**
     * check if the Word is good TBD add more parameters for it, currently only checking length
     *
     * @return true if good, false otherwise
     */
    public boolean isGood() {
        return value.length() > 1;
    }

    /**
     * main test for Word
     *
     * @param args none
     */
    public static void main(String[] args) {
        Word w = new Word("Hello");
        System.out.println(w.toString());
    }


}
