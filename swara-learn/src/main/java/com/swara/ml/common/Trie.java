package com.swara.ml.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.locks.StampedLock;
import java.util.function.Function;

import lombok.EqualsAndHashCode;

/**
 * A simple implementation of a generalized trie. A trie or prefix tree, is a data structure
 * optimized for prefix lookups. Unlike other trie implementations like the well-known PatriciaTrie,
 * this implementation makes no claim to be space-optimized; each node of the trie stores exactly
 * one key and value. Implementation features thread-safe get/put operations.
 *
 * @param <K> Key Type
 * @param <V> Value Type
 */
@EqualsAndHashCode(exclude = { "parent", "children", "value" })
public class Trie<K extends Comparable<K>, V> implements Comparable<Trie<K, V>>, Iterable<Trie<K, V>> {

    private final Trie<K, V> parent;
    private final List<Trie<K, V>> children;
    private final StampedLock lock;
    private final K key;
    private volatile V value;

    public Trie() {
        this(null, null, null);
    }

    public Trie(Trie<K, V> parent, K key, V value) {
        this.parent = parent;
        this.children = new ArrayList<>();
        this.lock = new StampedLock();
        this.key = key;
        this.value = value;
    }

    /**
     * Returns the key associated with this trie. The trie key is immutable, and, therefore, this
     * method is thread-safe and does not require any explicit locking.
     */
    public K key() {
        return this.key;
    }

    /**
     * Returns the value associated with this trie. The trie value is mutable, so this method
     * utilizes an optimistic read lock to guarantee thread-safety.
     */
    public V value() {
        long stamp = this.lock.tryOptimisticRead();
        V value = this.value;

        if (!this.lock.validate(stamp)) {
            stamp = this.lock.readLock();
            value = this.value;
            this.lock.unlock(stamp);
        }

        return value;
    }

    /**
     * Returns the closest trie node to the specified sequence. Implementation is thread-safe, and
     * utilizes binary search at each level of the trie. Therefore, get operations can be performed
     * in O(k log n) time, where k is the length of the sequence and n is the average # of children.
     */
    public Trie<K, V> get(List<K> seq) {
        if (seq == null || seq.isEmpty()) {
            return this;
        } else {
            final Trie<K, V> search = new Trie<>(this, seq.get(0), null);
            int index = Collections.binarySearch(this.children, search);
            return index < 0 ? this : this.children.get(index).get(seq.subList(1, seq.size()));
        }
    }

    /**
     * Traverses through the specified key sequence, applying the update function to each element.
     * If a particular key doesn't currently exist, the update function will be called with a null
     * argument. Implementation requires an exclusive write-lock on each node that it traverses
     * through, but is thread-safe. It utilizes binary search at each level of the trie, and,
     * therefore can be performed in O(k O(u) log n) where k is the length of the sequence, O(u) is
     * the complexity of the update function and n is average # of children.
     */
    public void put(List<K> seq, Function<V, V> update) {
        if (seq != null && !seq.isEmpty()) {
            // Find the child whose key matches the first element in the sequence.
            final Trie<K, V> search = new Trie<>(this, seq.get(0), null);
            int index = Collections.binarySearch(this.children, search);

            // If no child exists then create a new one, making sure to preserve order.
            if (index < 0) {
                this.children.add(-(index + 1), search);
            }

            // Use the specified function to atomically update the value and recurse.
            final Trie<K, V> tree = index < 0 ? search : this.children.get(index);
            final long stamp = tree.lock.writeLock();
            tree.value = update.apply(tree.value);
            tree.lock.unlock(stamp);
            tree.put(seq.subList(1, seq.size()), update);
        }
    }

    /**
     * Recursively removes this node and all its children. Implementation is not thread-safe, but
     * can be performed in O(h) where h is the height of the trie.
     */
    public void remove() {
        // Recursively delete all children of the tree.
        this.children.forEach(Trie::remove);

        // Remove the tree from the parent.
        this.parent.children.remove(this);
    }

    @Override
    public int compareTo(Trie<K, V> rhs) {
        return this.key.compareTo(rhs.key);
    }

    @Override
    public Iterator<Trie<K, V>> iterator() {
        return this.children.iterator();
    }

}
