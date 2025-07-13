package org.akazukin.graph.algorithm.algorithms.barycentric;

import org.akazukin.graph.algorithm.builder.IGraphBuilder;
import org.akazukin.graph.algorithm.builder.IGraphWayedBuilder;
import org.akazukin.graph.vec.Vec2d;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Builder class for constructing instances of {@link BarycentricGraph}.
 * This class provides methods to define waypoints and create a barycentric interpolation graph based on them.
 * <p>
 * Implements {@link IGraphBuilder} with {@link BarycentricGraph} as the generic type.
 */
public final class BarycentricGraphBuilder implements IGraphWayedBuilder<BarycentricGraph>, Cloneable {
    List<Vec2d> wayPoints = new ArrayList<>();

    /**
     * Sets the collection of waypoints to construct the barycentric interpolation.
     * Each waypoint is represented as a {@link Vec2d} object, containing x and y coordinates.
     * The waypoints define the control points for the interpolation and must adhere to a strictly ascending x order.
     *
     * @param wayPoints an array of {@link Vec2d} objects representing the control points for the barycentric interpolation.
     */
    @Override
    public void setWayPoints(@NotNull final Vec2d[] wayPoints) {
        this.wayPoints = new ArrayList<>(Arrays.asList(wayPoints));
    }

    /**
     * Adds a waypoint to the list of waypoints used to construct the barycentric interpolation.
     * <p>
     * If the list of waypoints is {@code null}, it initializes a new {@link ArrayList}.
     * The provided waypoint defines a control point in the interpolation.
     *
     * @param waypoint the {@link Vec2d} object representing a control point with x and y coordinates.
     */
    @Override
    public void addWayPoint(final @NotNull Vec2d waypoint) {
        if (this.wayPoints == null) {
            this.wayPoints = new ArrayList<>();
        }
        this.wayPoints.add(waypoint);
    }

    @Override
    protected BarycentricGraphBuilder clone() {
        final BarycentricGraphBuilder cloned = new BarycentricGraphBuilder();
        cloned.wayPoints = new ArrayList<>(this.wayPoints);
        return cloned;
    }

    /**
     * Builds a {@link BarycentricGraph} instance using the provided waypoints.
     * The waypoints define the control points for the barycentric interpolation.
     *
     * @return a new instance of {@link BarycentricGraph} constructed with the current collection of waypoints.
     * Throws an exception if the number of waypoints is less than one or their x-coordinates are not strictly in ascending order.
     */
    @Override
    public BarycentricGraph build() {
        return new BarycentricGraph(this.wayPoints.toArray(Vec2d.EMPTY));
    }
}