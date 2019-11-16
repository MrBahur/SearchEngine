package Model.File;

public class Number extends Phrase {
    private double value;
    private String sign;

    @Override
    public String toString() {
        return null;
    }

    @Override
    public boolean equals(Object other) {
        if(!(other instanceof Number)){
            return false;
        }
        Number x = (Number)other;
        return this.sign.equals(x.sign) && this.value == x.value;
    }

    @Override
    public int hashCode() {
        return 0;
    }
}
