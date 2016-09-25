package ru.spbau.gorokhov;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by wackloner on 25.09.16 in 12:36.
 */
public abstract class NotCompressedTrie implements Trie, StreamSerializable {
    protected NotCompressedTrieNode root;

    public boolean add(String element) {
        if (contains(element)) {
            return false;
        }
        NotCompressedTrieNode pointer = root;
        pointer.increaseSuffixesCount();
        for (char symbol : element.toCharArray()) {
            pointer = pointer.goSymbol(symbol);
            pointer.increaseSuffixesCount();
        }
        pointer.setAsTerminal();
        return true;
    }

    public boolean contains(String element) {
        NotCompressedTrieNode pointer = root;
        for (char symbol : element.toCharArray()) {
            if (!pointer.hasSymbol(symbol)) {
                return false;
            }
            pointer = pointer.goSymbol(symbol);
        }
        return pointer.isTerminal();
    }

    public boolean remove(String element) {
        if (!contains(element)) {
            return false;
        }
        NotCompressedTrieNode pointer = root;
        pointer.decreaseSuffixesCount();
        for (char symbol : element.toCharArray()) {
            NotCompressedTrieNode nextNode = pointer.goSymbol(symbol);
            nextNode.decreaseSuffixesCount();
            if (nextNode.hasNoSuffixes()) {
                pointer.removeSymbol(symbol);
            }
            pointer = nextNode;
        }
        pointer.setAsNotTerminal();
        return true;
    }

    public int size() {
        return root.suffixesCount();
    }

    public int howManyStartsWithPrefix(String prefix) {
        NotCompressedTrieNode pointer = root;
        for (char symbol : prefix.toCharArray()) {
            if (!pointer.hasSymbol(symbol)) {
                return 0;
            }
            pointer = pointer.goSymbol(symbol);
        }
        return pointer.suffixesCount();
    }

    public void serialize(OutputStream out) throws IOException {
        root.serialize(out);
    }

    public void deserialize(InputStream in) throws IOException {
        root.deserialize(in);
    }

    public String toString() {
        return root.toString();
    }
}
