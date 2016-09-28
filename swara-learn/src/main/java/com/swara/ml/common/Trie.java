package com.swara.ml.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.locks.StampedLock;
import java.util.function.Function;

import lombok.EqualsAndHashCode;

/**
 * A simple implementation of a generalized trie. A trie or prefix tree, is a data structure
 * optimized for prefix lookups. Unlike other trie implementations like the well-known PatriciaTrie,
 * this implementation makes no claim to be space-optimized; each node of the trie stores exactly
 * one key and value. Implementation is thread-safe.
 *
 * @param <K> Key Type
 * @param <V> Value Type
 */
@EqualsAndHashCode(exclude = { "parent", "children", "value" })
public class Trie<K, V> implements Comparable<Trie<K, V>> {

    private final Trie<K, V> parent;
    private final List<Trie<K, V>> children;
    private final Comparator<K> comparator;
    private final StampedLock lock;
    private final K key;
    private volatile V value;

    public Trie(Comparator<K> comparator) {
        this(null, null, null, comparator);
    }

    private Trie(Trie<K, V> parent, K key, V value, Comparator<K> comparator) {
        this.parent = parent;
        this.children = new ArrayList<>();
        this.comparator = comparator;
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
     * Returns the unmodifiable list of children of this trie node. Because the list is immutable,
     * this method is thread safe and does not require any explicit locking.
     */
    public List<Trie<K, V>> children() {
        return Collections.unmodifiableList(this.children);
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
            // Perform binary search using a read lock.
            final Trie<K, V> search = new Trie<>(this, seq.get(0), null, comparator);
            long stamp = this.lock.readLock();
            int index = Collections.binarySearch(this.children, search);
            final Trie<K, V> result = index < 0 ? null : this.children.get(index);
            this.lock.unlock(stamp);

            // Recurse on matching child or return if no such child exists.
            return result == null ? null : result.get(seq.subList(1, seq.size()));
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
            final Trie<K, V> search = new Trie<>(this, seq.get(0), null, comparator);
            long stamp = this.lock.readLock();
            int index = Collections.binarySearch(this.children, search);
            this.lock.unlock(stamp);

            // If no child exists then create a new one, making sure to preserve order.
            if (index < 0) {
                stamp = this.lock.writeLock();
                this.children.add(-(index + 1), search);
                this.lock.unlock(stamp);
            }

            // Use the specified function to atomically update the value and recurse.
            final Trie<K, V> tree = index < 0 ? search : this.children.get(index);
            stamp = tree.lock.writeLock();
            tree.value = update.apply(tree.value);
            tree.lock.unlock(stamp);
            tree.put(seq.subList(1, seq.size()), update);
        }
    }

    /**
     * Recursively removes this node and all its children. Implementation is thread-safe, and
     * can be performed in O(h) where h is the height of the trie.
     */
    public void remove() {
        // Remove the tree from the parent.
        long stamp = this.parent.lock.writeLock();
        this.parent.children.remove(this);
        this.parent.lock.unlock(stamp);
    }

    @Override
    public int compareTo(Trie<K, V> rhs) {
        return this.comparator.compare(key, rhs.key);
    }

}
