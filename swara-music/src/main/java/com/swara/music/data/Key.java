package com.swara.music.data;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

/**
 * A musical key. Keys may be major or minor and contain a key signature which specifies which notes
 * are sharp (# or x), which are flat (b), and which are natural. Keys are immutable, and, therefore,
 * thread-safe. Keys are built with a {@link Key.Builder}.
 */
@JsonDeserialize(builder = Key.Builder.class)
public class Key {

    public static final int MAJOR = 0;
    public static final int MINOR = 1;

    private final int type;
    private final int signature;

    private Key(Builder builder) {
        this.type = builder.type;
        this.signature = builder.signature;
    }

    /**
     * Returns the type of key. Keys may be major (0) or minor (1). Major keys tend to be brighter
     * and more lively, while minor keys tend to be more darker and sadder.
     */
    @JsonGetter
    public int type() {
        return this.type;
    }

    /**
     * Returns the key signature. Music theory allows the key signature to be encoded as a single
     * number on the interval [-7, 7] that specifies the number of sharps (+) or flats(-) are in a
     * particular key. For example, C Major and A Minor have a signature of 0, because they have no
     * sharps nor flats.
     */
    @JsonGetter
    public int signature() {
        return this.signature;
    }

    /**
     * Returns the pitches of all notes in the key. The tonic (root; first note of scale) is
     * calculated using the Circle of Fifths, which states that each additional sharp moves the
     * tonic up a perfect fifth (7 half-steps) and each additional flat moves the tonic down a
     * perfect fifth. The notes that comprise a scale are based on the observation that notes in a
     * major scale are 2, 4, 5, 7, 9, and 11 half-steps above tonic and notes in a minor scale are
     * 2, 3, 5, 7, 8, and 10 half-steps above tonic.
     */
    public int[] scale() {
        final int tonic = ((this.type == Key.MAJOR ? Note.C : Note.A) + this.signature * 7) % 12;
        return new int[] {
            tonic,
            tonic + 2,
            tonic + 4 - this.type,
            tonic + 5,
            tonic + 7,
            tonic + 9 - this.type,
            tonic + 11 - this.type
        };
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null || obj.getClass() == this.getClass()) {
            return false;
        }

        Key rhs = (Key) obj;
        return this.signature == rhs.signature && this.type == rhs.type;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.signature, this.type);
    }

    /**
     * Constructs a {@link Key} using a Fluent-style builder pattern. By default, the builder will
     * construct C Major, which is a major key with no sharps nor flats.
     */
    public static final class Builder {

        public int signature;
        public int type;

        public Builder() {
            this.signature = 0;
            this.type = Key.MAJOR;
        }

        public Builder withType(int type) {
            // The type must be major or minor.
            Preconditions.checkArgument(type == Key.MAJOR || type == Key.MINOR);
            this.type = type;
            return this;
        }

        public Builder withSignature(int signature) {
            // The number of sharps or flats may not exceed 7.
            Preconditions.checkArgument(Math.abs(signature) <= 7);
            this.signature = signature;
            return this;
        }

        public Key build() {
            return new Key(this);
        }
    }

}