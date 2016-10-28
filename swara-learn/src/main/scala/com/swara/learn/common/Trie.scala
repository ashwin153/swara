package com.swara.learn.common

import scala.collection.mutable

/**
 * A simple implementation of a concurrent, generalized trie. A trie or prefix tree, is a data
 * structure optimized for prefix lookups. Unlike other trie implementations like the well-known
 * PatriciaTrie, this implementation makes no claim to be space-optimized; each node of the trie
 * stores exactly one symbol (except the root, which contains no symbol) and an optional value.
 *
 * @tparam K Type of symbols; keys are a sequence of symbols
 * @tparam V Type of values stored in the trie
 */
class Trie[K, V] private (
  val parent: Option[Trie[K, V]],
  val symbol: Option[K],
  var value:  Option[V]
) {

  private[Trie] val next = new mutable.HashMap[K, Trie[K, V]]
  private[Trie] val lock = new Lock

  /**
   * Returns the full key associated with this trie. The key is simply the concatenation of all the
   * symbols of all the trie's ancestors. Implementation ignores all empty symbols. Because symbols
   * are immutable, this method is thread-safe. O(h), where h is the height of the trie.
   *
   * @return Concatenation of all symbols of all ancestors
   */
  def key: List[K] = (parent, symbol) match {
    case (None, None) => Nil
    case (None, Some(name)) => name :: Nil
    case (Some(trie), None) => trie.key
    case (Some(trie), Some(name)) => trie.key :+ name
  }

  /**
   * Returns the trie with the closest matching key. Implementation is thread-safe, but requires a
   * shared lock for each trie node corresponding to a symbol in the key. O(k), where k is the
   * length of the key.
   *
   * @param key Sequence of symbols to search for
   * @return Trie with the closest matching key
   */
  def get(key: List[K]): Trie[K, V] = key match {
    case Nil => this
    case x :: rest => this.lock.shared { this.next.get(x) }.fold(this)(_.get(rest))
  }

  /**
   * Traverses through the specified key (sequence of symbols), applying the visitor function to
   * each trie node. The visitor function specifies a new value for a node, given the key suffix
   * (remaining symbols in the key) and the previous value. The visitor function is guaranteed to be
   * applied atomically. Implementation is thread-safe, but requires an exclusive lock for each
   * visited trie node. O(k O(visitor)), where k is the length of the key.
   *
   * @param key Sequence of symbols to insert
   * @param visitor Function to apply to each visited trie node.
   */
  def put(key: List[K], visitor: (List[K], Option[V]) => Option[V]): Unit = key match {
    case Nil => this.lock.exclusive { this.value = visitor(Nil, this.value) }
    case x :: rest =>
      this.lock.exclusive {
        this.value = visitor(key, this.value)
        this.next.getOrElseUpdate(x, new Trie(Some(this), Some(x), None))
      }.put(rest, visitor)
  }

  def put(key: List[K], value: V): Unit = put(key, (suffix, prev) => (suffix, prev) match {
    case (Nil, _) => Some(value)
    case (_, prev) => prev
  })

  /**
   * Recursively removes this node and all its children, by removing the trie from its parent's list
   * of children. Implementation is thread-safe, but requires an exclusive write-lock. O(h), where
   * h is the height of the trie.
   */
  def remove(): Unit = parent.foreach(trie => this.symbol.foreach(trie.lock.exclusive { trie.next.remove(_) }))

  /**
   * Returns the set of all children of this trie. Modifications to this set have no effect on the
   * underlying trie.
   *
   * @return Set of all children of this trie.
   */
  def children: Set[Trie[K, V]] = this.next.values.toSet

}

object Trie {

  /** Construct an empty Trie */
  def apply[K, V] = new Trie[K, V](None, None, None)

}