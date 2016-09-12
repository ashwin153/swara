package com.swara.music.data;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Preconditions;

/**
 * A set of phrases played simultaneously in a particular {@link Key} and {@link Tempo}. Fragments
 * are combined together to form a song. Fragments are built using a {@link Fragment.Builder} and
 * are immutable and, therefore, thread-safe.
 */
@JsonDeserialize(builder = Fragment.Builder.class)
public class Fragment {

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
     * Returns the mapping of phrases to channels. A MIDI sequence is composed of 16 channels over
     * which information may be passed. Each phrase in the fragment is assigned to a unique channel.
     * Channel 10 is reserved for percussion.
     */
    @JsonGetter
    public Map<Integer, Phrase> phrases() {
        return this.phrases;
    }

    /**
     * Constructs a {@link Fragment} using a Fluent-style builder pattern. By default, the builder
     * will construct an empty fragment with the default key and tempo.
     */
    public static final class Builder {

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

        public Builder withPhrases(Map<Integer, Phrase> phrases) {
            // The phrase mapping may not be null.
            Preconditions.checkNotNull(phrases);
            this.phrases = phrases;
            return this;
        }

        public Fragment build() {
            return new Fragment(this);
        }
    }

}
