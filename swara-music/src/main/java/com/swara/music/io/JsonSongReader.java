package com.swara.music.io;

import java.io.IOException;
import java.io.InputStream;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.swara.music.struct.Song;

import org.apache.commons.math3.fraction.Fraction;

/**
 * Reads a {@link Song} from a JSON string.
 */
public class JsonSongReader implements SongReader {

    private final ObjectMapper mapper;

    public JsonSongReader() {
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
     * Custom fraction deserializer to make is easier to read and modify fractional values like time
     * signatures, note durations, etc.
     */
    private class FractionDeserializer extends JsonDeserializer<Fraction> {

        @Override
        public Fraction deserialize(JsonParser jsonParser,
                                    DeserializationContext context) throws IOException {

            final String[] tokens = jsonParser.getValueAsString().split("\\s/\\s");
            return new Fraction(Integer.parseInt(tokens[0]), Integer.parseInt(tokens[1]));
        }

    }

}
