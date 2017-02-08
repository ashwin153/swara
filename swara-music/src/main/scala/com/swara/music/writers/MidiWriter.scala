package com.swara.music
package writers

import java.io.OutputStream
import javax.sound.midi._
import scala.util.Try
import scala.math._

/**
 * Writes [[Song]] to a Midi [[Sequence]]. Midi files provide a compact alternative to space
 * inefficient audio files. Midi files are composed of sequences of time-stamped Midi messages,
 * which each encode the various musical events that occur throughout a song (e.g. key and tempo
 * changes, note played, note released, etc.). Midi writing conforms to the Format 0 specification;
 * therefore, the resulting sequence is a single-track, 16-channel Midi file. The ability to write
 * Midi files enables the library to interface with conventional musical production and playback
 * tools. For more information on Midi, check out: http://www.somascape.org/midi/tech/mfile.html.
 */
object MidiWriter extends MusicWriter {

  private val TickResolution: Int = 480

  override def write(song: Song, out: OutputStream): Try[Unit] = Try {
    // Construct an empty midi ppq sequence.
    val sequence = new Sequence(Sequence.PPQ, this.TickResolution)
    val track = sequence.createTrack()

    // Write the various fragments that comprise the song.
    song.fragments.foreach { fragment =>
      val start = sequence.getTickLength

      // Write the beats per minute to the sequence as microseconds per quarter note.
      val mspqn = (6E7 / fragment.tempo.bpm).toInt
      track.add(new MidiEvent(new MetaMessage(0x51, Array[Byte](
        ((mspqn >> 16) & 0xFF).toByte,
        ((mspqn >>  8) & 0xFF).toByte,
        ((mspqn >>  0) & 0xFF).toByte
      ), 3), start))

      // Write the time signature as the beats and the log(2) of the tempo.
      track.add(new MidiEvent(new MetaMessage(0x58, Array[Byte](
        fragment.tempo.signature.beats.toByte,
        (log(fragment.tempo.signature.meter) / log(2)).toByte,
        24.toByte,
        8.toByte
      ), 4), start))

      // Write the key by encoding its signature and the type.
      track.add(new MidiEvent(new MetaMessage(0x59, Array[Byte](
        fragment.key.signature.toByte,
        if (fragment.key.isMajor) 1.toByte else 0.toByte
      ), 2), start))

      fragment.channels.foreach { case (channel, phrase) =>
        // Write the instrument as a "program change".
        track.add(new MidiEvent(new ShortMessage(0xC0, channel, phrase.program, 0), start))

        phrase.voices.foreach { voice =>
          // Calculate the tick duration of each chord.
          val ticks: Seq[Long] = voice.chords
            .map(c => (c.length.beats * 4 * this.TickResolution) / c.length.meter)
            .scanLeft(start)((a, b) => a + b)

          // Encode the notes of the chord as note on/off events.
          ticks.sliding(2).zip(voice.chords.iterator).foreach {
            case (t, c) => c.notes.foreach { n =>
              val key = n.pitch.octave * 12 + n.pitch.position
              val vol = (n.volume * 128).toInt
              require(key >= 0 && key < 128, "Midi key must fit in a single byte.")

              track.add(new MidiEvent(new ShortMessage(0x90, channel, key, vol), t.head))
              track.add(new MidiEvent(new ShortMessage(0x80, channel, key,   0), t.last))
            }
          }
        }
      }
    }

    // Write the sequence to the output stream.
    MidiSystem.write(sequence, 0, out)
  }

}