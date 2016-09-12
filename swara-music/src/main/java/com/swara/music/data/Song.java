package com.swara.music.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Preconditions;
import com.swara.music.io.SongReader;
import com.swara.music.io.SongWriter;

/**
 * A list of fragments played sequentially. Songs can be read from various input sources using the
 * {@link SongReader} and written to various output sources using the {@link SongWriter}. Songs are
 * immutable, and, therefore, thread-safe, and are constructed using the {@link Song.Builder}.
 */
@JsonDeserialize(builder = Song.Builder.class)
public class Song {

    private final List<Fragment> fragments;

    private Song(Builder builder) {
        this.fragments = builder.fragments;
    }

    /**
     * Returns a mutable copy of all the fragments that comprise the song in playback order.
     */
    @JsonGetter
    public List<Fragment> fragments() {
        return this.fragments;
    }

    /**
     * Constructs a {@link Song} using a Fluent-style builder pattern. By default, the builder will
     * construct an empty song with no fragments.
     */
    public static final class Builder {

        private List<Fragment> fragments;

        public Builder() {
            this.fragments = new ArrayList<>();
        }

        public Builder withFragment(Fragment fragment) {
            // The fragment may not be null.
            Preconditions.checkNotNull(fragment);
            this.fragments.add(fragment);
            return this;
        }

        public Builder withFragments(Collection<? extends Fragment> fragments) {
            // The fragments may not be null.
            Preconditions.checkNotNull(fragments);
            this.fragments.addAll(fragments);
            return this;
        }

        public Song build() {
            return new Song(this);
        }

    }

}
