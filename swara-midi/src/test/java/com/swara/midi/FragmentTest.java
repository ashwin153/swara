package com.swara.midi;

import java.io.File;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;

import org.apache.commons.lang3.math.Fraction;
import org.junit.Test;

public class FragmentTest {

    @Test
    public void testWrite() throws Exception {
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
            .withNote(new Note.Builder().withPitch(Note.C, 4).withVolume(80).build())
            .withNote(new Note.Builder().withPitch(Note.E, 4).withVolume(60).build())
            .withNote(new Note.Builder().withPitch(Note.G, 5).withVolume(75).build())
            .withNote(new Note.Builder().withPitch(Note.B, 5).withVolume(90).build());

        // G Major 7 Chord.
        final Chord.Builder gmaj7 = new Chord.Builder()
            .withNote(new Note.Builder().withPitch(Note.G, 4).withVolume(80).build())
            .withNote(new Note.Builder().withPitch(Note.B, 4).withVolume(60).build())
            .withNote(new Note.Builder().withPitch(Note.D, 5).withVolume(75).build())
            .withNote(new Note.Builder().withPitch(Note.F, 5).withVolume(90).build());

        // Piano Phrase.
        final Phrase piano = new Phrase.Builder()
            .withProgram(0)
            .withChord(cmaj7.withDuration(Fraction.ONE_QUARTER).build())
            .withChord(gmaj7.withDuration(Fraction.ONE_HALF).build())
            .withChord(gmaj7.withDuration(Fraction.ONE_QUARTER).build())
            .build();

        // Guitar Phrase.
        final Phrase guitar = new Phrase.Builder()
            .withProgram(25)
            .withChord(cmaj7.withDuration(Fraction.getFraction(1, 8)).build())
            .withChord(gmaj7.withDuration(Fraction.getFraction(1, 8)).build())
            .withChord(cmaj7.withDuration(Fraction.getFraction(3, 4)).build())
            .build();

        // Song Fragment.
        final Fragment fragment = new Fragment.Builder()
            .withKey(cmajor)
            .withTempo(waltz)
            .withPhrase(0, piano)
            .withPhrase(1, guitar)
            .build();

        // Write to File.
        final Sequence sequence = new Sequence(Sequence.PPQ, 480);
        fragment.write(sequence);
        fragment.write(sequence);
        MidiSystem.write(sequence, 1, new File("./test.mid"));
    }



}
