package Model.File;

public class Word extends Phrase {
    private String value;

    public Word(String value) {
        this.value = value.toLowerCase();
    }

    @Override
    public String toString() {
        return null;
    }

    @Override
    public boolean equals(Object other) {
        return false;
    }

    @Override
    public int hashCode() {
        return 0;
    }

    public String getValue() {
        return value;
    }
}
