package com.swara.ml.common;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import org.junit.Assert;
import org.junit.Test;

public class TrieTest {

    @Test
    public void testGetAfterPutAndRemove() {
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
    
    @Test
    public void testPutThreadSafety() throws InterruptedException {
        final Trie<Character, Long> trie = new Trie<>();
        final List<Character> key = Arrays.asList('h', 'e', 'l', 'l', 'o');
        final ExecutorService executor = Executors.newFixedThreadPool(10);

        // Submit simulatenous put operations.
        IntStream.range(0, 10).forEach(i -> executor.submit(
            () -> trie.put(key, v -> v == null ? 0L : ++v))
        );

        // Await the completion of the put operations.
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);

        // Verify that the modifications were applied atomically.
        IntStream.range(1, key.size()).forEach(i ->
            Assert.assertEquals(Long.valueOf(9L), trie.get(key.subList(0, i)).value())
        );
    }

}
