package com.swara.learn.common

import java.util.concurrent.Executors
import org.scalatest.FunSuite
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

class TrieTest extends FunSuite {

  test("Get should have happens-before relationship") {
    val trie = Trie[Char, Int]
    val hello = 'h' :: 'e' :: 'l' :: 'l' :: 'o' :: Nil

    // Verify that the trie has read-your-update semantics.
    assert(trie.get(hello).key == Nil)
    trie.put(hello, 2)
    assert(trie.get(hello).key == hello)
    assert(trie.get(hello).value.contains(2))
    trie.get(hello).remove()
    assert(trie.get(hello).key != hello)
  }

  test("Put should update values") {
    val trie = Trie[Char, Int]
    val hel = 'h' :: 'e' :: 'l' :: Nil
    val help = hel :+ 'p'
    val hello = hel :+ 'l' :+ 'o'

    // Insert 'hello' and 'help' into the trie.
    trie.put(hello, (s, v) => (s, v) match {
      case (_, None) => Some(1)
      case (_, Some(value)) => Some(value + 1)
    })

    trie.put(help, (s, v) => (s, v) match {
      case (_, None) => Some(1)
      case (_, Some(value)) => Some(value + 2)
    })

    // Verify that each key has the correct value.
    assert(trie.get(hello).value.contains(1))
    assert(trie.get(help).value.contains(1))
    assert(trie.get(hel).value.contains(3))
  }

  test("Put should atomically updates value") {
    val trie = Trie[Char, Int]
    val hello = 'h' :: 'e' :: 'l' :: 'l' :: 'o' :: Nil
    val executor = Executors.newCachedThreadPool()

    // Submit simultaneous put operations and verify results.
    Future.sequence((0 until 10).map { i =>
      Future { trie.put(hello, (s, p) => (s, p) match {
        case (_, None) => Some(1)
        case (_, Some(value)) => Some(value + 1)
      })}
    }).onComplete {
      case Success(v) => (1 until hello.length)
        .map(i => hello.slice(0, i))
        .foreach(prefix => assert(trie.get(prefix).value.contains(9)))
      case Failure(e) => fail(e)
    }
  }

}
