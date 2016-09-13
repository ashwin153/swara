# Swara Music
Musical data structures.

## Data
### Terminology
Each ```Song``` is composed of a sequence of ```Fragments``` in which each ```Fragment``` represents a group of instruments playing simultaneously in a particular key and tempo. Each instrument is represented by a ```Phrase``` that consists of sequence of ```Chords``` played sequentially. Each ```Chord``` consists of a set of ```Notes``` played simulatenously. Each data structure is constructed using a Fluent-style Builder Pattern.

- ```Song```: symphonies
- ```Fragment```: passages
- ```Key```: scale
- ```Tempo```: beat
- ```Phrase```: melodies
- ```Chord```: harmony and duration
- ```Note```: pitch and volume

### Example
![Rendered Sheet Music](https://raw.githubusercontent.com/ashwin153/swara/master/swara-assets/swara-pics/sheet-music.png)

```java
// C Major Key.
final Key cmajor = new Key.Builder()
    .withSignature(0)
    .withType(Key.MAJOR)
    .build();

// Waltz Tempo.
final Tempo waltz = new Tempo.Builder()
    .withSignature(3, 4)
    .withBpm(88)
    .build();

// C Major 7 Chord.
final Chord.Builder cmaj7 = new Chord.Builder()
    .withNote(new Note.Builder().withPitch(Note.C).withOctave(4).withVolume(80).build())
    .withNote(new Note.Builder().withPitch(Note.E).withOctave(4).withVolume(60).build())
    .withNote(new Note.Builder().withPitch(Note.G).withOctave(5).withVolume(80).build())
    .withNote(new Note.Builder().withPitch(Note.B).withOctave(5).withVolume(75).build());

// G Major 7 Chord.
final Chord.Builder gmaj7 = new Chord.Builder()
    .withNote(new Note.Builder().withPitch(Note.G).withOctave(4).build())
    .withNote(new Note.Builder().withPitch(Note.B).withOctave(4).build())
    .withNote(new Note.Builder().withPitch(Note.D).withOctave(5).build())
    .withNote(new Note.Builder().withPitch(Note.F).withOctave(5).build());

// Piano Phrase.
final Phrase piano = new Phrase.Builder()
    .withChord(cmaj7.withDuration(new Fraction(1, 4)).build())
    .withChord(gmaj7.withDuration(new Fraction(1, 2)).build())
    .withChord(gmaj7.withDuration(new Fraction(1, 4)).build())
    .withProgram(0)
    .build();

// Guitar Phrase.
final Phrase guitar = new Phrase.Builder()
    .withChord(cmaj7.withDuration(new Fraction(1, 8)).build())
    .withChord(gmaj7.withDuration(new Fraction(1, 8)).build())
    .withChord(cmaj7.withDuration(new Fraction(3, 4)).build())
    .withProgram(25)
    .build();

// Song Fragment.
final Fragment fragment = new Fragment.Builder()
    .withKey(cmajor)
    .withTempo(waltz)
    .withPhrase(0, piano)
    .withPhrase(1, guitar)
    .build();

// Song.
final Song song = new Song.Builder()
    .withFragment(fragment)
    .build();
```

## I/O
### Overview
The I/O APIs expose the ```SongWriter``` and ```SongReader``` interface for serializing and deserializing ```Songs``` to and from streams. This provides a convenient solution to interoperability. For example, if you wanted to use machine learning frameworks written in Python ([TensorFlow](https://www.tensorflow.org/), [Theano](http://deeplearning.net/software/theano/), etc.), you would no longer have to rewrite the clunky ```MidiSongWriter``` and ```MidiSongReader```; you could simple do all the heavy-weight MIDI parsing in Java, and pass around comparatively light-weight JSON strings between languages. Another advantage of this approach is that you no longer need a MIDI sequencer to create music; you can write your music as a JSON string in a text editor read it through a ```JsonSongReader``` and into a MIDI file using a  ```MidiSongWriter```. The possibilities are endless!

### Example
```java
// Read song from JSON file.
final SongReader reader = new JsonSongReader();
final Song song = reader.read(new File("test.json"));

// Write song to MIDI file.
final SongWriter writer = new MidiSongWriter();
writer.write(new File("test.midi"), song);
```

## Attribution
Here's a little page-rank karma for all the sources I used:
- [Midi Guide #1](http://www.somascape.org/midi/tech/mfile.html)
- [Midi Guide #2](https://www.csie.ntu.edu.tw/~r92092/ref/midi/)
- [Effective Java](https://github.com/HackathonHackers/programming-ebooks/blob/master/Java/Effective%20Java%20(2nd%20Edition).pdf)
- [MuseScore 2](https://musescore.org/en/2.0)
