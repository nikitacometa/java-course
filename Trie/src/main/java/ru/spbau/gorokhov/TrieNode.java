package ru.spbau.gorokhov;

import java.util.ArrayList;

/**
 * Created by wackloner on 25.09.16 in 12:43.
 */
public interface TrieNode {
    boolean hasNext(char symbol);

    void removeNext(char symbol);

    TrieNode goNext(char symbol);

    void setAsTerminal();

    void setAsNotTerminal();

    boolean isTerminal();

    void increaseSuffixesCount();

    void decreaseSuffixesCount();

    int suffixesCount();

    boolean hasNoSuffixes();

    ArrayList<String> getElementsList();

    String toString();
}
