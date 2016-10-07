package com.swara.music.elements;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Preconditions;
import com.swara.music.MusicElement;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * A set of {@link Phrase} played simultaneously in a particular {@link Key} and {@link Tempo}.
 * Fragments are the building blocks of a {@link Song}; a song is simply a set of fragments played
 * sequentially. Fragments are built using a {@link Fragment.Builder} and are immutable and,
 * therefore, thread-safe. The default fragment is an empty set of phrases in the default key/tempo.
 */
@ToString
@EqualsAndHashCode
@JsonDeserialize(builder = Fragment.Builder.class)
public class Fragment implements MusicElement, Serializable {

    private static final long serialVersionUID = -5666974680990877863L;

    private final Key key;
    private final Tempo tempo;
    private final Map<Integer, Phrase> phrases;

    private Fragment(Builder builder) {
        this.key = builder.key;
        this.tempo = builder.tempo;
        this.phrases = builder.phrases;
    }

    /**
     * Returns the key in which all the phrases in the fragment are written.
     */
    @JsonGetter
    public Key key() {
        return this.key;
    }

    /**
     * Returns the tempo in which all the phrases in the fragment are written.
     */
    @JsonGetter
    public Tempo tempo() {
        return this.tempo;
    }

    /**
     * Returns the mapping of phrases to channels. Communication with a Midi sequencer occurs over
     * sixteen channels. Each channel represents an independent part in a musical score. The general
     * Midi (GM) specification dictates that percussion must occur on channel 10 and all other
     * instruments may be played on the other channels.
     */
    @JsonGetter
    public Map<Integer, Phrase> phrases() {
        return this.phrases;
    }

    public static final class Builder implements MusicElement.Builder<Fragment> {

        private Key key;
        private Tempo tempo;
        private Map<Integer, Phrase> phrases;

        public Builder() {
            this.key = new Key.Builder().build();
            this.tempo = new Tempo.Builder().build();
            this.phrases = new HashMap<>();
        }

        public Builder withKey(Key key) {
            // The key may not be null.
            Preconditions.checkNotNull(key);
            this.key = key;
            return this;
        }

        public Builder withTempo(Tempo tempo) {
            // The tempo may not be null.
            Preconditions.checkNotNull(tempo);
            this.tempo = tempo;
            return this;
        }

        public Builder withPhrase(int channel, Phrase phrase) {
            // The channel must be between 0 and 16, and the phrase may not be null.
            Preconditions.checkArgument(channel >= 0 && channel < 16);
            Preconditions.checkNotNull(phrase);
            this.phrases.put(channel, phrase);
            return this;
        }

        public Builder withPhrases(Map<? extends Integer, ? extends Phrase> phrases) {
            // The phrase mapping may not be null.
            Preconditions.checkNotNull(phrases);
            this.phrases.putAll(phrases);
            return this;
        }

        public Fragment build() {
            return new Fragment(this);
        }
    }

}
