package Model.File;

public class Range extends Term {
    private String left;
    private String middle;
    private String right;

    public Range(String left, String middle, String right) {
        this.left = left;
        this.middle = middle;
        this.right = right;
    }

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

    @Override
    public int hashCode() {
        if (right != null) {
            return left.hashCode() * middle.hashCode() * right.hashCode();
        } else {
            return left.hashCode() * middle.hashCode();
        }
    }

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
}
