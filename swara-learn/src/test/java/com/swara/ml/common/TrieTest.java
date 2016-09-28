package com.swara.ml.common;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class TrieTest {

    @Test
    public void testOperations() {
        final Trie<Character, Long> trie = new Trie<>();
        final List<Character> k1 = Arrays.asList('h', 'e', 'l', 'l', 'o');
        final List<Character> k2 = Arrays.asList('h', 'e', 'l', 'p');

        // Verify that the keys don't currently exist.
        Assert.assertNull(trie.get(k1));
        Assert.assertNull(trie.get(k2));

        // Verify that the keys are inserted into the trie.
        trie.put(k1, i -> i == null ? 0L : ++i);
        trie.put(k2, i -> i == null ? 0L : ++i);
        Assert.assertNotNull(trie.get(k1));
        Assert.assertNotNull(trie.get(k2));
        Assert.assertEquals(Long.valueOf(0L), trie.get(k1).value());
        Assert.assertEquals(Long.valueOf(1L), trie.get(Arrays.asList('h', 'e', 'l')).value());

        // Verify that the key is recursively removed from the trie.
        trie.get(Arrays.asList('h', 'e', 'l')).remove();
        Assert.assertNull(trie.get(k1));
        Assert.assertNull(trie.get(k2));
        Assert.assertNotNull(trie.get(Arrays.asList('h', 'e')));
    }

}
