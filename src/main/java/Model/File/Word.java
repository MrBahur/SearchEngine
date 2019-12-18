package Model.File;

public class Word extends Term {
    private String value;

    public Word(String value) {
        this.value = GarbageRemove.remove(value.toLowerCase());
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Word)) {
            return false;
        }
        Word w = (Word) other;
        return this.value.equals(w.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    public String getValue() {
        return value;
    }

    public boolean isGood() {
        return value.length() > 2;
    }

    public static void main(String[] args) {
        Word w = new Word("Hello");
        System.out.println(w.toString());
    }


}
