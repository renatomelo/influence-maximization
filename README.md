# Influence Maximization

This project contains the implementation of the algorithms proposed in my paper entitled:

- [A preselection algorithm for the influence maximization problem in power law graphs](https://dl.acm.org/doi/abs/10.1145/3167132.3167322)

**Problem Definition**: *The influence maximization problem in social networks seeks out a set of nodes that allows spreading information to the greatest number of members.*

In this work, we propose a methodology to speedup the Kempe's algorithm with focus on power law graphs. The improvement consists of choosing the most promising nodes in advance. To this end, we explore some properties of power law graphs and the relationship between social influence and degree distribution. We have verified by experimental analysis that this preselection reduces the run time while preserving the quality of the solution.
 
 The source code of the proposed algorithm is here:  `src/algoritmos/PrevalentSeed.java`

## Dependences:
- JGraphT Library (a Java library of graph theory data structures and algorithms)
- JUNG (Java Universal Network/Graph Framework)

Random Graph Generative Models:
- [Generalized Random Graphs (GRG)](https://www.cambridge.org/core/books/random-graphs-and-complex-networks/generalized-random-graphs/21A9FB3C0727E788E507B49C7C3BFED7)
- [Configuration Model](https://www.cambridge.org/core/books/random-graphs-and-complex-networks/configuration-model/6D3DAC44FF73AAB1CED2FA5F3864B51C)

