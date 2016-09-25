package ru.spbau.gorokhov;

/**
 * Created by wackloner on 25.09.16 in 12:43.
 */
public interface TrieNode {
    boolean hasSymbol(char symbol);

    TrieNode goSymbol(char symbol);

    void removeSymbol(char symbol);

    void setAsTerminal();

    void setAsNotTerminal();

    boolean isTerminal();

    void increaseSuffixesCount();

    void decreaseSuffixesCount();

    int suffixesCount();

    boolean hasNoSuffixes();

    String toString();
}
