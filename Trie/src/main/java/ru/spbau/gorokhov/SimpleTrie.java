package ru.spbau.gorokhov;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 * Created by wackloner on 25.09.16 in 12:36.
 */
public abstract class SimpleTrie implements Trie, StreamSerializable {
    protected SimpleTrieNode root;

    public boolean add(String element) {
        if (contains(element)) {
            return false;
        }
        SimpleTrieNode pointer = root;
        pointer.increaseSuffixesCount();
        for (char symbol : element.toCharArray()) {
            pointer = pointer.goNext(symbol);
            pointer.increaseSuffixesCount();
        }
        pointer.setAsTerminal();
        return true;
    }

    public boolean contains(String element) {
        SimpleTrieNode pointer = root;
        for (char symbol : element.toCharArray()) {
            if (!pointer.hasNext(symbol)) {
                return false;
            }
            System.out.println(symbol);
            pointer = pointer.goNext(symbol);
        }
        return pointer.isTerminal();
    }

    public boolean remove(String element) {
        if (!contains(element)) {
            return false;
        }
        SimpleTrieNode pointer = root;
        pointer.decreaseSuffixesCount();
        for (char symbol : element.toCharArray()) {
            SimpleTrieNode nextNode = pointer.goNext(symbol);
            nextNode.decreaseSuffixesCount();
            if (nextNode.hasNoSuffixes()) {
                pointer.removeNext(symbol);
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
        SimpleTrieNode pointer = root;
        for (char symbol : prefix.toCharArray()) {
            if (!pointer.hasNext(symbol)) {
                return 0;
            }
            pointer = pointer.goNext(symbol);
        }
        return pointer.suffixesCount();
    }

    public void serialize(OutputStream out) throws IOException {
        root.serialize(out);
    }

    public void deserialize(InputStream in) throws IOException {
        root.deserialize(in);
    }

    public ArrayList<String> getElementsList() {
        return root.getElementsList();
    }

    public String toString() {
        return root.toString();
    }
}
