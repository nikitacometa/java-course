package ru.spbau.gorokhov;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by wackloner on 25.09.16 in 16:26.
 */
public abstract class CollectionTrieNode implements TrieNode, StreamSerializable {
    protected static final String COLLECTION_NAME = "Collection";

    private boolean isTerminal;
    private int suffixesCount;

    public boolean hasNext(char symbol) {
        return getNext(symbol) != null;
    }

    public CollectionTrieNode goNext(char symbol) {
        CollectionTrieNode nextNode;
        if (hasNext(symbol)) {
            nextNode = getNext(symbol);
        } else {
            nextNode = createNode();
            setNext(symbol, nextNode);
        }
        return nextNode;
    }

    public abstract void removeNext(char symbol);

    public void setAsTerminal() {
        isTerminal = true;
    }

    public void setAsNotTerminal() {
        isTerminal = false;
    }

    public boolean isTerminal() {
        return isTerminal;
    }

    public void increaseSuffixesCount() {
        suffixesCount++;
    }

    public void decreaseSuffixesCount() {
        suffixesCount--;
    }

    public int suffixesCount() {
        return suffixesCount;
    }

    public boolean hasNoSuffixes() {
        return suffixesCount == 0;
    }

    public ArrayList<String> getElementsList() {
        ArrayList<String> result = new ArrayList<String>();
        write(result, "");
        return result;
    }

    public String toString() {
        return getElementsList().toString();
    }

    public void serialize(OutputStream out) throws IOException {
        out.write(isTerminal ? 1 : 0);
        for (char symbol : getSetOfNextSymbols()) {
            out.write(1);
            out.write(symbol);
            getNext(symbol).serialize(out);
            out.write(0);
        }
    }

    public void deserialize(InputStream in) throws IOException {
        int terminality = in.read();
        if (terminality == 0) {
            isTerminal = false;
        } else if (terminality == 1) {
            isTerminal = true;
        } else {
            throw new IOException("Invalid " + COLLECTION_NAME + "TrieNode format\n");
        }
        while (true) {
            int cur = in.read();
            if (cur == -1) {
                break;
            } if (cur == 0) {
                break;
            } else if (cur == 1) {
                int symbol = in.read();
                CollectionTrieNode newNode = createNode();
                setNext((char) symbol, newNode);
                newNode.deserialize(in);
            } else {
                throw new IOException("Invalid " + COLLECTION_NAME + "TrieNode format.\n");
            }
        }
        countSuffixes();
    }

    private void countSuffixes() {
        suffixesCount = isTerminal ? 1 : 0;
        for (char symbol : getSetOfNextSymbols()) {
            suffixesCount += getNext(symbol).suffixesCount();
        }
    }

    private void write(ArrayList<String> array, String prefix) {
        if (isTerminal) {
            array.add(prefix);
        }
        for (char symbol : getSetOfNextSymbols()) {
            getNext(symbol).write(array, prefix + symbol);
        }
    }

    protected abstract CollectionTrieNode createNode();

    protected abstract void setNext(char symbol, CollectionTrieNode node);

    protected abstract CollectionTrieNode getNext(char symbol);

    protected abstract Collection<Character> getSetOfNextSymbols();
}
