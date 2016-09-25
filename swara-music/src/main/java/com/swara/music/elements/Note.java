package com.swara.music.elements;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Preconditions;
import com.swara.music.MusicElement;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * A musical note. Notes represent pitch. Notes are combined together to form a {@link Chord}. Notes
 * are built using a {@link Note.Builder} immutable, and, therefore, thread-safe. The default note
 * is a middle-C.
 */
@ToString
@EqualsAndHashCode
@JsonDeserialize(builder = Note.Builder.class)
public class Note implements MusicElement {

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

    private Note(Builder builder) {
        this.pitch = builder.pitch;
        this.octave = builder.octave;
    }

    /**
     * Returns the pitch of the note. The pitch is encoded as a number on the interval [0, 11], in
     * which 0 represents C and each successive number represents a half-step higher.
     */
    @JsonGetter
    public int pitch() {
        return this.pitch;
    }

    /**
     * Returns the octave of the note. The octave is on the interval [0, 11], in which 4 represents
     * the middle octave.
     */
    @JsonGetter
    public int octave() { return this.octave; }

    public static final class Builder implements MusicElement.Builder<Note> {

        private int pitch;
        private int octave;

        public Builder() {
            this.pitch = Note.C;
            this.octave = 5;
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

        public Note build() {
            return new Note(this);
        }
    }

}
