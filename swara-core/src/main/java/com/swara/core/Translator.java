package com.swara.core;

import javax.sound.sampled.AudioInputStream;

import com.swara.music.elements.Song;

/**
 * A translator is the musical equivalent of speech-to-text; it translates audio input (.mp3, .wav,
 * etc.) into their musical representations.
 */
@FunctionalInterface
public interface Translator {

    Song translate(AudioInputStream stream);

}
