package ru.spbau.gorokhov;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by wackloner on 25.09.16 in 12:42.
 */
public class HashMapTrieNode implements TrieNode, StreamSerializable {
    private HashMap<Character, HashMapTrieNode> symbols;
    private boolean isTerminal;
    private int suffixesCount;

    public HashMapTrieNode() {
        symbols = new HashMap<Character, HashMapTrieNode>(0);
    }

    public boolean hasSymbol(char symbol) {
        return symbols.containsKey(symbol);
    }

    public HashMapTrieNode goSymbol(char symbol) {
        HashMapTrieNode nextNode;
        if (hasSymbol(symbol)) {
            nextNode = symbols.get(symbol);
        } else {
            nextNode = new HashMapTrieNode();
            symbols.put(symbol, nextNode);
        }
        return nextNode;
    }

    public void removeSymbol(char symbol) {
        symbols.remove(symbol);
    }

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

    public void serialize(OutputStream out) throws IOException {
        out.write(isTerminal ? 1 : 0);
        for (char symbol : symbols.keySet()) {
            out.write(1);
            out.write(symbol);
            symbols.get(symbol).serialize(out);
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
            throw new IOException("Invalid HashMapTrieNode format.");
        }
        while (true) {
            int cur = in.read();
            if (cur == -1) {
                break;
            } if (cur == 0) {
                break;
            } else if (cur == 1) {
                int symbol = in.read();
                HashMapTrieNode newNode = new HashMapTrieNode();
                symbols.put((char) symbol, newNode);
                newNode.deserialize(in);
            } else {
                throw new IOException("Invalid HashMapTrieNode format.");
            }
        }
        countSuffixes();
    }

    public String toString() {
        ArrayList<String> result = new ArrayList<String>();
        write(result, "");
        return result.toString();
    }

    private void write(ArrayList<String> array, String prefix) {
        if (isTerminal) {
            array.add(prefix);
        }
        for (char symbol : symbols.keySet()) {
            symbols.get(symbol).write(array, prefix + symbol);
        }
    }

    private void countSuffixes() {
        suffixesCount = isTerminal ? 1 : 0;
        for (HashMapTrieNode next : symbols.values()) {
            suffixesCount += next.suffixesCount();
        }
    }
}
