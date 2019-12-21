package Model.File;

/**
 * they way we represent Range in memory
 */
public class Range extends Term {
    private String left;
    private String middle;
    private String right;

    /**
     * Constructor for Range looking like thar : A-B-C
     *
     * @param left   A
     * @param middle B
     * @param right  B
     */
    public Range(String left, String middle, String right) {
        this.left = GarbageRemove.removeWithoutNumbers(left);
        this.middle = GarbageRemove.removeWithoutNumbers(middle);
        this.right = GarbageRemove.removeWithoutNumbers(right);
    }

    /**
     * java to String
     *
     * @return string representing Range
     */
    @Override
    public String toString() {
        String str = "";
        if (right == null) {
            str = left + "-" + middle;
        } else {
            str = left + "-" + middle + "-" + right;
        }
        return str;
    }

    /**
     * java equals
     *
     * @param other other object we want to check if the same as Range
     * @return true if both objects are Ranges that are the same
     */
    @Override
    public boolean equals(Object other) {
        if (other instanceof Range) {
            Range otherRange = (Range) other;
            if (this.right == null && otherRange.right == null) {
                return this.left.equals(otherRange.left) && this.middle.equals(otherRange.middle);
            } else if (this.right == null || otherRange.right == null) {
                return false;
            } else {
                return this.left.equals(otherRange.left) && this.middle.equals(otherRange.middle) && this.right.equals(otherRange.right);
            }
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
        if (right != null) {
            return left.hashCode() * middle.hashCode() * right.hashCode();
        } else {
            return left.hashCode() * middle.hashCode();
        }
    }

    /**
     * main test for Range
     *
     * @param args none
     */
    public static void main(String[] args) {
        Range r1 = new Range("Value", null, "added");
        System.out.println(r1.toString());
        Range r2 = new Range("step", "by", "step");
        System.out.println(r2.toString());
        Range r3 = new Range("10", null, "part");
        System.out.println(r3.toString());
        Range r4 = new Range("6", null, "7");
        System.out.println(r4.toString());
    }

    /**
     * check if the Range is valid
     * might add allot of changes to this function in the feature
     *
     * @return boolean if the phrase satisfy our condition for being "good"
     */
    public boolean isGood() {
        if (right == null) {
            return left.length() >= 1 && middle.length() >= 1;
        } else {
            return !(left.equals("") || middle.equals("") || right.equals(""));
        }
    }
}
