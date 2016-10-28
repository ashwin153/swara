package com.swara.learn.markov

import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import org.scalatest.FunSuite
import scala.io.Source

class DiscreteMarkovChainTest extends FunSuite {

  test("Text writer") {
    val markov = DiscreteMarkovChain[String](3)
    val shakespeare = Source.fromFile("swara-learn/src/test/resources/shakespeare.txt").getLines()
      .flatMap(line => line.toLowerCase.trim.split("\\W+"))
      .toList

    markov.train(shakespeare)
    markov.generate().take(100).foreach(word => print(word + " "))
  }

  test("Gradient writer") {
    // Construct a markov chain that compares colors by luminance.
    val luminance: Color => Double = c => c.getRed * 0.2126 + c.getBlue * 0.0722 + c.getGreen * 0.7152
    val markov = DiscreteMarkovChain[Color](3)

    // Load the color gradient from the test image.
    val input = ImageIO.read(new File("swara-learn/src/test/resources/gradient.jpg"))
    val gradient = (0 until input.getWidth * input.getHeight)
      .map(pixel => new Color(input.getRGB(pixel / input.getWidth, pixel % input.getHeight)))
      .toList

    // Generate a new gradient and write to file.
    markov.train(gradient)
    val result = markov.generate().take(200).toList
    val output = new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB)
    (0 until 200).foreach(w => (0 until 200).foreach(h => output.setRGB(h, w, result(w).getRGB)))
    ImageIO.write(output, "jpg", new File("swara-learn/src/test/resources/gradient-result.jpg"))
  }

}