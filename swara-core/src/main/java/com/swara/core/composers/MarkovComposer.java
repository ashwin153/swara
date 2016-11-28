package com.swara.core.composers;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.google.common.collect.ConcurrentHashMultiset;
import com.google.common.collect.Iterators;
import com.google.common.collect.Multiset;
import com.google.common.collect.Ordering;
import com.google.common.primitives.Ints;
import com.swara.core.Composer;
import com.swara.learn.markov.DiscreteMarkovChain;
import com.swara.music.elements.Chord;
import com.swara.music.elements.Key;
import com.swara.music.elements.Note;
import com.swara.music.elements.Voice;

import org.apache.commons.math3.fraction.Fraction;

/**
 * A markov composer utilizes a number of {@link DiscreteMarkovChain} to synthesize music that is
 * oringal, but representative of the example voices. Markov methods have been used with varying
 * degrees of success in musical synthesis before; however, what is novel are the techniques
 * that significantly reduce the size state space, which makes these methods more tractable.
 */
public class MarkovComposer implements Composer {

    private final Key key;

    public MarkovComposer(Key key) {
        this.key = key;
    }

    @Override
    public Stream<Chord> compose(List<Voice> examples) {
        // Create and train markov chains to generate chord durations, volumes and notes. Because
        // the markov chain implementation is thread-safe, we can train them on all the sample
        // songs in parallel. This will significantly reduce train time for large datasets.
        final DiscreteMarkovChain<Fraction> rhythm = new DiscreteMarkovChain<>(5, Fraction::compareTo);
        final DiscreteMarkovChain<Integer> dynamics = new DiscreteMarkovChain<>(2, Integer::compareTo);
        final DiscreteMarkovChain<Iterable<Integer>> harmony = new DiscreteMarkovChain<>(2, Ordering.natural().lexicographical());
        final Map<Iterable<Integer>, Multiset<Set<Note>>> types = new ConcurrentHashMap<>();

        examples.stream().parallel().forEach(voice -> {
            // Remove all chords that have notes that are not in the key for now. Accidentals
            // significantly increase the size of the state space and make it extremely difficult
            // to produce high quality music.
            final List<Chord> chords = voice.chords().stream().filter(chord -> chord.notes().stream()
                .map(Note::pitch)
                .map(pitch -> Ints.indexOf(key.scale(), pitch))
                .min(Integer::compareTo)
                .orElse(-1) >= 0
            ).collect(Collectors.toList());

            rhythm.train(chords.stream()
                .map(Chord::duration)
                .collect(Collectors.toList())
            );

            dynamics.train(chords.stream()
                .map(chord -> chord.volume() / 16)
                .collect(Collectors.toList())
            );

            final List<Iterable<Integer>> classes = chords.stream()
                .map(Chord::type)
                .collect(Collectors.toList());

            harmony.train(classes);
            IntStream.range(0, classes.size()).parallel().forEach(i -> {
                types.putIfAbsent(classes.get(i), ConcurrentHashMultiset.create());
                types.get(classes.get(i)).add(chords.get(i).notes());
            });
        });

        // Use the trained markov chains to crate state iterators.
        final Iterator<Fraction> durations = rhythm.generate();
        final Iterator<Integer> volumes = dynamics.generate();
        final Iterator<Set<Note>> notes = Iterators.transform(harmony.generate(), pitch -> {
            final Multiset<Set<Note>> type = types.get(pitch);
            final Iterator<Set<Note>> members = type.iterator();

            Set<Note> next = null;
            int total = (int) (Math.random() * type.size());
            while (total >= 0 && members.hasNext()) {
                next = members.next();
                total -= type.count(next);
            }

            return next;
        });

        // Use the state iterators to create an infinite, but lazily computed stream of chords.
        return Stream.generate(() -> new Chord.Builder()
            .withNotes(notes.next())
            .withDuration(durations.next())
            .withVolume(volumes.next() * 16)
            .build()
        );
    }

}
