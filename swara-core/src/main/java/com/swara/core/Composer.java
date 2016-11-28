package com.swara.core;

import java.util.List;
import java.util.stream.Stream;

import com.swara.music.elements.Chord;
import com.swara.music.elements.Voice;

/**
 * A composer generates an original stream of {@link Chord}. Composers utilize a variety of machine
 * learning methods to create orginal, but representative music!
 */
@FunctionalInterface
public interface Composer {

    Stream<Chord> compose(List<Voice> examples);

}
