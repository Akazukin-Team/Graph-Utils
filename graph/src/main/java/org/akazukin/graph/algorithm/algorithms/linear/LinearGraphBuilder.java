package org.akazukin.graph.algorithm.algorithms.linear;

import org.akazukin.graph.algorithm.builder.IGraphBuilder;
import org.akazukin.graph.algorithm.builder.IGraphWayedBuilder;
import org.akazukin.graph.vec.Vec2d;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Builder class for constructing instances of {@link LinearGraph}.
 * This class provides methods to define waypoints and create a linear graph based on them.
 * <p>
 * Implements {@link IGraphBuilder} with {@link LinearGraph} as the generic type.
 */
public final class LinearGraphBuilder implements IGraphWayedBuilder<LinearGraph>, Cloneable {
    List<Vec2d> wayPoints = new ArrayList<>();

    /**
     * Sets the collection of waypoints to construct the linear curve.
     * Each waypoint is represented as a {@link Vec2d} object, containing x and y coordinates.
     * The waypoints define the control points for the curve and must adhere to a strictly ascending x order.
     *
     * @param waypoints an array of {@link Vec2d} objects representing the control points for the linear curve.
     */
    @Override
    public void setWayPoints(@NotNull final Vec2d[] waypoints) {
        this.wayPoints = new ArrayList<>(Arrays.asList(waypoints));
    }

    /**
     * Adds a waypoint to the list of waypoints used to construct the linear curve.
     * <p>
     * If the list of waypoints is {@code null}, it initializes a new {@link ArrayList}.
     * The provided waypoint defines a control point in the curve.
     *
     * @param waypoint the {@link Vec2d} object representing a control point with x and y coordinates.
     */
    @Override
    public void addWayPoint(@NotNull final Vec2d waypoint) {
        if (this.wayPoints == null) {
            this.wayPoints = new ArrayList<>();
        }
        this.wayPoints.add(waypoint);
    }

    @Override
    protected LinearGraphBuilder clone() {
        final LinearGraphBuilder cloned = new LinearGraphBuilder();
        cloned.wayPoints = new ArrayList<>(this.wayPoints);
        return cloned;
    }

    /**
     * Builds a {@link LinearGraph} instance using the provided waypoints.
     * The waypoints define the control points for the linear curve.
     *
     * @return a new instance of {@link LinearGraph} constructed with the current collection of waypoints.
     * @throws IllegalStateException if the number of waypoints is less than one or their x-coordinates are not strictly in ascending order.
     */
    @Override
    public LinearGraph build() {
        return new LinearGraph(this.wayPoints.toArray(Vec2d.EMPTY));
    }
}
