package com.swara.core.composers;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.swara.core.Composer;
import com.swara.music.elements.Chord;
import com.swara.music.elements.Voice;

public class RandomComposer implements Composer {

    @Override
    public Stream<Chord> compose(List<Voice> examples) {
        // Aggregate all the various chords in all the example voices.
        final List<Chord> chords = examples.stream()
            .flatMap(voice -> voice.chords().stream())
            .collect(Collectors.toList());

        // Generate a stream of random chords sampled from the aggregated list.
        return Stream.generate(() -> chords.get((int) (Math.random() * chords.size())));
    }

}
