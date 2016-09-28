# Swara Machine Learning
A machine learning framework built on [Apache Commons Math](http://commons.apache.org/proper/commons-math/).

## Recipes
### Markov Chains
#### Discrete Markov Chains
The following is an example of a 3rd-order discrete markov chain learning to generate a gradient. The image below is the training example, and the image to the right is the output of a 3rd-order discrete markov chain. This example highlights the simplicity of the implementation; all that must be provided is an order, a comparator, and training examples.

```java
// Load the gradient image and create the output image.
final BufferedImage input = ImageIO.read(this.getClass().getResource("/gradient.jpg"));
final BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

// Construct a markov chain that compares colors by luminance.
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

// Generate a sequence of bits and write it it to an image.
final Iterator<Color> color = markov.generate();
IntStream.range(0, height).forEach(h -> {
    final int rgb = color.next().getRGB();
    IntStream.range(0, width).forEach(w -> result.setRGB(w, h, rgb));
});

// Write the image to file.
ImageIO.write(result, "jpg", new File("src/test/resources/gradient-result.jpg"));
```

<img width="49.744%" src="src/test/resources/gradient.jpg"/>
<img width="49.744%" style="float: right" src="src/test/resources/gradient-result.jpg"/>

### Neural Networks
All neural networks are trained via [backpropagation through time](https://en.wikipedia.org/wiki/Backpropagation_through_time).

```java
final NeuralNetwork<RealVector, RealVector> network = NeuralNetwork.of(new LstmLayer())
    .compose(new ForwardLayer(4, 3, new Tanh()))
    .compose(new LstmLayer(3, 2))
    .compose(NeuralNetwork.of(new LstmLayer()));
```


### Support Vector Machines (SVM)

### Kernels
- Linear Kernel
- Polynomial Kernel
- Sigmoid Kernel
- Gaussian RBF Kernel

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
