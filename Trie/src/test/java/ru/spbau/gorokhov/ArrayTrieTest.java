package ru.spbau.gorokhov;

import org.junit.Before;

/**
 * Created by wackloner on 25.09.16 in 18:13.
 */
public class ArrayTrieTest extends CollectionTrieTest {
    @Before
    public void setUp() throws Exception {
        trie = new ArrayTrie();
        trie2 = new ArrayTrie();

        for (String word : WORDS) {
            trie.add(word);
        }
    }
}