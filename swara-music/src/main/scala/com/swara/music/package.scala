package com.swara

import com.swara.music.writers.MidiWriter
import java.io.{ByteArrayInputStream, ByteArrayOutputStream, File, FileInputStream, FileOutputStream}
import javax.sound.midi.MidiSystem
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

  /**
   * A music player. Plays the specified song by first converting it into midi, and then utilizing
   * the Java Sound Api for playback. Returns whether or not playback was successful.
   *
   * @param song Song to play.
   * @return Whether or not playback was successful.
   */
  def play(song: Song): Try[Unit] = {
    val out = new ByteArrayOutputStream
    MidiWriter.write(song, out).flatMap(_ => Try {
      // Load the midi file into a sequencer.
      val in = new ByteArrayInputStream(out.toByteArray)
      val sequencer = MidiSystem.getSequencer()
      sequencer.setSequence(MidiSystem.getSequence(in))

      // Run the sequencer to completion.
      sequencer.open()
      sequencer.start()

      try {
        Thread.sleep(song.duration.toMillis)
      } finally {
        sequencer.stop()
        sequencer.close()
      }
    })
  }

}
