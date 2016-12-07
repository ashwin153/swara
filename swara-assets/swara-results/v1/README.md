# Version 1
- **Date**: October 7th, 2016
- **Git SHA**: 835e30c9cbea0276d0979259ed9b6980687ad03f
- **Model**: Three discrete markov chains to separately model rhythm, dynamics, and harmony. The rythmic markov chain takes into account the previous 5 notes, while the dynamics and harmony markov chains only consider the previous 2 notes. To reduce the state space (and make Markov methods more tractable), a few simplifications were made: accidentals were removed (fewer kinds of chords), volume was placed into one of 8 buckets (correspond to P, MP, M, MF, F, etc.).
- **Training**: Twelve Mozart pieces.
