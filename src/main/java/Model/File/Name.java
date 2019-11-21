package Model.File;

public class Name extends Phrase {
    private String value;

    public Name(String value) {
        this.value = value.toUpperCase();
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
        return 0;
    }

    public String getValue() {
        return value;
    }

    public static void main(String[] args) {
        Name n = new Name("Shahar");
        System.out.println(n.toString());
    }

}
