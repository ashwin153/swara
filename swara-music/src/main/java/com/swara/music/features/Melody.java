package com.swara.music.features;

import com.swara.music.MusicFeature;
import com.swara.music.elements.Song;

public class Melody implements MusicFeature {

    public static class Extractor implements MusicFeature.Extractor<Melody> {

        @Override
        public Melody extract(Song song) {
            throw new UnsupportedOperationException();
        }

    }

}
