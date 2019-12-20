package Model.File;

import Model.Parser.Stemmer;

/**
 * the way we represent Name in memory
 */
public class Name extends Term {
    private String value;

    /**
     * constructor
     *
     * @param value  the name
     * @param toStem if need or not to stem
     */
    public Name(String value, boolean toStem) {
        if (toStem) {
            this.value = Stemmer.stem(value).toUpperCase();
        } else {
            this.value = GarbageRemove.remove(value.toUpperCase());
        }
    }

    /**
     * return the Name as String
     *
     * @return the Name
     */
    @Override
    public String toString() {
        return value;
    }

    /**
     * java equals
     *
     * @param other other
     * @return return true if same name, false otherwise
     */
    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Name)) {
            return false;
        }
        Name n = (Name) other;
        return this.value.equals(n.value);
    }

    /**
     * java hashcode for inserting to hash DS
     *
     * @return in represnting the name
     */
    @Override
    public int hashCode() {
        return value.hashCode();
    }

    /**
     * basicly toString just with other name
     *
     * @return the name
     */
    public String getValue() {
        return value;
    }

    /**
     * chech if the Name is good TBD add more parameters for it, currently only checking length
     *
     * @return true if good, false otherwise
     */
    public boolean isGood() {
        return this.value.length() > 1;
    }

    /**
     * tests fr Name class
     *
     * @param args none
     */
    public static void main(String[] args) {
        Name n = new Name("Shahar", false);
        System.out.println(n.toString());
    }
}
