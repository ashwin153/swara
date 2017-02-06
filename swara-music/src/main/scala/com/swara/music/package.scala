package com.swara

import java.io.{File, FileInputStream, FileOutputStream}
import scala.util.Try
import resource._

package object music {

  /**
   * A music conversion codec. Loads music from the source file using the provided [[MusicReader]]
   * and writes it the destination file using the provided [[MusicWriter]]. Returns whether or not
   * the conversion operation was successful.
   *
   * @param reader Music reader.
   * @param writer Music writer.
   * @param src Source file.
   * @param dest Destination file.
   * @return Whether or not the conversion was successful.
   */
  def convert(reader: MusicReader, writer: MusicWriter)(src: File, dest: File): Try[Unit] =
    managed(new FileInputStream(src)).acquireAndGet { in =>
      managed(new FileOutputStream(dest)).acquireAndGet { out =>
        reader.read(in).flatMap(writer.write(_, out))
      }
    }

}
