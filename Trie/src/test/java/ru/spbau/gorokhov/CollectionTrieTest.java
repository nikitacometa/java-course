package ru.spbau.gorokhov;

import org.junit.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;

import static org.junit.Assert.*;

/**
 * Created by wackloner on 25.09.16 in 17:11.
 */
public abstract class CollectionTrieTest {
    protected static final String[] WORDS = { "a", "aa", "aaa", "aaaacd", "aaab", "abab", "b", "bb", "aabac", "aaacc" };
    protected static final String NEW_WORD = "Kanye West is God";
    protected static final String PREFIX = "aa";
    protected CollectionTrie trie, trie2;

    @Test
    public void add() throws Exception {
        assertTrue(trie.add(NEW_WORD));

        assertFalse(trie.add(NEW_WORD));

        assertTrue(trie.contains(NEW_WORD));
    }

    @Test
    public void contains() throws Exception {
        assertFalse(trie.contains(NEW_WORD));

        trie.add(NEW_WORD);

        assertTrue(trie.contains(NEW_WORD));

        trie.remove(NEW_WORD);

        assertFalse(trie.contains(NEW_WORD));
    }

    @Test
    public void remove() throws Exception {
        assertFalse(trie.remove(NEW_WORD));

        trie.add(NEW_WORD);

        assertTrue(trie.remove(NEW_WORD));

        assertFalse(trie.remove(NEW_WORD));
    }

    @Test
    public void size() throws Exception {
        assertEquals(WORDS.length, trie.size());

        trie.add(NEW_WORD);

        assertEquals(WORDS.length + 1, trie.size());

        trie.remove(NEW_WORD);

        assertEquals(WORDS.length, trie.size());
    }

    @Test
    public void howManyStartsWithPrefix() throws Exception {
        assertEquals(countPrefixes(), trie.howManyStartsWithPrefix(PREFIX));

        for (String word : WORDS) {
            trie.remove(word);

            assertEquals(countPrefixes(), trie.howManyStartsWithPrefix(PREFIX));
        }
    }

    protected int countPrefixes() {
        int result = 0;
        for (String word : WORDS) {
            if (trie.contains(word) && word.startsWith(PREFIX)) {
                result++;
            }
        }
        return result;
    }

    @Test
    public void getElementsList() {
        ArrayList<String> list = new ArrayList<String>();
        for (String word : WORDS) {
            list.add(word);
        }
        Collections.sort(list);

        assertEquals(list, trie.getElementsList());
    }

    @Test
    public void serialization() throws IOException {
        OutputStream out = new ByteArrayOutputStream();

        trie.serialize(out);

        InputStream in = new ByteArrayInputStream(((ByteArrayOutputStream) out).toByteArray());

        trie2.deserialize(in);

        System.out.println("W\nH\nA\nT");

        System.out.println(trie);
        System.out.println(trie2);

        assertEquals(trie.toString(), trie2.toString());
    }
}