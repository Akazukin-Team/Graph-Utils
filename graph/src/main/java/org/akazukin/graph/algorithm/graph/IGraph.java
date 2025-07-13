package org.akazukin.graph.algorithm.graph;


/**
 * Represents a mathematical graph that defines a relationship between 'x' and 'y' coordinates
 * and provides the ability to calculate the 'y' value for a given 'x' value.
 * <p>
 * This interface is typically used to define the contract for graph types that compute a
 * smooth or non-linear mapping, such as splines or other curve interpolation mechanisms.
 */
public interface IGraph {
    /**
     * Calculates the y-coordinate of the graph for the given x-coordinate based on the graph's mathematical relationship.
     *
     * @param x the x-coordinate for which the y-coordinate is to be calculated.
     * @return the calculated y-coordinate corresponding to the given x-coordinate.
     * The result is determined by the graph's implementation, such as interpolation or mapping.
     */
    double getY(double x);
}
