package com.swara.midi;

import com.google.common.base.Preconditions;

/**
 * A musical note. Notes have a pitch and a volume. Notes are combined together to form a
 * {@link com.swara.midi.Chord}. Notes are built using a {@link com.swara.midi.Note.Builder}
 * immutable, and, therefore, thread-safe.
 */
public class Note {

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
    private final int volume;

    private Note(Builder builder) {
        this.pitch = builder.pitch;
        this.volume = builder.volume;
    }

    /**
     * Returns the MIDI pitch of the note. The pitch is encoded as a number on the interval
     * [0, 128), in which 0 represents C and each successive number represents a half-step higher.
     */
    public int pitch() {
        return this.pitch;
    }

    /**
     * Returns the volume of the note. Volume is synonymous with MIDI velocity. The volume is
     * encoded as a number on the interval [0, 128), in which 0 represents silence.
     */
    public int volume() {
        return this.volume;
    }

    /**
     * Constructs a {@link com.swara.midi.Note} using a Fluent-style builder pattern. By default,
     * the builder will construct a medium volume, middle-C.
     */
    public static final class Builder {

        private int pitch;
        private int volume;

        public Builder() {
            this.pitch = 60;
            this.volume = 64;
        }

        public Builder withPitch(int note, int octave) {
            // The pitch must be on the interval [0, 128).
            Preconditions.checkArgument(note >= 0 && note < 11);
            Preconditions.checkArgument(octave >= 0 && octave < 11);
            this.pitch = octave * 12 + note;
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
