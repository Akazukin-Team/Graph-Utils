package org.akazukin.graph.algorithm.builder;

import org.akazukin.graph.algorithm.graph.IGraph;
import org.akazukin.graph.algorithm.graph.IGraphWayed;
import org.akazukin.graph.vec.Vec2d;
import org.jetbrains.annotations.NotNull;

public interface IGraphWayedBuilder<G extends IGraph & IGraphWayed> extends IGraphBuilder<G> {
    void setWayPoints(@NotNull Vec2d[] wayPoints);

    void addWayPoint(@NotNull Vec2d wayPoint);
}
