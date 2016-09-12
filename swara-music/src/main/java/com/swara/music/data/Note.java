package com.swara.music.data;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ComparisonChain;

/**
 * A musical note. Notes have a pitch and a volume. Notes are combined together to form a
 * {@link Chord}. Notes are built using a {@link Note.Builder} immutable, and, therefore,
 * thread-safe.
 */
@JsonDeserialize(builder = Note.Builder.class)
public class Note implements Comparable<Note> {

    public static final int C  = 0;
    public static final int Cx = 1;
    public static final int Db = Cx;
    public static final int D  = 2;
    public static final int Dx = 3;
    public static final int Eb = Dx;
    public static final int E  = 4;
    public static final int F  = 5;
    public static final int Fx = 6;
    public static final int Fb = Fx;
    public static final int G  = 7;
    public static final int Gx = 8;
    public static final int Ab = Gx;
    public static final int A  = 9;
    public static final int Ax = 10;
    public static final int Bb = Ax;
    public static final int B  = 11;

    private final int pitch;
    private final int octave;
    private final int volume;

    private Note(Builder builder) {
        this.pitch = builder.pitch;
        this.octave = builder.octave;
        this.volume = builder.volume;
    }

    /**
     * Returns the MIDI pitch of the note. The pitch is encoded as a number on the interval
     * [0, 128), in which 0 represents C and each successive number represents a half-step higher.
     */
    @JsonGetter
    public int pitch() {
        return this.pitch;
    }

    /**
     *
     */
    @JsonGetter
    public int octave() { return this.octave; }

    /**
     * Returns the volume of the note. Volume is synonymous with MIDI velocity. The volume is
     * encoded as a number on the interval [0, 128), in which 0 represents silence.
     */
    @JsonGetter
    public int volume() {
        return this.volume;
    }

    @Override
    public int compareTo(Note rhs) {
        return ComparisonChain.start()
            .compare(this.pitch, rhs.pitch)
            .compare(this.octave, rhs.octave)
            .result();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }

        Note rhs = (Note) obj;
        return this.pitch == rhs.pitch && this.octave == rhs.octave;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.pitch, this.octave);
    }

    /**
     * Constructs a {@link Note} using a Fluent-style builder pattern. By default, the builder will
     * construct a medium volume, middle-C.
     */
    public static final class Builder {

        private int pitch;
        private int octave;
        private int volume;

        public Builder() {
            this.pitch = Note.C;
            this.octave = 5;
            this.volume = 64;
        }

        public Builder withPitch(int pitch) {
            // The pitch must be on the interval [0, 11].
            Preconditions.checkArgument(pitch >= 0 && pitch <= 11);
            this.pitch = pitch;
            return this;
        }

        public Builder withOctave(int octave) {
            // The octave must be on the interval [0, 11).
            Preconditions.checkArgument(octave >= 0);
            this.octave = octave;
            return this;
        }

        public Builder withVolume(int volume) {
            // The volume must be on the interval [0, 128).
            Preconditions.checkArgument(volume >= 0 && volume < 128);
            this.volume = volume;
            return this;
        }

        public Note build() {
            return new Note(this);
        }
    }

}
