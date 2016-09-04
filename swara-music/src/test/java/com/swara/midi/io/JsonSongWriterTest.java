package com.swara.midi.io;

import java.io.ByteArrayOutputStream;

import com.swara.music.io.JsonSongWriter;
import com.swara.music.model.Chord;
import com.swara.music.model.Fragment;
import com.swara.music.model.Key;
import com.swara.music.model.Note;
import com.swara.music.model.Phrase;
import com.swara.music.model.Song;
import com.swara.music.model.Tempo;

import org.apache.commons.math3.fraction.Fraction;
import org.junit.Test;

public class JsonSongWriterTest {

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
            .withChord(cmaj7.withDuration(new Fraction(1, 8)).build())
            .withChord(gmaj7.withDuration(new Fraction(1, 8)).build())
            .withChord(cmaj7.withDuration(new Fraction(3, 4)).build())
            .build();

        // Song Fragment.
        final Fragment fragment = new Fragment.Builder()
            .withKey(cmajor)
            .withTempo(waltz)
            .withPhrase(0, piano)
            .withPhrase(1, guitar)
            .build();

        // Write to File.
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        new JsonSongWriter().write(out, new Song.Builder()
            .withFragment(fragment)
            .build()
        );
    }

}
