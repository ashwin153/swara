package com.swara.core.fingerprinters;

import java.nio.ByteBuffer;
import java.util.Set;
import javax.sound.sampled.AudioInputStream;

import com.swara.core.Fingerprinter;

/**
 * Based off the @see <a href="http://www.ee.columbia.edu/~dpwe/papers/Wang03-shazam.pdf">Shazam</a>
 * whitepaper. Calculates a set of 32-bit fingerprints that consist of spectrogram peaks at various
 * time offsets in a particular {@link com.swara.music.elements.Song}
 */
public class ShazamFingerprinter implements Fingerprinter {

    @Override
    public Set<ByteBuffer> extract(AudioInputStream input) {
        return null;
    }

}
