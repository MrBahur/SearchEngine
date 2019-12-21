package Model.File;

/**
 * the way we represent Term in memory
 */
public class Phrase extends Term {
    private String value;

    /**
     * constructor for phrases W1-W2
     *
     * @param value1 W1
     * @param value2 W2
     */
    public Phrase(String value1, String value2) {
        this.value = value1 + "-" + value2;
    }

    /**
     * constructor for phrases W1-W2-W3
     *
     * @param value1 W1
     * @param value2 W2
     * @param value3 W3
     */
    public Phrase(String value1, String value2, String value3) {
        this.value = value1 + "-" + value2 + "-" + value3;
    }

    /**
     * constructor for phrases W1-W2-W3-W4
     *
     * @param s  W1
     * @param s1 W2
     * @param s2 W3
     * @param s3 W4
     */
    public Phrase(String s, String s1, String s2, String s3) {
        value = (s + "-" + s1 + ((s2 != null) ? "-" + s2 : "") + ((s3 != null) ? "-" + s3 : ""));
        value = GarbageRemove.removeWithoutFew(value);
    }

    /**
     * java toString
     *
     * @return String representing a phrase
     */
    @Override
    public String toString() {
        return value;
    }

    /**
     * java equals
     *
     * @param other other object we want to check if the same as Phrase
     * @return true if both objects are Phrases that are the same
     */
    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Phrase)) {
            return false;
        }
        return value.equals(((Phrase) other).value);
    }

    /**
     * java hashCode
     *
     * @return int representing the Phrase
     */
    @Override
    public int hashCode() {
        return value.hashCode();
    }

    /**
     * check if the phrase is valid
     * might add allot of changes to this function in the feature
     *
     * @return boolean if the phrase satisfy our condition for being "good"
     */
    public boolean isGood() {
        return !value.equals("");
    }
}
