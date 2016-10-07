package com.swara.music.readers;

import java.io.IOException;
import java.io.InputStream;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.swara.music.MusicReader;
import com.swara.music.elements.Song;

import org.apache.commons.math3.fraction.Fraction;

/**
 * Reads a {@link Song} from a Json string. Json conversion is particularly important to facilitate
 * interoperability; without a convenient format for transferring musical information between
 * programming languages, the complex logic in {@link com.swara.music.writers.MidiWriter} and in
 * {@link MidiReader} would have to be constantly rewritten in every supported language.
 */
public final class JsonReader implements MusicReader {

    private final ObjectMapper mapper;

    public JsonReader() {
        // Register custom fraction deserializer.
        final SimpleModule module = new SimpleModule();
        module.addDeserializer(Fraction.class, new FractionDeserializer());
        this.mapper = new ObjectMapper();
        this.mapper.registerModule(module);
    }

    @Override
    public Song read(InputStream in) throws Exception {
        return this.mapper.readValue(in, Song.class);
    }

    /**
     * Custom fraction deserializer to make is easier to read and modify fractional values like note
     * durations, etc. Fractions are parsed according to the following regex: \\d+\\s*\/\\s*\\d+.
     */
    private final class FractionDeserializer extends JsonDeserializer<Fraction> {

        @Override
        public Fraction deserialize(JsonParser parser,
                                    DeserializationContext context) throws IOException {

            final String[] tokens = parser.getValueAsString().split("\\s*/\\s*");
            return new Fraction(Integer.parseInt(tokens[0]), Integer.parseInt(tokens[1]));
        }

    }

}
