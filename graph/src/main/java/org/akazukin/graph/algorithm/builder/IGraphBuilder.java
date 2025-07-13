package org.akazukin.graph.algorithm.builder;

import org.akazukin.graph.algorithm.graph.IGraph;

/**
 * A generic builder interface for constructing instances of {@link org.akazukin.graph.algorithm.graph.IGraph}-compliant graph implementations.
 *
 * @param <G> the type of graph that this builder produces, extending {@link org.akazukin.graph.algorithm.graph.IGraph}.
 */
public interface IGraphBuilder<G extends IGraph> {
    /**
     * Constructs and returns an instance of a graph that complies with {@link IGraph}.
     *
     * @return an instance of the graph of type {@code G} created by this builder.
     */
    G build();
}
