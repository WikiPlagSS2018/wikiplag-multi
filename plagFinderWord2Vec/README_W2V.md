# Finding Plagiarisms with Word2Vec 

# What is Word2Vec

# Algorithm Idea

# Speeding up the process with TF-IDF

A algorithm for finding wikipedia plagiarisms.
[Hyperparameters used by the algorithm.](https://github.com/WikiplagWS17/wikiplag-multi/blob/feature/plagiarism-finder-documentation/plagiarismFinder/src/main/scala/de/htwberlin/f4/wikiplag/plagiarism/models/HyperParameters.scala) 

The algorthim works as follows:
* Prepare the user input.
  1. Split into sentences (by splitting on punctuation marks).
  2. Combine short ones. (Minimum length specified by the <code>minimumSentenceLength</code> hyperparmater)
  3. Remove stop words.
  4. Build n-grams.
  5. Compute hashes for each n-gram.
