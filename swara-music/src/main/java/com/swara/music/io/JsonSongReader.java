package com.swara.music.io;

import java.io.IOException;
import java.io.InputStream;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.swara.music.model.Song;

import org.apache.commons.math3.fraction.Fraction;

/**
 *
 */
public class JsonSongReader implements SongReader {

    private final ObjectMapper mapper;

    public JsonSongReader() {
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
     *
     */
    private class FractionDeserializer extends JsonDeserializer<Fraction> {

        @Override
        public Fraction deserialize(JsonParser jsonParser,
                                    DeserializationContext context) throws IOException {

            final String[] tokens = jsonParser.getValueAsString().split("/");
            return new Fraction(Integer.parseInt(tokens[0].trim()), Integer.parseInt(tokens[1].trim()));
        }

    }

}
