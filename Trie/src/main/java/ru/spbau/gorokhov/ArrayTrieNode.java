package ru.spbau.gorokhov;

import java.util.ArrayList;

/**
 * Created by wackloner on 25.09.16 in 17:59.
 */
public class ArrayTrieNode extends SimpleTrieNode {
    protected static final String COLLECTION_NAME = "Array";
    private static final int CHARACTERS = 256;

    private ArrayTrieNode[] next;

    public ArrayTrieNode() {
        next = new ArrayTrieNode[CHARACTERS];
    }

    @Override
    public void removeNext(char symbol) {
        next[symbol] = null;
    }

    @Override
    protected ArrayTrieNode createNode() {
        return new ArrayTrieNode();
    }

    @Override
    protected void setNext(char symbol, SimpleTrieNode node) {
        next[symbol] = (ArrayTrieNode) node;
    }

    @Override
    protected ArrayTrieNode getNext(char symbol) {
        return next[symbol];
    }

    @Override
    protected ArrayList<Character> getSetOfNextSymbols() {
        ArrayList<Character> result = new ArrayList<Character>();
        for (char symbol = 0; symbol < CHARACTERS; symbol++) {
            if (hasNext(symbol)) {
                result.add(symbol);
            }
        }
        return result;
    }
}
