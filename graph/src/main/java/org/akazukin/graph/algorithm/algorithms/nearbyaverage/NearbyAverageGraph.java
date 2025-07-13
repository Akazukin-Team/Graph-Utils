package org.akazukin.graph.algorithm.algorithms.nearbyaverage;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.akazukin.graph.algorithm.algorithms.Constants;
import org.akazukin.graph.algorithm.graph.IGraphWayed;
import org.akazukin.graph.vec.Vec2d;

/**
 * Represents a nearby average interpolation graph that finds the average of nearby waypoints.
 * This class performs averaging interpolation between defined points, calculating the average
 * of surrounding waypoints for any given x-coordinate.
 * <p>
 * The nearby average graph is defined by a set of waypoints, and it ensures that:
 * - The waypoints have strictly ascending x-coordinates.
 * - For any given x-coordinate, the y-value is determined by the average of nearby waypoints.
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class NearbyAverageGraph implements IGraphWayed {
    @Getter
    Vec2d[] wayPoints;

    /**
     * Constructs a nearby average interpolation graph using the provided waypoints.
     * The generated graph calculates the average of nearby waypoints for any given x-coordinate.
     * <p>
     * The waypoints must have strictly ascending x-coordinates.
     * The constructor throws an {@link IllegalStateException} if the conditions are not met.
     *
     * @param wayPoints an array of {@link Vec2d} objects representing the waypoints for
     *                  the nearby average interpolation. Each Vec2d contains x and y coordinates for a point.
     */
    public NearbyAverageGraph(final Vec2d[] wayPoints) {
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
     * Computes the y-coordinate for a given x-coordinate using the nearby average interpolation.
     * If the nearby average graph comprises a single waypoint, this method directly returns the y-coordinate of that waypoint.
     * Otherwise, it determines the correct interval index for the given x-coordinate and evaluates the average of nearby waypoints within that interval.
     *
     * @param x the x-coordinate for which the corresponding y-coordinate is to be calculated.
     *          Represents the input value for which the nearby average interpolation's output is computed.
     * @return the y-coordinate corresponding to the given x-coordinate using nearby average interpolation.
     * The value is calculated based on the average of nearby waypoints.
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
     * Determines the index of the interval within the array of waypoints (represented as {@link Vec2d})
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
     * Evaluates the nearby average interpolation at a specific x-coordinate within the given interval.
     * The method calculates the y-coordinate by averaging nearby waypoints based on the position and availability of surrounding points.
     *
     * @param referenceIndex the index of the interval within the waypoint array where the given x-coordinate resides.
     *                       This value is determined using {@link #getReferenceIndex(double)}.
     * @param curX           the x-coordinate for which the y-coordinate of the average of nearby waypoints is to be calculated.
     *                       Represents the input point to evaluate the nearby average interpolation function.
     * @return the y-coordinate corresponding to the given x-coordinate using nearby average interpolation.
     */
    private double evaluateGraph(final int referenceIndex, final double curX) {
        if (this.wayPoints.length == 1) {
            return this.wayPoints[0].getY();
        }
        if (curX <= this.wayPoints[0].getX() || curX >= this.wayPoints[this.wayPoints.length - 1].getX()) {
            return this.wayPoints[referenceIndex].getY();
        }
        if (this.wayPoints[referenceIndex].getX() == curX) {
            if (referenceIndex > 0 && this.wayPoints.length > referenceIndex + 1) {
                return (this.wayPoints[referenceIndex + 1].getY() + this.wayPoints[referenceIndex].getY() + this.wayPoints[referenceIndex - 1].getY()) / 3;
            } else if (referenceIndex > 0) {
                return (this.wayPoints[referenceIndex - 1].getY() + this.wayPoints[referenceIndex].getY()) / 2;
            } else {
                return (this.wayPoints[referenceIndex + 1].getY() + this.wayPoints[referenceIndex].getY()) / 2;
            }
        } else if (this.wayPoints.length > referenceIndex + 1) {
            return (this.wayPoints[referenceIndex + 1].getY() + this.wayPoints[referenceIndex].getY()) / 2;
        }
        return this.wayPoints[referenceIndex].getY();
    }
}
