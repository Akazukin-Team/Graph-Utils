package org.akazukin.graph.algorithm.algorithms.expo;

import lombok.Setter;
import org.akazukin.graph.algorithm.builder.IGraphBuilder;
import org.akazukin.graph.vec.Vec2d;

/**
 * Builder class for constructing instances of {@link ExpoGraph}.
 * This class provides methods to define a start point and exponentiation amount and create an exponential graph based on them.
 * <p>
 * Implements {@link org.akazukin.graph.algorithm.builder.IGraphBuilder} with {@link ExpoGraph} as the generic type.
 */
@Setter
public final class ExpoGraphBuilder implements IGraphBuilder<ExpoGraph>, Cloneable {
    Vec2d startPoint;
    double amount;

    @Override
    protected ExpoGraphBuilder clone() {
        final ExpoGraphBuilder cloned = new ExpoGraphBuilder();
        cloned.startPoint = this.startPoint;
        cloned.amount = this.amount;
        return cloned;
    }

    /**
     * Builds a {@link ExpoGraph} instance using the provided waypoints.
     * The start point defines the control points for the exponentiation curve.
     *
     * @return a new instance of {@link ExpoGraph} constructed with the current collection of waypoints.
     * Throws an exception if the exponentiation amount is 0.
     */
    @Override
    public ExpoGraph build() {
        return new ExpoGraph(this.startPoint, this.amount);
    }
}
