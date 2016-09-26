# Swara Core
Swara core libraries.

## Compose

## Search
Conventional music identification techniques like [Shazam](http://www.ee.columbia.edu/~dpwe/papers/Wang03-shazam.pdf), [Dejavu](http://willdrevo.com/fingerprinting-and-audio-recognition-with-python/), and [Computer Vision for Music Identification](http://www.cs.cmu.edu/~yke/musicretrieval/cvpr2005-mr.pdf) all extract audio fingerprints from spectrograms (FFT). While these approaches have been highly successful in practice, they consider only the numerical properties of waveforms and not the underlying musical properties.

- Use a Hidden Markov Model to convert audio to music (like speech-to-text)
- Extract a descriptor for the musical content
- [Search by nearest neighbor in Hamming space](http://www.cl.cam.ac.uk/~jgd1000/FastFuzzySearch.pdf)


## Attribution
Here's a little page-rank karma for all the sources I used:
- [RNN Composition](http://www.hexahedria.com/2015/08/03/composing-music-with-recurrent-neural-networks/)
- [Google Magenta](https://magenta.tensorflow.org/2016/06/10/recurrent-neural-network-generation-tutorial/)
