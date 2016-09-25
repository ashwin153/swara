package com.swara.music.writers;

import java.io.IOException;
import java.io.OutputStream;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.swara.music.MusicWriter;
import com.swara.music.elements.Song;

import org.apache.commons.math3.fraction.Fraction;

/**
 * Writes a {@link Song} to a JSON string.
 */
public class JsonWriter implements MusicWriter {

    private final ObjectMapper mapper;

    public JsonWriter() {
        // Register custom fraction serializer.
        final SimpleModule module = new SimpleModule();
        module.addSerializer(Fraction.class, new FractionSerializer());
        this.mapper = new ObjectMapper();
        this.mapper.enable(SerializationFeature.INDENT_OUTPUT);
        this.mapper.registerModule(module);
    }

    @Override
    public void write(OutputStream out, Song song) throws Exception {
        out.write(this.mapper.writeValueAsBytes(song));
    }

    /**
     * Custom fraction serializer to make is easier to read and modify fractional values like time
     * signatures, note durations, etc.
     */
    private final class FractionSerializer extends JsonSerializer<Fraction> {

        @Override
        public void serialize(Fraction fraction,
                              JsonGenerator generator,
                              SerializerProvider provider) throws IOException {

            generator.writeString(fraction.toString());
        }

    }

}
