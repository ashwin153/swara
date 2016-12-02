# Swara Machine Learning
A machine learning framework built from scratch with help from [Breeze](http://commons.apache.org/proper/commons-math/).

## Recipes
### Automatic Differentiation
The following is an example of the automatic differentation tools built into the library.

```scala
val x = Var(5.0)
val y = Var(2.5)
Cos(x) * Sin(x) / Tan(y) d(x)d(y)
```

### Markov Chains
#### Discrete Markov Chains
The following is an example of a ```DiscreteMarkovChain``` learning to generate a gradient. The image below is the training example, and the image to the right is the output of a 3rd-order discrete markov chain. The markov chain implementation is thread-safe, it may be trained while it is concurrently being used for prediction!

```scala
// Load the color gradient from the input image.
val img = ImageIO.read(new File("swara-learn/src/test/resources/gradient.jpg"))
val inW = img.getWidth, inH = img.getHeight
val col = (0 until inW * inH).map(pix => new Color(img.getRGB(pix / inW, pix % inH)))

// Create and train a markov chain.
val markov = DiscreteMarkovChain[Color](3)
markov.train(col)

// Generate a new gradient using the markov chain.
val outW = 200, outH = 200
val out = new BufferedImage(outW, outH, BufferedImage.TYPE_INT_RGB)
val gen = markov.generate().take(outW * outH).toList
(0 until outW).foreach(w => (0 until outH).foreach(h => out.setRGB(h, w, gen(w).getRGB)))
ImageIO.write(out, "jpg", new File("swara-learn/src/test/resources/gradient-result.jpg"))
```

<img width="49.744%" src="src/test/resources/gradient.jpg"/>
<img width="49.744%" style="float: right" src="src/test/resources/gradient-result.jpg"/>

#### Hidden Markov Models
The following is an example of a ```HiddenMarkovModel``` learning to predict the parts-of-speech in a sentence. The HMM implementation is thread-safe, it may be trained while it is concurrently being used for prediction!

### Neural Networks


## Attribution
Here's a little page-rank karma for all the sources I used:
- [Markov Chains](https://www.jair.org/media/1491/live-1491-2335-jair.pdf)
- [Recurrent Neural Networks](http://www.wildml.com/2015/09/recurrent-neural-networks-tutorial-part-1-introduction-to-rnns/)
- [LSTM Neural Networks #1](http://arunmallya.github.io/writeups/nn/lstm/index.html#/)
- [LSTM Neural Networks #2](http://colah.github.io/posts/2015-08-Understanding-LSTMs/)
- [Convolutional Neural Networks](http://ufldl.stanford.edu/tutorial/supervised/ConvolutionalNeuralNetwork/)
- [Fast Learning Algorithms](https://page.mi.fu-berlin.de/rojas/neural/chapter/K8.pdf)
- [Support Vector Machines](https://www.csie.ntu.edu.tw/~cjlin/papers/guide/guide.pdf)
- [Stanford CS229](http://cs229.stanford.edu)
