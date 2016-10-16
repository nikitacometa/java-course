package ru.spbau.gorokhov;

import java.util.ArrayList;

/**
 * Created by wackloner on 25.09.16 in 12:43.
 */
public interface TrieNode {

    /**
     * Checks if there's an edge by specified symbol in the trie.
     *
     * @param symbol specified symbol
     * @return <tt>true</tt> if there's an edge by specified symbol in trie
     */
    boolean hasNext(char symbol);

    /**
     * Removes edge with specified symbol from the trie.
     *
     * @param symbol specified symbol
     */
    void removeNext(char symbol);

    /**
     * Follows edge with specified symbol. If there's no such edge, creates it and then follows.
     * Returns next node.
     *
     * @param symbol specified symbol
     * @return next node by specified edge
     */
    TrieNode goNext(char symbol);

    /**
     * Marks node as terminal.
     */
    void setAsTerminal();

    /**
     * Marks node as not terminal.
     */
    void setAsNotTerminal();

    /**
     * Checks if node is terminal or not.
     *
     * @return <tt>true</tt> if node is terminal.
     */
    boolean isTerminal();

    /**
     * Increases number of suffixes following prefix specified by this node.
     */
    void increaseSuffixesCount();

    /**
     * Decreases number of suffixes following prefix specified by this node.
     */
    void decreaseSuffixesCount();

    /**
     * @return number of suffixes following prefix specified by this node
     */
    int suffixesCount();

    /**
     * @return <tt>true</tt> if there're no suffixes following prefix specified by this node
     */
    boolean hasNoSuffixes();

    /**
     * @return list containing all strings containing in this node
     */
    ArrayList<String> getElementsList();
}
