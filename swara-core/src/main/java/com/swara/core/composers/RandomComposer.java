package com.swara.core.composers;

import java.time.Duration;
import java.util.Random;

import com.swara.core.Composer;
import com.swara.music.elements.Song;

/**
 *
 */
public class RandomComposer implements Composer {

    public final Random rng;

    public RandomComposer() {
        this.rng = new Random();
    }

    @Override
    public Song compose(Duration duration) {
        throw new UnsupportedOperationException();
    }

}
