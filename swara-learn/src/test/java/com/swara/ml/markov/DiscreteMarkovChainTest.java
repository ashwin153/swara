package com.swara.ml.markov;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Iterator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javax.imageio.ImageIO;

import org.junit.Test;

public class DiscreteMarkovChainTest {

    @Test
    public void testText() throws URISyntaxException, IOException {
        final Path path = Paths.get(this.getClass().getResource("/shakespeare.txt").toURI());
        final DiscreteMarkovChain<String> markov = new DiscreteMarkovChain<>(5, String::compareTo);

        // Train the markov chain on all the words in the file.
        markov.train(Files.readAllLines(path).stream()
            .flatMap(i -> Arrays.stream(i.toLowerCase().trim().split("\\W+")))
            .collect(Collectors.toList())
        );

        // Generate a sequence of 100 words and print it out.
        Stream.generate(markov.generate()::next)
            .limit(100)
            .map(i -> i + " ")
            .forEach(System.out::print);
    }

    @Test
    public void testGradient() throws URISyntaxException, IOException {
        // Load the gradient image and construct a markov chain that compares colors by luminance.
        final BufferedImage input = ImageIO.read(this.getClass().getResource("/gradient.jpg"));
        final DiscreteMarkovChain<Color> markov = new DiscreteMarkovChain<>(3, (a, b) -> {
            final int dr = a.getRed() - b.getRed();
            final int dg = a.getGreen() - b.getGreen();
            final int db = a.getBlue() - b.getBlue();
            return (int) (0.2126 * dr + 0.7152 * dg + 0.0722 * db);
        });

        // Train the markov chains on the RGB contents of the image.
        markov.train(IntStream.range(0, input.getWidth() * input.getHeight()).boxed()
            .map(i -> new Color(input.getRGB(i / input.getHeight(), i % input.getHeight())))
            .collect(Collectors.toList()));

        // Generate a sequence of bits and write it is a 200x200 image.
        final BufferedImage image = new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);
        final Iterator<Color> color = markov.generate();
        IntStream.range(0, 200).forEach(h -> {
            final int rgb = color.next().getRGB();
            IntStream.range(0, 200).forEach(w -> image.setRGB(w, h, rgb));
        });

        // Write the image to file.
        ImageIO.write(image, "jpg", new File("src/test/resources/gradient-result.jpg"));
    }



}
