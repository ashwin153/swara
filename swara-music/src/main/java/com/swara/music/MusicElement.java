package com.swara.music;

/**
 * A musical element. A musical element is any attribute of a {@link com.swara.music.elements.Song}
 * that is necessary to encode it as sheet music. For example, a song is <emph>composed</emph> of
 * notes and the notes <emph>form</emph> a chord progression; notes are a musical element and the
 * resulting chord progression is a musical feature. Music elements are the core components of
 * every song.
 */
public interface MusicElement {

    /**
     * Musical elements are constructed using a Fluent-style Builder Pattern, in concordance with
     * Item 2 in Effective Java. Builders make musical code more understandable to a less technical
     * audience, while also simplifying the constructino of complex objects.
     */
    interface Builder<E extends MusicElement> {
        E build();
    }

}
