# Swara Music
The ```swara-music``` library defines the various data structures required to programmatically model musical information and provides interfaces for reading from and writing to various musical file formats.

## Data Structures
* ```Pitch``` is a musical pitch.
* ```Note``` is a pitch and a volume.
* ```Chord``` is a collection of notes played simultaneously for some duration.
* ```Voice``` is a collection of chords played simultaneously.
* ```Phrase``` is a collection of voices played simultaneously for a particular instrument.
* ```Fragment``` is a collection of phrases played simultaneously in a particular key and tempo.
* ```Song``` is a sequence of fragments played sequentially.

## I/O
The library exposes the ```MusicReader``` and ```MusicWriter``` interfaces to encourage interoperability between the internal musical representation and external musical formats and includes implementations for [MIDI](https://en.wikipedia.org/wiki/MIDI) and JSON.

## Example
For example, this [code](https://gist.github.com/ashwin153/d86292dbfc346b48d7e8f9e79db463fd) produces the following fragment of sheet music (rendered using [MuseScore 2](https://musescore.org/en/2.0)). The fragment is marked up with examples of the musical data structures described above and highlights some of libraries cool features including support for: key and tempo changes, polyphony, multiple instruments, dynamics, and accidentals!

![Sample Song](https://raw.githubusercontent.com/ashwin153/swara/master/swara-assets/swara-pics/sample-song.png)

## Attribution
Here's a little page-rank karma for all the sources I used:
- [Midi Guide #1](http://www.somascape.org/midi/tech/mfile.html)
- [Midi Guide #2](https://www.csie.ntu.edu.tw/~r92092/ref/midi/)
- [Effective Java](https://github.com/HackathonHackers/programming-ebooks/blob/master/Java/Effective%20Java%20(2nd%20Edition).pdf)
- [MuseScore 2](https://musescore.org/en/2.0)
