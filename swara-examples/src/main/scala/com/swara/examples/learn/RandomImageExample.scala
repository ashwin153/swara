package com.swara.examples.learn

import com.swara.learn.markov.DiscreteMarkovChain
import java.awt.Color
import java.awt.image.BufferedImage

/**
 * A random image writer generates an image via discrete markov chain. Random image writers learn
 * the probability distribution of each color given the previous k colors and then use this
 * distribution to generate a sequence of colors beginning from a randomly selected seed.
 */
object RandomImageExample {

  def apply(img: BufferedImage): BufferedImage = {
    val (w, h) = (img.getWidth, img.getHeight)
    val markov = DiscreteMarkovChain.empty[Color](2)
    markov.train((0 until w * h).map(p => new Color(img.getRGB(p / w, p % h))))

    val result = markov.generate().take(w * h).toIndexedSeq
    val out = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB)
    (0 until h).foreach(y => (0 until w).foreach(x => out.setRGB(x, y, result(y).getRGB)))
    out
  }

}
