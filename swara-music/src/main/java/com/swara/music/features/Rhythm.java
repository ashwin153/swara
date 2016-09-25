package com.swara.music.features;

import com.swara.music.MusicFeature;
import com.swara.music.elements.Song;

public class Rhythm implements MusicFeature {

    public static class Extractor implements MusicFeature.Extractor<Rhythm> {

        @Override
        public Rhythm extract(Song song) {
            throw new UnsupportedOperationException();
        }

    }

}
