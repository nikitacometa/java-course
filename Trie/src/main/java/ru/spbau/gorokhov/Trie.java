package ru.spbau.gorokhov;

/**
 * Created by wackloner on 25.09.16 in 12:33.
 */
public interface Trie {

    /**
     * Adds string to the trie.
     * Expected complexity: O(|element|)
     *
     * @param element adding string
     * @return <tt>true</tt> if this set did not already contain the specified
     *         element
     */
    boolean add(String element);

    /**
     * Expected complexity: O(|element|)
     *
     * @param element specified string
     * @return <tt>true</tt> if specified string contains in the trie
     */
    boolean contains(String element);

    /**
     * Removes string from the trie.
     * Expected complexity: O(|element|)
     *
     * @param element removing string
     * @return <tt>true</tt> if this set contained the specified element
     */
    boolean remove(String element);

    /**
     * Expected complexity: O(1)
     *
     * @return number of strings stored in the trie
     */
    int size();

    /**
     * Expected complexity: O(|prefix|)
     *
     * @param prefix specified prefix
     * @return number of suffixes following specified prefix
     */
    int howManyStartsWithPrefix(String prefix);
}