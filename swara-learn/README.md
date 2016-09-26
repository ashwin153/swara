# Swara Machine Learning
A machine learning framework built on [Apache Commons Math](http://commons.apache.org/proper/commons-math/).

## Recipes
### Neural Networks
All neural networks are trained via [backpropagation through time](https://en.wikipedia.org/wiki/Backpropagation_through_time).

```java
final NeuralNetwork network = new NeuralNetwork(lrate, 
    new ForwardLayer(6, 4, ForwardLayer.LOGISTIC),
    new ForwardLayer(4, 3, ForwardLayer.TANH),
    new LstmLayer(3, 4)
);
```

### Markov Chains
```java
final DiscreteMarkovChain<Integer> markov = new DiscreteMarkovChain<>(2);
markov.train(Arrays.asList(1, 4, 3, 1, 4, 3));
final Stream<Integer> output = markov.generate(Arrays.asList(1, 4));
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
