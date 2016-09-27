package com.swara.core.translators;

import javax.sound.sampled.AudioInputStream;

import com.swara.core.Translator;
import com.swara.music.elements.Song;

/**
 * Like many modern speech-to-text systems the Markov translator relies on Hidden Markov Models,
 * but instead of extracting words it extracts chords!
 */
public class MarkovTranslator implements Translator {

    @Override
    public Song translate(AudioInputStream stream) {
        throw new UnsupportedOperationException();
    }

}
