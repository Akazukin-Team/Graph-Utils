package org.akazukin.graph.algorithm.algorithms.nearby;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.akazukin.graph.algorithm.algorithms.Constants;
import org.akazukin.graph.algorithm.graph.IGraphWayed;
import org.akazukin.graph.vec.Vec2d;

/**
 * Represents a nearby interpolation graph that finds the closest waypoint based on x-coordinate.
 * This class performs nearest-neighbor interpolation between defined points, selecting the waypoint
 * with the smallest distance for any given x-coordinate.
 * <p>
 * The nearby graph is defined by a set of waypoints, and it ensures that:
 * - The waypoints have strictly ascending x-coordinates.
 * - For any given x-coordinate, the y-value is determined by the nearest waypoint.
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class NearbyGraph implements IGraphWayed {
    @Getter
    Vec2d[] wayPoints;

    /**
     * Constructs a nearby interpolation graph using the provided waypoints.
     * The generated graph selects the nearest waypoint for any given x-coordinate.
     * <p>
     * The waypoints must have strictly ascending x-coordinates.
     * The constructor throws an {@link IllegalStateException} if the conditions are not met.
     *
     * @param wayPoints an array of {@link Vec2d} objects representing the waypoints for
     *                  the nearby interpolation. Each Vec2d contains x and y coordinates for a point.
     */
    public NearbyGraph(final Vec2d[] wayPoints) {
        this.wayPoints = wayPoints.clone();
        if (this.wayPoints.length < 1) {
            throw new IllegalStateException(Constants.EX_NO_WAYS);
        }
        for (int i = 0; i + 1 < wayPoints.length; i++) {
            if (wayPoints[i].getX() >= wayPoints[i + 1].getX()) {
                throw new IllegalStateException(Constants.EX_WAYS_ORDER);
            }
        }
    }

    /**
     * Computes the y-coordinate for a given x-coordinate using the nearby interpolation.
     * If the nearby graph comprises a single waypoint, this method directly returns the y-coordinate of that waypoint.
     * Otherwise, it determines the correct interval index for the given x-coordinate and evaluates the nearest waypoint within that interval.
     *
     * @param x the x-coordinate for which the corresponding y-coordinate is to be calculated.
     *          Represents the input value for which the nearest waypoint's output is computed.
     * @return the y-coordinate corresponding to the given x-coordinate using nearby interpolation.
     * The value is calculated based on the nearest waypoint selection.
     */
    @Override
    public double getY(final double x) {
        if (Double.isNaN(x) || Double.isInfinite(x)) {
            throw new IllegalArgumentException(Constants.EX_INVALID_NUM + " | " + x);
        }

        if (this.wayPoints.length == 1) {
            return this.wayPoints[0].getY();
        }
        final int intervalIndex = this.getReferenceIndex(x);
        return this.evaluateGraph(intervalIndex, x);
    }

    /**
     * Determines the index of the interval within the array of waypoints (represented as {@link org.akazukin.graph.vec.Vec2d})
     * that contains the given x-coordinate.
     * This method performs a binary search to find the correct segment index efficiently.
     *
     * @param x the x-coordinate for which the corresponding interval index is to be found.
     *          Represents the input value whose location in the x-range of the waypoints is determined.
     * @return the index of the way interval such that {@code ways[index].getX() <= x < ways[index + 1].getX()}.
     * If {@code x} is less than the smallest x-coordinate, returns 0.
     * If {@code x} exceeds the largest x-coordinate, returns {@code ways.length - 2}.
     */
    private int getReferenceIndex(final double x) {
        final int wayCount = this.wayPoints.length;
        int left = 0;
        int right = wayCount - 2;

        if (x <= this.wayPoints[0].getX()) {
            return left;
        }
        if (x >= this.wayPoints[wayCount - 1].getX()) {
            return right;
        }

        while (left < right) {
            final int mid = (left + right + 1) / 2;
            if (this.wayPoints[mid].getX() <= x) {
                left = mid;
            } else {
                right = mid - 1;
            }
        }

        return left;
    }

    /**
     * Evaluates the nearby interpolation at a specific x-coordinate within the given interval.
     * The method calculates the y-coordinate by selecting the nearest waypoint using distance comparison.
     *
     * @param intervalIndex the index of the interval within the waypoint array where the given x-coordinate resides.
     *                      This value is determined using {@link #getReferenceIndex(double)}.
     * @param curX          the x-coordinate for which the y-coordinate of the nearest waypoint is to be calculated.
     *                      Represents the input point to evaluate the nearby interpolation function.
     * @return the y-coordinate corresponding to the given x-coordinate using nearby interpolation.
     */
    private double evaluateGraph(final int intervalIndex, final double curX) {
        final double deltaX = curX - this.wayPoints[intervalIndex].getX();
        if (this.wayPoints.length > intervalIndex + 1) {
            if (this.wayPoints[intervalIndex + 1].getX() - curX < deltaX) {
                return this.wayPoints[intervalIndex + 1].getY();
            }
        }
        return this.wayPoints[intervalIndex].getY();
    }
}
