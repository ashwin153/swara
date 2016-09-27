package com.swara.music.features;

import com.swara.music.MusicFeature;
import com.swara.music.elements.Song;

/**
 * https://en.wikipedia.org/wiki/Dynamics_(music)#/media/File:Dynamic%27s_Note_Velocity.svg
 */
public class Dynamics implements MusicFeature {

    public static final int PPP = 16;
    public static final int PP  = 33;
    public static final int P   = 49;
    public static final int MP  = 64;
    public static final int MF  = 80;
    public static final int F   = 96;
    public static final int FF  = 112;
    public static final int FFF = 126;

    public static class Extractor implements MusicFeature.Extractor<Dynamics> {
        @Override
        public Dynamics extract(Song song) {
            throw new UnsupportedOperationException();
        }
    }

}
