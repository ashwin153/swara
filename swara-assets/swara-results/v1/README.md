Version 1
- Trained on 12 Mozart pieces
final DiscreteMarkovChain<Fraction> rhythm = new DiscreteMarkovChain<>(5, Fraction::compareTo);
final DiscreteMarkovChain<Integer> dynamics = new DiscreteMarkovChain<>(2, Integer::compareTo);
final DiscreteMarkovChain<Iterable<Integer>> harmony = new DiscreteMarkovChain<>(2, Ordering.natural().lexicographical());

