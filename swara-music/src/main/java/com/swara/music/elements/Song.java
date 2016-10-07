package com.swara.music.elements;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Preconditions;
import com.swara.music.MusicElement;
import com.swara.music.MusicReader;
import com.swara.music.MusicWriter;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * A list of fragments played sequentially. Songs can be read from various input sources using the
 * {@link MusicReader} and written to various output sources using the {@link MusicWriter}. Songs
 * are immutable, and, therefore, thread-safe, and are constructed using the {@link Song.Builder}.
 * Songs are the top-level {@link MusicElement}. By default, a song consists of an empty list of
 * fragments.
 */
@ToString
@EqualsAndHashCode
@JsonDeserialize(builder = Song.Builder.class)
public class Song implements MusicElement, Serializable {

    private static final long serialVersionUID = 7959312166736630115L;

    private final List<Fragment> fragments;

    private Song(Builder builder) {
        this.fragments = builder.fragments;
    }

    /**
     * Returns a list of the fragments that comprise the song. Fragments are retained in playback
     * order, and, together, they contain all the musical elements of the song.
     */
    @JsonGetter
    public List<Fragment> fragments() {
        return this.fragments;
    }

    public static final class Builder implements MusicElement.Builder<Song>{

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
