package com.swara.core;

import java.util.List;
import java.util.stream.Stream;

import com.swara.music.elements.Chord;
import com.swara.music.elements.Song;
import com.swara.music.elements.Voice;

/**
 * A composer generates an original {@link Song} of the specified duration. Composers utilize
 * various machine learning methods to create music!
 */
@FunctionalInterface
public interface Composer {

    Stream<Chord> compose(List<Voice> voices);

}
