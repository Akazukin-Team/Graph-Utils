package org.akazukin.graph.algorithm.algorithms.nearbyaverage;

import org.akazukin.graph.algorithm.builder.IGraphBuilder;
import org.akazukin.graph.algorithm.builder.IGraphWayedBuilder;
import org.akazukin.graph.vec.Vec2d;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Builder class for constructing instances of {@link NearbyAverageGraph}.
 * This class provides methods to define waypoints and create a cubic spline graph based on them.
 * <p>
 * Implements {@link IGraphBuilder} with {@link NearbyAverageGraph} as the generic type.
 */
public final class NearbyAverageGraphBuilder implements IGraphWayedBuilder<NearbyAverageGraph>, Cloneable {
    List<Vec2d> wayPoints = new ArrayList<>();

    /**
     * Sets the collection of waypoints to construct the cubic spline curve.
     * Each waypoint is represented as a {@link Vec2d} object, containing x and y coordinates.
     * The waypoints define the control points for the spline and must adhere to a strictly ascending x order.
     *
     * @param waypoints an array of {@link Vec2d} objects representing the control points for the cubic spline curve.
     */
    @Override
    public void setWayPoints(final Vec2d[] waypoints) {
        this.wayPoints = new ArrayList<>(this.wayPoints);
    }

    /**
     * Adds a waypoint to the list of waypoints used to construct the cubic spline curve.
     * <p>
     * If the list of waypoints is {@code null}, it initializes a new {@link ArrayList}.
     * The provided waypoint defines a control point in the spline curve.
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
    protected NearbyAverageGraphBuilder clone() {
        final NearbyAverageGraphBuilder cloned = new NearbyAverageGraphBuilder();
        cloned.wayPoints = new ArrayList<>(this.wayPoints);
        return cloned;
    }

    /**
     * Builds a {@link NearbyAverageGraph} instance using the provided waypoints.
     * The waypoints define the control points for the cubic spline curve.
     *
     * @return a new instance of {@link NearbyAverageGraph} constructed with the current collection of waypoints.
     * Throws an exception if the number of waypoints is less than one or their x-coordinates are not strictly in ascending order.
     */
    @Override
    public NearbyAverageGraph build() {
        return new NearbyAverageGraph(this.wayPoints.toArray(Vec2d.EMPTY));
    }
}
