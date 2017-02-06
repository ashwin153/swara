package com.swara.music

/**
 * A musical element. An element is any attribute of a [[Song]] that is necessary to encode it as
 * sheet music. In other words, elements are all the non-derived properties of music; songs are
 * uniquely defined by their component elements.
 */
sealed trait MusicElement extends Serializable

/**
 * A sequence of fragments played sequentially. Songs can be read from various input sources using a
 * [[MusicReader]] and written to various output sources using a [[MusicWriter]]. Songs are the
 * top-level [[MusicElement]]. Songs consist of a sequence of fragments. Fragments are stored in
 * playback order. Songs are immutable, and, therefore, thread-safe.
 *
 * @param fragments Fragments to be sequentially played.
 */
@SerialVersionUID(1L)
case class Song(
  fragments: Seq[Fragment]
) extends MusicElement {

  /**
   * Returns the length of the song in minutes. The length of a song is simply the sum of the
   * lengths of all the fragments in the song.
   *
   * @return Length of the song in minutes.
   */
  def length: Double =
    this.fragments.map(_.length).sum

}

/**
 * A set of [[Phrase]] played simultaneously in a particular [[Key]] and [[Tempo]]. Fragments are
 * the building blocks of a [[Song]]; a song is simply a set of fragments played sequentially.
 * Fragments represent a particular mapping of midi channels to phrases played in a particular key
 * and tempo. Communication with a midi sequencer occurs over sixteen channels. Each channel
 * represents an independent part in a musical score. The general midi (GM) specification dictates
 * that percussion must occur on channel 10 and all other instruments may be played on the other
 * channels. Fragments are immutable and, therefore, thread-safe.
 *
 * @param key Key in which all phrases are simultaneously played.
 * @param tempo Tempo in which all phrases are simultaneously played.
 * @param channels Mapping of channels to phrases.
 */
@SerialVersionUID(1L)
case class Fragment(
  key: Key,
  tempo: Tempo,
  channels: Map[Int, Phrase]
) extends MusicElement {

  require(this.channels.keys.forall(c => c >= 0 && c < 16), "Channels must be between 0 and 16.")

  /**
   * Returns the length of the fragment in minutes. Fragment length is simply the length of the
   * longest phrase in beats multiplied by the bpm of the tempo.
   *
   * @return Length of the fragment in minutes.
   */
  def length: Double = {
    val beats = channels.values
      .flatMap(_.voices.map(_.chords.map(_.duration)))
      .map(_.foldLeft(0.0)((b, d) => b + d.beats.toDouble / d.meter))
      .max

    beats / tempo.bpm
  }

}

/**
 * A musical key. Keys may be major or minor, and contain a key signature which specifies which
 * notes are sharp (x), which are flat (b), and which are natural. Music theory permits the key
 * signature to be encoded as a single number on the interval [-7, 7] that specifies the number of
 * sharps (+) or flats (-). For example, C Major and A Minor have a signature of 0, because they
 * have no sharps nor flats. Keys are immutable, and, therefore, thread-safe.
 *
 * @param signature Key signature.
 * @param isMajor True if a major key, false if minor.
 */
@SerialVersionUID(1L)
case class Key(
  signature: Int,
  isMajor: Boolean
) extends MusicElement {

  require(this.signature >= -7, "Key signature may have up to 7 flats.")
  require(this.signature <= +7, "Key signature may have up to 7 sharps.")

  def isMinor: Boolean = !isMajor

  /**
   * Returns the pitch positions of all the notes in the key. The tonic, or first position, is
   * calculated from the Circle of Fifths, which states that each additional sharp moves the tonic
   * up a perfect fifth (7 half-steps) and each additional flat moves the tonic down a perfect
   * fourth beginning from C in major keys and A in minor keys. The positions of the scale are
   * calculated from the observation that the notes of a major scale are 2, 4, 5, 7, 9, and 11
   * half-steps above the tonic and the notes of a minor scale are 2, 3, 5, 7, 8, and 10 half-steps
   * above.
   *
   * @return Pitches of all the notes in the scale.
   */
  def scale: Seq[Int] = {
    val start  = if (this.isMajor) Pitch.C else Pitch.A
    val offset = if (this.isMajor) Seq(0, 2, 4, 5, 7, 9, 11) else Seq(0, 2, 3, 5, 7, 8, 10)
    val tonic  = start + this.signature * 7
    offset.map(p => Math.floorMod(p + tonic, 12))
  }

}

object Key {

  val CMajor  = Key(+0, isMajor = true)
  val GMajor  = Key(+1, isMajor = true)
  val DMajor  = Key(+2, isMajor = true)
  val AMajor  = Key(+3, isMajor = true)
  val EMajor  = Key(+4, isMajor = true)
  val BMajor  = Key(+5, isMajor = true)
  val FxMajor = Key(+6, isMajor = true)
  val CxMajor = Key(+7, isMajor = true)
  val CbMajor = Key(-7, isMajor = true)
  val GbMajor = Key(-6, isMajor = true)
  val DbMajor = Key(-5, isMajor = true)
  val AbMajor = Key(-4, isMajor = true)
  val EbMajor = Key(-3, isMajor = true)
  val BbMajor = Key(-2, isMajor = true)
  val FMajor  = Key(-1, isMajor = true)

  val AMinor  = Key(+0, isMajor = false)
  val EMinor  = Key(+1, isMajor = false)
  val BMinor  = Key(+2, isMajor = false)
  val FxMinor = Key(+3, isMajor = false)
  val CxMinor = Key(+4, isMajor = false)
  val GxMinor = Key(+5, isMajor = false)
  val DxMinor = Key(+6, isMajor = false)
  val AxMinor = Key(+7, isMajor = false)
  val AbMinor = Key(-7, isMajor = false)
  val EbMinor = Key(-6, isMajor = false)
  val BbMinor = Key(-5, isMajor = false)
  val FMinor  = Key(-4, isMajor = false)
  val CMinor  = Key(-3, isMajor = false)
  val GMinor  = Key(-2, isMajor = false)
  val DMinor  = Key(-1, isMajor = false)

}

/**
 * A musical tempo. Tempo specifies the number of beats per minute (bpm) as well as a time
 * signature. Time signatures define the duration of a measure of music as a particular number of
 * beats in a particular meter. For example, waltzes typically have a time signature of 3/4,
 * indicating that each measure consists of three quarter notes. Tempos are immutable, and,
 * therefore, thread-safe.
 *
 * @param signature Time signature.
 * @param bpm Beats per minute.
 */
@SerialVersionUID(1L)
case class Tempo(
  signature: Duration,
  bpm: Double
) extends MusicElement {

  require(this.bpm > 0, "Bpm must be positive.")

}

object Tempo {

  val Grave = 45.0
  val Largo = 50.0
  val Larghetto = 55.0
  val Adagio = 60.0
  val Andante = 70.0
  val Moderato = 90.0
  val Allegretto = 105.0
  val Allegro = 120.0
  val Vivace = 140.0
  val Presto = 160.0
  val Prestissimo = 200.0

}

/**
 * A set of voices played simultaneously. Phrases encode musical information for a particular
 * instrument. Phrases are composed of a program, or a midi instrument identifier, and a set of
 * voices. Monophonic phrases consist of a single voice, while polyphonic phrases consist of
 * multiple voices sounded simultaneously. Phrases are combined together to form a [[Fragment]].
 * Phrases are immutable, and, therefore, thread-safe.
 *
 * @param program Instrument identifier.
 * @param voices Voices that comprise the phrase.
 */
@SerialVersionUID(1L)
case class Phrase(
  program: Int,
  voices: Set[Voice]
) {

  require(program >=  0, "Program number must be positive.")
  require(program < 128, "Program number must be less than 128.")

}

object Phrase {

  val GrandPiano = 0
  val Keyboard = 4
  val AcousticGuitar = 25
  val ElectricGuitar = 27
  val Violin = 40
  val Viola = 41
  val Cello = 42
  val Trumpet = 56
  val Trombone = 57
  val Tuba = 58
  val FrenchHorn = 60
  val AltoSax = 65
  val TenorSax = 66
  val Oboe = 68
  val Clarinet = 71
  val Flute = 73
  val Applause = 126

}

/**
 * A set of chords played sequentially. Voices are combined to form a [[Phrase]]. Chords are stored
 * sequentially in playback order. Voices are immutable, and, therefore, thread-safe.
 *
 * @param chords Chords to be sequentially played.
 */
@SerialVersionUID(1L)
case class Voice(
  chords: Seq[Chord]
) extends MusicElement

/**
 * A set of notes sounded simultaneously for a particular duration. Chords, therefore, encode the
 * rhythmic, and harmonic properties of a set of notes. Unlike many traditional definitions, a chord
 * may contain any number of notes, or no notes at all; in fact, musical rests are represented as
 * empty chords! Chords are combined together to form a [[Voice]]. Chords are immutable, and,
 * therefore, thread-safe.
 *
 * @param notes Notes in the chord.
 * @param duration Duration of the chord.
 */
@SerialVersionUID(1L)
case class Chord(
  notes: Set[Note],
  duration: Duration
) extends MusicElement

/**
 * A musical note. Notes contains pitch and volume.
 * A note's volume is encoded as a number on the interval [0, 1] in which 0 represents silence and
 * 1 represents maximum volume. Notes are ordered by their octave and pitch and not by their volume.
 * In other words, notes are partitioned into equivalence classes by their frequencies and not their
 * amplitudes. Notes are combined together to form a [[Chord]]. Notes are immutable, and, therefore,
 * thread-safe.
 *
 * @param pitch Pitch of the note.
 * @param volume Dynamics of the note.
 */
@SerialVersionUID(1L)
case class Note(
  pitch: Pitch,
  volume: Double
) extends MusicElement {

  require(volume >= 0, "Volume must be positive.")
  require(volume <= 1, "Volume must be no more than 1.")

}

/**
 * A note position. Pitch is represented by a position and an octave. The position is encoded as a
 * number between [0, 11] and corresponds to a number of half-steps above C. Therefore, the note C
 * has a pitch of 0, while the note G has a pitch of 7. The octave may be any number with the
 * arbitrary rule that the position C in octave 5 is middle-C. In general, midi can only encode
 * octaves -1 to 9. Pitch is immutable, and, therefore, thread-safe.
 *
 * @param position Position of the pitch.
 * @param octave Octave of the pitch.
 */
case class Pitch(
  position: Int,
  octave: Int
) extends MusicElement with Ordered[Pitch] {

  require(this.position >= 0, "Position must be positive.")
  require(this.position <= 11, "Position must be no more than 11.")

  override def compare(that: Pitch): Int = {
    import scala.math.Ordered.orderingToOrdered
    (this.octave, this.position).compare(that.octave, that.position)
  }

  /**
   * Returns the frequency in hertz (Hz) of the note. The fundamental frequency f0 is set to the
   * frequency of A5 (69) and is fixed at 440 Hz and f(n) = f(A5) * 2^(n-49)/12^ is defined to be
   * the frequency of the of the note n half-steps away from note 69.
   *
   * @return Frequency of the note in hertz.
   */
  def frequency: Double =
    math.pow(2.0, (this.octave * 12 + this.position - 69) / 12.0) * 440

}

object Pitch {

  val C = 0
  val Cx = 1
  val Db = Cx
  val D = 2
  val Dx = 3
  val Eb = Dx
  val E = 4
  val Fb = E
  val F = 5
  val Fx = 6
  val Gb = Fx
  val G = 7
  val Gx = 8
  val Ab = Gx
  val A = 9
  val Ax = 10
  val Bb = Ax
  val B = 11
  val Cb = B

}

/**
 * A musical duration. Durations are represented as a particular number of beats in a particular
 * meter. For example, a quarter-note (1/4) is represented as a duration of 1 beat of a meter of 4.
 * Typically, durations have a meter that is a power of 2. Durations are immutable, and, therefore,
 * thread-safe.
 *
 * @param beats Number of time intervals for the chord.
 * @param meter Length of time intervals for the chord.
 */
case class Duration (
  beats: Int,
  meter: Int
) extends MusicElement with Ordered[Duration] {

  require(beats > 0, "Beats must be positive.")
  require(meter > 0, "Meter must be positive.")

  override def compare(that: Duration): Int =
    this.beats * that.meter - that.beats * this.meter

  /**
   * Returns a reduced duration in which the beats and meters are relatively prime. Implementation
   * utilizes Euclid's Algorithm to find the greatest common denominator of the meter, and then
   * divides the beat and meter parameters by this value.
   *
   * @return Reduced duration.
   */
  def reduce: Duration = {
    def gcd(a: Int, b: Int): Int = if (b == 0) a else gcd(b, a % b)
    val mul = gcd(this.beats, this.meter)
    Duration(this.beats / mul, this.meter / mul)
  }

}