package org.akazukin.graph.algorithm.algorithms.hermite;

import org.akazukin.graph.algorithm.builder.IGraphBuilder;
import org.akazukin.graph.vec.Vec2d;
import org.akazukin.graph.vec.Vec2d2;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Builder class for constructing instances of {@link CubicHermiteGraph}.
 * This class provides methods to define waypoints with optional tangent vectors and create a cubic Hermite graph based on them.
 * <p>
 * Implements {@link IGraphBuilder} with {@link CubicHermiteGraph} as the generic type.
 */
public final class CubicHermiteGraphBuilder implements IGraphBuilder<CubicHermiteGraph>, Cloneable {
    List<Vec2d2> wayPoints = new ArrayList<>();

    /**
     * Sets the collection of waypoints to construct the cubic Hermite curve.
     * Each waypoint is represented as a {@link Vec2d2} object, containing position and optional tangent vector.
     * The waypoints define the control points for the Hermite interpolation and must adhere to a strictly ascending x order.
     *
     * @param wayPoints an array of {@link Vec2d2} objects representing the control points for the cubic Hermite curve.
     */
    public void setWayPoints(@NotNull final Vec2d2[] wayPoints) {
        this.wayPoints = new ArrayList<>(Arrays.asList(wayPoints));
    }

    /**
     * Adds a waypoint with position only to the list of waypoints used to construct the cubic Hermite curve.
     * The tangent vector will be automatically calculated using finite differences.
     * <p>
     * If the list of waypoints is {@code null}, it initializes a new {@link ArrayList}.
     *
     * @param position the {@link Vec2d} object representing the position of the control point.
     */
    public void addWayPoint(final Vec2d position) {
        this.addWayPoint(new Vec2d2(position, null));
    }

    /**
     * Adds a waypoint to the list of waypoints used to construct the cubic Hermite curve.
     * <p>
     * If the list of waypoints is {@code null}, it initializes a new {@link ArrayList}.
     * The provided waypoint defines a control point in the Hermite curve.
     *
     * @param waypoint the {@link Vec2d2} object representing a control point with position and optional tangent vector.
     */
    public void addWayPoint(final Vec2d2 waypoint) {
        if (this.wayPoints == null) {
            this.wayPoints = new ArrayList<>();
        }
        this.wayPoints.add(waypoint);
    }

    /**
     * Adds a waypoint with both position and tangent vector to the list of waypoints used to construct the cubic Hermite curve.
     * <p>
     * If the list of waypoints is {@code null}, it initializes a new {@link ArrayList}.
     *
     * @param position the {@link Vec2d} object representing the position of the control point.
     * @param tangent  the {@link Vec2d} object representing the tangent vector at the control point.
     */
    public void addWayPoint(final Vec2d position, final Vec2d tangent) {
        this.addWayPoint(new Vec2d2(position, tangent));
    }

    @Override
    protected CubicHermiteGraphBuilder clone() {
        final CubicHermiteGraphBuilder cloned = new CubicHermiteGraphBuilder();
        cloned.wayPoints = new ArrayList<>(this.wayPoints);
        return cloned;
    }

    /**
     * Builds a {@link CubicHermiteGraph} instance using the provided waypoints.
     * The waypoints define the control points for the cubic Hermite curve.
     *
     * @return a new instance of {@link CubicHermiteGraph} constructed with the current collection of waypoints.
     * Throws an exception if the number of waypoints is less than one or their x-coordinates are not strictly in ascending order.
     */
    @Override
    public CubicHermiteGraph build() {
        return new CubicHermiteGraph(this.wayPoints.toArray(Vec2d2.EMPTY));
    }
}