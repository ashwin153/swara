Each MIDI file is placed into two groups: one based on key signature, and the other based on time signature. Separate Markov Chains are created for each category. States in the key category represent chords (CEG) and states in the time category represent measures (eighth+quarter+quarter+eighth+quarter). To generate a measure of music, the algorithm first selects a rhymthm using the time Markov Chain and then selects as many chords as needed from the key Markov Chain. This ensures that the generated music is both melodically and rhythmically correct.

Separate Markov Chains for
  - Chords
  - Phrases
  - Dynamics