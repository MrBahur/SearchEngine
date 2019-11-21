package Model.File;

public class Word extends Phrase {
    private String value;

    public Word(String value) {
        this.value = value.toLowerCase();
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
        return 0;
    }

    public String getValue() {
        return value;
    }

    public static void main(String[] args) {
       Word w = new Word("Hello");
        System.out.println(w.toString());
    }

}
