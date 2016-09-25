package com.swara.music.features;

import com.swara.music.MusicFeature;
import com.swara.music.elements.Song;

public class Harmony implements MusicFeature {

    public static class Extractor implements MusicFeature.Extractor<Harmony> {

        @Override
        public Harmony extract(Song song) {
            throw new UnsupportedOperationException();
        }

    }

}
