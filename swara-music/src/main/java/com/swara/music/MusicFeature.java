package com.swara.music;

import com.swara.music.elements.Song;

/**
 * A musical feature. Unlike the {@link MusicElement}, a MusicFeature is not necessary to encode a
 * {@link com.swara.music.elements.Song} as sheet music. MusicFeatures are simply high-level musical
 * attrbutes. For example, a song is <emph>composed</emph> of notes and the notes <emph>form</emph>
 * a chord progression; notes are a musical element and the resulting chord progression is a musical
 * feature. Music features are an important part of any song, and could very well be more useful
 * than music elements for machine learning tasks.
 */
public interface MusicFeature {

    /**
     * An extractor is responsible for extracting a {@link MusicFeature} from a Song.
     */
    @FunctionalInterface
    interface Extractor<T extends MusicFeature> {
        T extract(Song song);
    }

}