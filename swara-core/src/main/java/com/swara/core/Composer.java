package com.swara.core;

import java.time.Duration;

import com.swara.music.elements.Song;

/**
 * A composer generates an original {@link Song} of the specified duration. Composers utilize
 * various machine learning methods to create music!
 */
@FunctionalInterface
public interface Composer {

    Song compose(Duration duration);

}
