package ru.spbau.gorokhov;

import org.junit.Before;

/**
 * Created by wackloner on 25.09.16 in 16:54.
 */
public class HashMapTrieTest extends SimpleTrieTest {
    @Before
    public void setUp() throws Exception {
        trie = new HashMapTrie();
        trie2 = new HashMapTrie();

        for (String word : WORDS) {
            trie.add(word);
        }
    }
}