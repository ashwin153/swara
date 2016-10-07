package com.swara.core.composers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.swara.music.MusicReader;
import com.swara.music.elements.Fragment;
import com.swara.music.elements.Key;
import com.swara.music.elements.Phrase;
import com.swara.music.elements.Song;
import com.swara.music.elements.Tempo;
import com.swara.music.elements.Voice;
import com.swara.music.readers.MidiReader;
import com.swara.music.writers.MidiWriter;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

public class MarkovComposerTest {

    @Test
    public void testCreateDataset() throws Exception {
        // Recursively load all midi files in the specified directory.
        final Collection<File> inputs = FileUtils.listFiles(
            new File("/Users/ashwin/Desktop/swara-midi/classical/Mozart"),
            new String[] { "mid" },
            true
        );

        // Load all the songs in the input files in parallel.
        final List<Song> songs = new ArrayList<>();
        inputs.parallelStream().forEach(file -> {
            try {
                final MusicReader reader = new MidiReader();
                songs.add(reader.read(file));
            } catch (Exception e) {
                System.err.println("Error processing " + file.getName());
            }
        });

        // Save the loaded songs to disk.
        final FileOutputStream output = new FileOutputStream("src/test/resources/mozart.ser");
        final ObjectOutputStream writer = new ObjectOutputStream(output);
        writer.writeObject(songs);
        writer.close();
    }

    @Test
    @SuppressWarnings({ "unchecked" })
    public void testVersion1() throws Exception {
        // Choose a key and tempo.
        final Key key = new Key.Builder()
            .withSignature(0)
            .withType(Key.MAJOR)
            .build();

        final Tempo tempo = new Tempo.Builder()
            .withBpm(60)
            .withSignature(4, 4)
            .build();

        // Load all the saved songs.
        final ObjectInputStream input = new ObjectInputStream(new FileInputStream("src/test/resources/mozart.ser"));
        final List<Song> songs = (List<Song>) input.readObject();
        input.close();

        // Create a piano and a violin phrase using a Markov Composer.
        final MarkovComposer ashwin = new MarkovComposer(key);
        final Function<Integer, Phrase> generator = program -> new Phrase.Builder()
            .withProgram(program)
            .withVoice(new Voice.Builder()
                .withChords(ashwin
                    .compose(songs.stream()
                        .flatMap(song -> song.fragments().stream())
                        .filter(fragment -> fragment.key().equals(key))
                        .flatMap(fragment -> fragment.phrases().values().stream())
                        .filter(phrase -> phrase.program() == program)
                        .flatMap(phrase -> phrase.voices().stream())
                        .collect(Collectors.toList()))
                    .limit(50)
                    .collect(Collectors.toList()))
                .build())
            .build();

        // Write a generated song to file.
        final MidiWriter writer = new MidiWriter();
        final File output = new File("src/test/resources/version1.mid");

        writer.write(output, new Song.Builder()
            .withFragment(new Fragment.Builder()
                .withKey(key)
                .withTempo(tempo)
                .withPhrase(0, generator.apply(Phrase.GRAND_PIANO))
                .withPhrase(1, generator.apply(Phrase.VIOLIN))
                .build())
            .build()
        );
    }

}
