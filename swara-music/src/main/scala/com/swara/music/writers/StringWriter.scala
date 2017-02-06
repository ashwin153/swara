package com.swara.music
package writers

import java.io.OutputStream
import scala.util.Try

import com.swara.music.Song

/**
 * Writes a [[Song]] to a standard Java String. This writer is included primarily for debugging
 * and logging purposes as well as the gold-standard for performance; it doesn't get faster than
 * dumping an object to string!
 */
object StringWriter extends MusicWriter {

  override def write(song: Song, out: OutputStream): Try[Unit] =
    Try(out.write(song.toString.getBytes))

}