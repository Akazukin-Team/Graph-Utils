package org.akazukin.graph.algorithm.builder;

import org.akazukin.graph.algorithm.graph.IGraph;
import org.akazukin.graph.algorithm.graph.IGraphControlled;
import org.akazukin.graph.vec.Vec2d;

public interface IGraphControlledBuilder<G extends IGraph & IGraphControlled> extends IGraphBuilder<G> {
    void setControlPoints(Vec2d[] controlPoints);

    void addControlPoint(Vec2d controlPoint);
}
