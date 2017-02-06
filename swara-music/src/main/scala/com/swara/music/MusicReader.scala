package com.swara.music

import java.io.InputStream
import scala.util.Try

/**
 * A music reader. A music reader is responsible for parsing a [[Song]] from a given input stream,
 * and it, along with the [[MusicWriter]], allow musical information to be serialized and
 * deserialized to and from a variety of sources. The reader and writer interfaces are separated on
 * purpose, because they ought to be independent operations.
 */
trait MusicReader {

  def read(in: InputStream): Try[Song]

}