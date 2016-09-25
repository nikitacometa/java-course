package ru.spbau.gorokhov;

/**
 * Created by wackloner on 025 25.09.16 in 16:34.
 */
public class HashMapTrie extends NotCompressedTrie {
    public HashMapTrie() {
        root = new HashMapTrieNode();
    }
}
