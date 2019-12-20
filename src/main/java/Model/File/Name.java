package Model.File;

import Model.Parser.Stemmer;

public class Name extends Term {
    private String value;

    public Name(String value, boolean toStem) {
        if (toStem) {
            this.value = Stemmer.stem(value).toUpperCase();
        } else {
            this.value = GarbageRemove.remove(value.toUpperCase());
        }
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Name)) {
            return false;
        }
        Name n = (Name) other;
        return this.value.equals(n.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    public String getValue() {
        return value;
    }

    public static void main(String[] args) {
        Name n = new Name("Shahar",false);
        System.out.println(n.toString());
    }

    public boolean isGood() {
        return this.value.length() > 1;
    }
}
