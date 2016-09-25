package com.swara.music.features;

import com.swara.music.MusicFeature;
import com.swara.music.elements.Song;

public class Dynamics implements MusicFeature {

    public static class Extractor implements MusicFeature.Extractor<Dynamics> {
        @Override
        public Dynamics extract(Song song) {
            throw new UnsupportedOperationException();
        }
    }

}
