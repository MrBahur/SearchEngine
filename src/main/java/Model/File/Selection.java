package Model.File;

public class Selection extends Term {
    private String first;
    private String second;

    public Selection(String first, String second) {
        this.first = GarbageRemove.remove(first);
        this.second = GarbageRemove.remove(second);
    }

    @Override
    public String toString() {
        return first + "/" + second;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Selection) {
            Selection otherSelect = (Selection) other;
            return this.first.equals(otherSelect.first) && this.second.equals(otherSelect.second);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return (first.hashCode() * second.hashCode());
    }

    public static void main(String[] args) {
        Selection s = new Selection("yes", "no");
        System.out.println(s.toString());
    }

    public boolean isGood() {
        return this.first.length() >= 1 && this.second.length() >= 2 || this.first.length() >= 2 && this.second.length() >= 1;
    }
}
