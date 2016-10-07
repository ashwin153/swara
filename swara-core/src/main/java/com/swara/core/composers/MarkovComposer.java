package com.swara.core.composers;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
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
import com.swara.ml.markov.DiscreteMarkovChain;
import com.swara.music.elements.Chord;
import com.swara.music.elements.Key;
import com.swara.music.elements.Note;
import com.swara.music.elements.Voice;

import org.apache.commons.math3.fraction.Fraction;

/**
 *
 */
public class MarkovComposer implements Composer {

    private final Random random;
    private final Key key;

    public MarkovComposer(Key key) {
        this.key = key;
        this.random = new Random();
    }

    @Override
    public Stream<Chord> compose(List<Voice> voices) {
        // Create markov chains to generate chord durations, chord volumes and chord notes. Chords
        // are partitioned into equivalence classes or 'families' by the ordered set of the pitches
        // of the notes that compose it. For example, all c major chords belong to the family
        // {0, 4, 7}. Because these equivalence classes are ordered by construction, they can be
        // compared lexicographically. By partitioning chords into equivalence classes, we
        // drastically reduce the state space and make Markov methods more tractable. We can recover
        // actual chords by storing a mapping between families and the various chords within them.
        final DiscreteMarkovChain<Fraction> rhythm = new DiscreteMarkovChain<>(5, Fraction::compareTo);
        final DiscreteMarkovChain<Integer> dynamics = new DiscreteMarkovChain<>(2, Integer::compareTo);
        final DiscreteMarkovChain<Iterable<Integer>> harmony = new DiscreteMarkovChain<>(2, Ordering.natural().lexicographical());
        final Map<Iterable<Integer>, Multiset<Set<Note>>> families = new ConcurrentHashMap<>();

        // Because the markov chain implementation is thread-safe, we can train them on all the
        // sample voices in parallel. This will significantly reduce train time for large datasets.
        voices.stream().parallel().forEach(voice -> {
            // Remove all chords that have notes that are not in the key for now. Accidentals
            // significantly increase the size of the state space and make it extremely difficult
            // to produce high quality music.
            final List<Chord> chords = voice.chords().stream().filter(chord -> chord.notes().stream()
                .map(Note::pitch)
                .map(pitch -> Ints.indexOf(this.key.scale(), pitch))
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
                .map(chord -> chord.notes().stream()
                    .map(Note::pitch)
                    .collect(Collectors.toCollection(TreeSet::new)))
                .collect(Collectors.toList());

            harmony.train(classes);
            IntStream.range(0, classes.size()).parallel().forEach(i -> {
                families.putIfAbsent(classes.get(i), ConcurrentHashMultiset.create());
                families.get(classes.get(i)).add(chords.get(i).notes());
            });
        });

        // Use the trained markov chains to generated a stream of random, but representative chords.
        // This stream is infinite, but lazily computed; therefore, there is a negligible
        // performance impact for iterating over the stream.
        final Iterator<Fraction> durations = rhythm.generate();
        final Iterator<Integer> volumes = dynamics.generate();
        final Iterator<Set<Note>> notes = Iterators.transform(harmony.generate(), pitch -> {
            final Multiset<Set<Note>> family = families.get(pitch);
            final Iterator<Set<Note>> members = family.iterator();
            int total = this.random.nextInt(family.size());

            Set<Note> next = null;
            while (total >= 0 && members.hasNext()) {
                next = members.next();
                total -= family.count(next);
            }

            return next;
        });

        return Stream.generate(() -> new Chord.Builder()
            .withNotes(notes.next())
            .withDuration(durations.next())
            .withVolume(volumes.next() * 16)
            .build()
        );
    }

}
