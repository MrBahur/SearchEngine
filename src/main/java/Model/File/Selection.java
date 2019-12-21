package Model.File;

/**
 * a new data that we collected, this class is the representation of it in the memory
 */
public class Selection extends Term {
    private String first;
    private String second;

    /**
     * constructor for selection from the type: A/B
     *
     * @param first  A
     * @param second B
     */
    public Selection(String first, String second) {
        this.first = GarbageRemove.remove(first);
        this.second = GarbageRemove.remove(second);
    }

    /**
     * java to String
     *
     * @return string representing Selection
     */
    @Override
    public String toString() {
        return first + "/" + second;
    }

    /**
     * java equals
     *
     * @param other other object we want to check if the same as Selection
     * @return true if both objects are Selections that are the same
     */
    @Override
    public boolean equals(Object other) {
        if (other instanceof Selection) {
            Selection otherSelect = (Selection) other;
            return this.first.equals(otherSelect.first) && this.second.equals(otherSelect.second);
        }
        return false;
    }

    /**
     * java hashCode
     *
     * @return int representing the Range
     */
    @Override
    public int hashCode() {
        return (first.hashCode() * second.hashCode());
    }

    /**
     * main test for selection
     *
     * @param args none
     */
    public static void main(String[] args) {
        Selection s = new Selection("yes", "no");
        System.out.println(s.toString());
    }

    /**
     * check if the Selection is valid
     * might add allot of changes to this function in the feature
     *
     * @return boolean if the phrase satisfy our condition for being "good"
     */
    public boolean isGood() {
        return this.first.length() >= 2 && this.second.length() >= 2;
    }
}
