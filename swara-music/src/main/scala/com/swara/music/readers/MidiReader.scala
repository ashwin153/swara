package com.swara.music
package readers

import java.io.InputStream
import javax.sound.midi._
import scala.util.Try
import scala.collection.mutable

/**
 * Reads a [[Song]] from a Midi [[javax.sound.midi.Sequence]]. Midi files provide a compact
 * alternative to space inefficient audio files. Midi files are composed of sequences of
 * time-stamped Midi messages, which each encode the various musical events that occur throughout a
 * song (e.g. key and tempo changes, note played, note released, etc.). The ability to read Midi
 * files enables the library to interface with conventional musical production and playback tools.
 * For more information on Midi, check out: http://www.somascape.org/midi/tech/mfile.html.
 */
object MidiReader extends MusicReader {

  override def read(in: InputStream): Try[Song] = Try {
    // Flatten all midi messages into a sorted map from midi tick to message.
    val sequence = MidiSystem.getSequence(in)
    require(sequence.getDivisionType == Sequence.PPQ, "Sequence must be divided in PPQ.")

//    var multiple = sequence.getResolution; while (multiple % 2 == 0) multiple /= 2
//    def round(x: Long, to: Double): Long = (math.round(x / to) * to).toLong

    val midi = mutable.TreeMap.empty[Long, Array[MidiMessage]]
    midi ++= sequence.getTracks
      .flatMap(t => (0 until t.size).map(t.get))
      .groupBy(_.getTick)
      .mapValues(_.map(_.getMessage))

    // Iteratively construct the various musical elements; defaults specified in midi format spec.
    val fragments = mutable.Buffer.empty[Fragment]
    var key = Key.CMajor
    var tempo = Tempo(Duration(4, 4), 120.0)
    val programs = mutable.Map.empty[Int, Int]
    val channels = mutable.Map.empty[Int, Phrase]

    while (midi.nonEmpty) {
      val (start, head) = midi.head

      // Update the fragment's key, time, tempo, and programs.
      head.collect {
        case m: MetaMessage if m.getType == 0x59 /* Key Change */ =>
          key = key.copy(signature = m.getData()(0), isMajor = m.getData()(1) == 1)
        case m: MetaMessage if m.getType == 0x58 /* Time Change */ =>
          tempo = tempo.copy(signature = Duration(m.getData()(0), Math.pow(2, m.getData()(1)).toInt))
        case m: MetaMessage if m.getType == 0x51 /* Tempo Change */ =>
          val mspqn = (m.getData()(0) << 16) + (m.getData()(1) << 8) + m.getData()(2)
          tempo = tempo.copy(bpm = 6E7 / mspqn)
        case m: ShortMessage if m.getCommand == 0xC0 /* Program Change */ =>
          programs += m.getChannel -> m.getData1
      }

      // Extract all messages in the current fragment, until the next fragment delimiter.
      val messages = midi.takeWhile {
        case (tick, msgs) => tick == start || msgs.forall {
          case m: MetaMessage if m.getType == 0x59     /* Key Change     */ => false
          case m: MetaMessage if m.getType == 0x58     /* Time Change    */ => false
          case m: MetaMessage if m.getType == 0x51     /* Tempo Change   */ => false
          case m: ShortMessage if m.getCommand == 0xC0 /* Program Change */ => false
          case _ => true
        }
      }

      messages.flatMap({
        case (tick, msgs) => msgs.collect {
          case e: ShortMessage if e.getCommand == 0x90 /* Note On */ =>
            (tick, e.getChannel, Note(Pitch(e.getData1 % 12, e.getData1 / 12), e.getData2 / 128.0))
          case e: ShortMessage if e.getCommand == 0x80 /* Note Off */ =>
            (tick, e.getChannel, Note(Pitch(e.getData1 % 12, e.getData1 / 12), 0.0))
        }
      }).groupBy(_._2).foreach({ case (channel, notes) =>
        val groups = new mutable.TreeMap[(Long, Long), Set[Note]]
        val active = mutable.Map.empty[Note, Long]

        notes.toSeq.sortBy(_._1).foreach { case (tick, _, note) =>
          // Deactivate the note if it is currently active.
          active.keys.find(_.pitch == note.pitch).foreach { last =>
            val duration = (active(last), tick)
            groups += duration -> (groups.getOrElse(duration, Set.empty) + last)
            active -= last
          }

          // Activate the note if and only if it has a non-zero volume.
          if (note.volume > 0) active += note -> tick
        }

        midi --= messages.keys

        // Construct a builder to generate chords from notes and midi tick durations.
        val voices = mutable.Buffer.empty[Voice]
        val ppb = 4 * sequence.getResolution

        while (groups.nonEmpty) {
          val chords = mutable.Buffer.empty[Chord]
          var prev = start
          var next = groups.from((prev, prev))

          // While we have not traversed the entire sequence of chords, iteratively find the next
          // chord whose start time is at least the previous time. Insert a rest before the next
          // chord if it does not immediately start after the previous chord. Then, insert the
          // chord itself and shift the previous time to the end of the next chord.
          while (next.nonEmpty) {
            val ((start, end), group) = next.head
            if (start > prev) chords += Chord(Set.empty, Duration((start - prev).toInt, ppb).reduce)
            chords += Chord(group, Duration((end - start).toInt, ppb).reduce)
            groups -= ((start, end))
            prev = end
            next = groups.from((prev, prev))
          }

          if (midi.nonEmpty) chords += Chord(Set.empty, Duration((midi.head._1 - prev).toInt, ppb).reduce)
          voices += Voice(chords)
        }

        // Construct a new phrase from the constructed voices and add it to the channel mapping.
        channels += channel -> Phrase(programs(channel), voices.toSet)
      })

      // Insert the fragment into the song.
      fragments += Fragment(key, tempo, channels.toMap)
    }

    Song(fragments)
  }

}