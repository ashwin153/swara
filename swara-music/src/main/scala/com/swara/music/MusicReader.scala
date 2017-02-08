package com.swara.music

import java.io.{File, FileInputStream, InputStream}
import scala.util.Try
import resource._

/**
 * A music reader. A music reader is responsible for parsing a [[Song]] from a given input stream,
 * and it, along with the [[MusicWriter]], allow musical information to be serialized and
 * deserialized to and from a variety of sources. The reader and writer interfaces are separated on
 * purpose, because they ought to be independent operations.
 */
trait MusicReader {

  def read(in: InputStream): Try[Song]

  final def read(file: File): Try[Song] =
    managed(new FileInputStream(file)).acquireAndGet(read)

}