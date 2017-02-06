package com.swara.music

import java.io.OutputStream
import scala.util.Try

/**
 * A music writer. A music writer is responsible for writing a [[Song]] to the given stream, and it,
 * along with the [[MusicReader]], allow musical information to be serialized and deserialized to
 * and from a variety of sources. The reader and writer interfaces are separated on purpose, because
 * they ought to be independent operations.
 */
trait MusicWriter {

  def write(song: Song, out: OutputStream): Try[Unit]

}
