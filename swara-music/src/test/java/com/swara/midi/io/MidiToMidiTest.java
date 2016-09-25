package com.swara.midi.io;

import java.io.File;

import com.swara.music.MusicReader;
import com.swara.music.MusicWriter;
import com.swara.music.elements.Chord;
import com.swara.music.elements.Fragment;
import com.swara.music.elements.Key;
import com.swara.music.elements.Note;
import com.swara.music.elements.Phrase;
import com.swara.music.elements.Song;
import com.swara.music.elements.Tempo;
import com.swara.music.elements.Voice;
import com.swara.music.readers.MidiReader;
import com.swara.music.writers.MidiWriter;

import org.apache.commons.math3.fraction.Fraction;
import org.junit.Test;

public class MidiToMidiTest {

    @Test
    public void testReadWrite() throws Exception {

// C Major Key.
        final Key cmajor = new Key.Builder()
            .withSignature(1)
            .withType(Key.MAJOR)
            .build();

// Common Tempo.
        final Tempo common = new Tempo.Builder()
            .withSignature(4, 4)
            .withBpm(120)
            .build();

// C Major Chord.
        final Chord.Builder cmaj = new Chord.Builder()
            .withNote(new Note.Builder().withPitch(Note.C).withOctave(4).build())
            .withNote(new Note.Builder().withPitch(Note.E).withOctave(4).build())
            .withNote(new Note.Builder().withPitch(Note.G).withOctave(4).build());

        final Chord.Builder cmaj7 = new Chord.Builder()
            .withNote(new Note.Builder().withPitch(Note.B).withOctave(3).build());

// G Major 7 Chord.
        final Chord.Builder gmaj = new Chord.Builder()
            .withNote(new Note.Builder().withPitch(Note.G).withOctave(4).build())
            .withNote(new Note.Builder().withPitch(Note.B).withOctave(4).build())
            .withNote(new Note.Builder().withPitch(Note.D).withOctave(5).build());

        final Chord.Builder gmaj7 = new Chord.Builder()
            .withNote(new Note.Builder().withPitch(Note.F).withOctave(3).build());

// Piano Phrase.
        final Phrase piano = new Phrase.Builder()
            .withProgram(Phrase.GRAND_PIANO)
            .withVoice(new Voice.Builder()
                .withChord(cmaj.withDuration(new Fraction(1, 4)).withVolume(42).build())
                .withChord(gmaj.withDuration(new Fraction(1, 2)).withVolume(64).build())
                .withChord(cmaj.withDuration(new Fraction(1, 4)).withVolume(80).build())
                .build())
            .withVoice(new Voice.Builder()
                .withChord(cmaj7.withDuration(new Fraction(1, 4)).withVolume(42).build())
                .withChord(gmaj7.withDuration(new Fraction(1, 4)).withVolume(64).build())
                .withChord(cmaj7.withDuration(new Fraction(1, 4)).withVolume(64).build())
                .withChord(cmaj7.withDuration(new Fraction(1, 16)).withVolume(80).build())
                .withChord(gmaj7.withDuration(new Fraction(1, 16)).withVolume(80).build())
                .withChord(cmaj7.withDuration(new Fraction(1, 16)).withVolume(80).build())
                .withChord(gmaj7.withDuration(new Fraction(1, 16)).withVolume(80).build())
                .build())
            .build();

// Guitar Phrase.
        final Phrase guitar = new Phrase.Builder()
            .withProgram(Phrase.ACOUSTIC_GUITAR)
            .withVoice(new Voice.Builder()
                .withChord(cmaj.withDuration(new Fraction(1, 8)).withVolume(80).build())
                .withChord(gmaj7.withDuration(new Fraction(1, 8)).withVolume(64).build())
                .withChord(gmaj7.withDuration(new Fraction(3, 4)).withVolume(42).build())
                .build())
            .build();

// Song Fragment.
        final Fragment fragment = new Fragment.Builder()
            .withKey(cmajor)
            .withTempo(common)
            .withPhrase(0, piano)
            .withPhrase(1, guitar)
            .build();

// Song.
        final Song song = new Song.Builder()
            .withFragment(fragment)
            .build();
        final MusicReader reader = new MidiReader();
        final MusicWriter writer = new MidiWriter();

        // Read the test file and write it to the result file.
//        final Song song = reader.read(new File("./src/test/resources/test.mid"));
        writer.write(new File("./src/test/resources/test-result.mid"), song);
    }

}
