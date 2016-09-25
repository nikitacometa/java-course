package ru.spbau.gorokhov;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by wackloner on 25.09.16 in 16:54.
 */
public class HashMapTrieTest extends NotCompressedTrieTest {
    @Before
    public void setUp() throws Exception {
        trie = new HashMapTrie();
        trie2 = new HashMapTrie();

        for (String word : WORDS) {
            trie.add(word);
        }
    }
}