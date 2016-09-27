package com.swara.core;

import java.nio.ByteBuffer;
import java.util.Set;
import javax.sound.sampled.AudioInputStream;

/**
 * A fingerprinter is responsible for extracting a set of descriptors that uniquely identify a piece
 * of musical input. These fingerprints can then be used to efficiently lookup and identify music.
 */
@FunctionalInterface
public interface Fingerprinter {

    Set<ByteBuffer> extract(AudioInputStream input);

}
