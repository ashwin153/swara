package com.swara.music.features;

import com.swara.music.MusicFeature;
import com.swara.music.elements.Song;

public class Genre implements MusicFeature {

    public static class Extractor implements MusicFeature.Extractor<Genre> {
        @Override
        public Genre extract(Song song) {
            throw new UnsupportedOperationException();
        }
    }

}

