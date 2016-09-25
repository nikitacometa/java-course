package ru.spbau.gorokhov;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 * Created by wackloner on 25.09.16 in 16:26.
 */
public abstract class NotCompressedTrieNode implements TrieNode, StreamSerializable {
    protected boolean isTerminal;
    protected int suffixesCount;

    public abstract boolean hasSymbol(char symbol);

    public abstract NotCompressedTrieNode goSymbol(char symbol);

    public abstract void removeSymbol(char symbol);

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

    protected abstract void write(ArrayList<String> array, String prefix);

    public String toString() {
        return getElementsList().toString();
    }

    public abstract void serialize(OutputStream out) throws IOException;

    public abstract void deserialize(InputStream in) throws IOException;
}
