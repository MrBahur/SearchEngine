package Model.File;

/**
 * Abstract class that helps us with polymorphism cuz of all of the types of the terms in the corpus
 * will help us to extend the Engine if needed in an easy way
 */
public abstract class Term {
    /**
     * java to string
     *
     * @return sting
     */
    @Override
    public abstract String toString();

    /**
     * java equals
     *
     * @param other Object
     * @return true if Both object are identical
     */
    @Override
    public abstract boolean equals(Object other);

    /**
     * java hashcode
     *
     * @return int that represent the Term for Hash DS
     */
    @Override
    public abstract int hashCode();
}
