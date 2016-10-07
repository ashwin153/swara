package com.swara.music.elements;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Preconditions;
import com.swara.music.MusicElement;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * A musical tempo. Tempo specify the number of beats (quarter-notes) per minute as well as a time
 * signature. Tempos are built with {@link Tempo.Builder} and are immutable, and, therefore,
 * thread-safe. The default tempo is 120 BPM in common time (4/4).
 */
@ToString
@EqualsAndHashCode
@JsonDeserialize(builder = Tempo.Builder.class)
public class Tempo implements MusicElement, Serializable {

    private static final long serialVersionUID = 3472258255797173960L;

    private final int type;
    private final int beats;
    private final double bpm;

    private Tempo(Builder builder) {
        this.type = builder.type;
        this.beats = builder.beats;
        this.bpm = builder.bpm;
    }

    /**
     * Returns the number of beats (quarter-notes) per second. For example, a classical piece in
     * adagio would likely be played at around 60 bpm, while a piece in allegro would likely be
     * played at around 140 bpm.
     */
    @JsonGetter
    public double bpm() {
        return this.bpm;
    }

    /**
     * Returns the numerator of the time signature. The numerator corresponds to the number of
     * beats of the specified note type in a measure.
     */
    @JsonGetter
    public int beats() {
        return this.beats;
    }

    /**
     * Returns the denominator of the time signature. The denominator corresponds to the type of
     * beat (half-note is 2, quarter-note is 4, etc.) For example, a piece in the common time
     * signature (4/4) would have 4 quarter-notes per measure.
     */
    @JsonGetter
    public int type() {
        return this.type;
    }

    public static final class Builder implements MusicElement.Builder<Tempo> {

        private double bpm;
        private int beats;
        private int type;

        public Builder() {
            this.bpm = 120;
            this.beats = 4;
            this.type = 4;
        }

        public Builder withBpm(double bpm) {
            // Beats per minute may not be negative.
            Preconditions.checkArgument(bpm > 0);
            this.bpm = bpm;
            return this;
        }

        @JsonIgnore
        public Builder withSignature(int beats, int type) {
            // Beats and type must both be positive and type must be a power of 2.
            Preconditions.checkArgument(beats > 0 && type > 0);
            Preconditions.checkArgument((type & -type) == type);
            this.beats = beats;
            this.type = type;
            return this;
        }

        public Tempo build() {
            return new Tempo(this);
        }
    }

}
