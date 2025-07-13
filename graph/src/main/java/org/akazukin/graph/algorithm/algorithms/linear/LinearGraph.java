package org.akazukin.graph.algorithm.algorithms.linear;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.akazukin.graph.algorithm.algorithms.Constants;
import org.akazukin.graph.algorithm.graph.IGraphWayed;
import org.akazukin.graph.vec.Vec2d;

/**
 * This package contains implementations of mathematical graph structures designed for linear
 * and multisegmented linear interpolation.
 * These graphs compute relationships between x and y coordinates for given input data points.
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class LinearGraph implements IGraphWayed {
    @Getter
    Vec2d[] wayPoints;
    double[] angles; // angles of each liner

    /**
     * Constructs a composite linear graph using the provided waypoints.
     * They generated linear functions through these waypoints while ensuring multiple connected lines.
     * <p>
     * The waypoints must have strictly ascending x-coordinates.
     * The constructor throws an {@link IllegalStateException} if the conditions are not met.
     *
     * @param wayPoints an array of {@link Vec2d} objects representing the waypoints through which
     *                  the composite linear will pass.
     *                  Each Vec2d contains x and y coordinates for a point.
     */
    public LinearGraph(final Vec2d[] wayPoints) {
        this.wayPoints = wayPoints.clone();
        if (this.wayPoints.length < 1) {
            throw new IllegalStateException(Constants.EX_NO_WAYS);
        }
        for (int i = 0; i + 1 < wayPoints.length; i++) {
            if (wayPoints[i].getX() >= wayPoints[i + 1].getX()) {
                throw new IllegalStateException(Constants.EX_WAYS_ORDER);
            }
        }

        this.angles = new double[this.wayPoints.length - 1];
        this.compute();
    }

    private void compute() {
        for (int i = 0; i < this.wayPoints.length - 1; i++) {
            this.angles[i] = (this.wayPoints[i + 1].getY() - this.wayPoints[i].getY())
                    / (this.wayPoints[i + 1].getX() - this.wayPoints[i].getX());
        }
    }

    /**
     * Computes the y-coordinate for a given x-coordinate using the composite linear functions.
     * If the composite linear functions a single interval, this method directly returns the y-coordinate of that interval.
     * Otherwise, it determines the correct interval index for the given x-coordinate and evaluates the linear within that interval.
     *
     * @param x the x-coordinate for which the corresponding y-coordinate is to be calculated.
     *          Represents the input value for which the linear's output is computed.
     * @return the y-coordinate corresponding to the given x-coordinate on the composite linear.
     * The value is calculated based on the pre-computed angles.
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
     * Evaluates the composite linear at a specific x-coordinate within the given interval.
     * The method calculates the y-coordinate of the linear using pre-computed angles for the specified interval.
     *
     * @param intervalIndex the index of the interval within the waypoint array where the given x-coordinate resides.
     *                      This value is determined using {@link #getReferenceIndex(double)}.
     * @param curX          the x-coordinate for which the y-coordinate of the linear is to be calculated.
     *                      Represents the input point to evaluate the linear function.
     * @return the y-coordinate corresponding to the given x-coordinate on the linear.
     */
    private double evaluateGraph(final int intervalIndex, final double curX) {
        final double deltaX = curX - this.wayPoints[intervalIndex].getX();
        return this.wayPoints[intervalIndex].getY()
                + (this.angles[intervalIndex] * deltaX);
    }
}
