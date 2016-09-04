package com.swara.music.model;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Preconditions;

import org.apache.commons.math3.fraction.Fraction;

/**
 * A musical tempo. Tempo specify the number of beats (quarter-notes) per minute as well as a time
 * signature. Tempos are built with {@link Tempo.Builder} and are immutable, and, therefore,
 * thread-safe.
 */
@JsonDeserialize(builder = Tempo.Builder.class)
public class Tempo {

    public static final Fraction COMMON = new Fraction(4, 4);

    private final int bpm;
    private final Fraction signature;

    private Tempo(Builder builder) {
        this.bpm = builder.bpm;
        this.signature = builder.signature;
    }

    /**
     * Returns the number of beats (quarter-notes) per second. For example, a classical piece in
     * adagio would likely be played at around 60 bpm, while a piece in allegro would likely be
     * played at around 140 bpm.
     */
    @JsonGetter
    public int bpm() {
        return this.bpm;
    }

    /**
     * Returns the time signature. The time signature is composed of a fraction in which the
     * denominator specifies a type of note (half-note is 2, quarter-note is 4, etc.) and the
     * numerator specifies the number of that type of note that comprise a measure of music. For
     * example, a piece in the common time signature (4/4) would have 4 quarter-notes per measure.
     */
    @JsonGetter
    public Fraction signature() {
        return this.signature;
    }

    /**
     * Constructs a {@link Tempo} using a Fluent-style builder pattern. By default, the builder will
     * construct a 120 bpm tempo in common time (4/4).
     */
    public static final class Builder {

        private int bpm;
        private Fraction signature;

        public Builder() {
            this.bpm = 120;
            this.signature = Tempo.COMMON;
        }

        public Builder withBpm(int bpm) {
            // Beats per minute may not be negative.
            Preconditions.checkArgument(bpm > 0);
            this.bpm = bpm;
            return this;
        }

        @JsonIgnore
        public Builder withSignature(int beats, int note) {
            // Beats and note must both be positive and note must be a power of 2.
            Preconditions.checkArgument(beats > 0 && note > 0);
            Preconditions.checkArgument((note & -note) == note);
            this.signature = new Fraction(beats, note);
            return this;
        }

        public Builder withSignature(Fraction sig) {
            // Fraction must not be negative and denominator must be a power of 2.
            Preconditions.checkNotNull(sig);
            Preconditions.checkArgument(sig.compareTo(Fraction.ZERO) > 0);
            Preconditions.checkArgument((sig.getDenominator() & -sig.getDenominator()) == sig.getDenominator());
            this.signature = sig;
            return this;
        }

        public Tempo build() {
            return new Tempo(this);
        }
    }

}
