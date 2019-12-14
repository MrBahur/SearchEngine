package Model.File;

public class Phrase extends Term {
    private String value;

    public Phrase(String value1, String value2) {
        this.value = value1 + " " + value2;
    }

    public Phrase(String value1, String value2, String value3) {
        this.value = value1 + " " + value2 + " " + value3;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Phrase)) {
            return false;
        }
        return value.equals(((Phrase) other).value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
