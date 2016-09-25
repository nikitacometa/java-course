package ru.spbau.gorokhov;

import java.util.HashMap;
import java.util.Set;

/**
 * Created by wackloner on 25.09.16 in 12:42.
 */
public class HashMapTrieNode extends CollectionTrieNode {
    protected static final String COLLECTION_NAME = "HashMap";

    private HashMap<Character, HashMapTrieNode> next;

    public HashMapTrieNode() {
        next = new HashMap<Character, HashMapTrieNode>(0);
    }

    @Override
    public void removeNext(char symbol) {
        next.remove(symbol);
    }

    @Override
    protected void setNext(char symbol, CollectionTrieNode node) {
        next.put(symbol, (HashMapTrieNode) node);
    }

    @Override
    protected HashMapTrieNode createNode() {
        return new HashMapTrieNode();
    }

    @Override
    protected HashMapTrieNode getNext(char symbol) {
        return next.get(symbol);
    }

    @Override
    protected Set<Character> getSetOfNextSymbols() {
        return next.keySet();
    }
}
