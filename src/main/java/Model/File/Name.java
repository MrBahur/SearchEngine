package Model.File;

public class Name extends Phrase {
    private String value;

    public Name(String value) {
        this.value = value.toUpperCase();
    }

    public Name(Word word){
        value = word.getValue().toUpperCase();
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
