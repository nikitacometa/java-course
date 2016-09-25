package ru.spbau.gorokhov;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 * Created by wackloner on 25.09.16 in 17:59.
 */
public class ArrayTrieNode extends NotCompressedTrieNode {
    private static final int CHARACTERS = 256;
    private ArrayTrieNode[] next;

    public ArrayTrieNode() {
        next = new ArrayTrieNode[CHARACTERS];
    }

    public boolean hasSymbol(char symbol) {
        return next[symbol] != null;
    }

    public ArrayTrieNode goSymbol(char symbol) {
        ArrayTrieNode nextNode;
        if (hasSymbol(symbol)) {
            nextNode = next[symbol];
        } else {
            nextNode = new ArrayTrieNode();
            next[symbol] = nextNode;
        }
        return nextNode;
    }

    public void removeSymbol(char symbol) {
        next[symbol] = null;
    }

    public void serialize(OutputStream out) throws IOException {
        out.write(isTerminal ? 1 : 0);
        for (char symbol = 0; symbol < CHARACTERS; symbol++) {
            if (next[symbol] != null) {
                out.write(1);
                out.write(symbol);
                next[symbol].serialize(out);
                out.write(0);
            }
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
                ArrayTrieNode newNode = new ArrayTrieNode();
                next[symbol] = newNode;
                newNode.deserialize(in);
            } else {
                throw new IOException("Invalid HashMapTrieNode format.");
            }
        }
        countSuffixes();
    }

    protected void write(ArrayList<String> array, String prefix) {
        if (isTerminal) {
            array.add(prefix);
        }
        for (char symbol = 0; symbol < CHARACTERS; symbol++) {
            if (next[symbol] != null) {
                next[symbol].write(array, prefix + symbol);
            }
        }
    }

    private void countSuffixes() {
        suffixesCount = isTerminal ? 1 : 0;
        for (char symbol = 0; symbol < CHARACTERS; symbol++) {
            if (next[symbol] != null) {
                suffixesCount += next[symbol].suffixesCount();
            }
        }
    }
}
