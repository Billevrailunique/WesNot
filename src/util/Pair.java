package util;

import java.util.Objects;

/**
 * A generic container that holds two related values of potentially different
 * types.
 * <p>
 * This class provides a simple way to associate two objects together without
 * creating
 * a custom class. It is commonly used to:
 * <ul>
 * <li>Return multiple values from methods</li>
 * <li>Store coordinate pairs</li>
 * <li>Maintain key-value associations</li>
 * <li>Group related data temporarily</li>
 * </ul>
 * </p>
 * 
 * @param <T> The type of the first element in the pair
 * @param <S> The type of the second element in the pair
 */
public class Pair<T, S> {
    private T first; // The first element in the pair
    private S second; // The second element in the pair

    /**
     * Constructs a new Pair with the specified elements.
     *
     * @param first  The first element to store
     * @param second The second element to store
     */
    public Pair(T first, S second) {
        this.first = first;
        this.second = second;
    }

    /**
     * Gets the first element of the pair.
     *
     * @return The first element
     */
    public T first() {
        return first;
    }

    /**
     * Gets the second element of the pair.
     *
     * @return The second element
     */
    public S second() {
        return second;
    }

    /**
     * Sets the first element of the pair.
     *
     * @param first The new value for the first element
     */
    public void setFirst(T first) {
        this.first = first;
    }

    /**
     * Sets the second element of the pair.
     *
     * @param second The new value for the second element
     */
    public void setSecond(S second) {
        this.second = second;
    }

    /**
     * Creates a new Pair with the same elements as the given model.
     * <p>
     * This performs a shallow copy of the elements.
     * </p>
     *
     * @param model The Pair to copy
     * @return A new Pair containing copies of the model's elements
     */
    public Pair<T, S> copie(Pair<T, S> model) {
        return new Pair<>(model.first(), model.second());
    }

    /**
     * Returns a string representation of the pair.
     * <p>
     * Format: "(first,second)"
     * </p>
     *
     * @return A string representation of the pair
     */
    @Override
    public String toString() {
        return "(" + this.first + "," + this.second + ")";
    }

    /**
     * for a better HashMap
     */
    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }

    /**
     * Compares this pair with another object for equality.
     * <p>
     * Two pairs are considered equal if both their first and second elements
     * are equal according to their respective equals() methods.
     * </p>
     *
     * @param obj The object to compare with
     * @return true if the objects are equal pairs, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;

        Pair<?, ?> pair = (Pair<?, ?>) obj;
        return this.first().equals(pair.first()) && this.second().equals(pair.second());
    }
}