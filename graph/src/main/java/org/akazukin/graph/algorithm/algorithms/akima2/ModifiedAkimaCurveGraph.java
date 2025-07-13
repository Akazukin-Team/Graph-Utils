package org.akazukin.graph.algorithm.algorithms.akima2;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.akazukin.graph.algorithm.algorithms.Constants;
import org.akazukin.graph.algorithm.graph.IGraphWayed;
import org.akazukin.graph.vec.Vec2d;

/**
 * Represents a Modified Akima curve graph capable of smooth curve interpolation between defined points.
 * This class computes derivatives for Modified Akima interpolation and evaluates the y-value for a given x-value using that data.
 * <p>
 * The Modified Akima curve is defined by a set of points (or ways), and it ensures that:
 * - The curves between points are smooth and continuous.
 * - The method uses weighted averages of slopes to reduce oscillations compared to standard cubic splines.
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class ModifiedAkimaCurveGraph implements IGraphWayed {
    private static final double EPSILON = 1e-10;
    private static final double WEIGHT_THRESHOLD = 1e-10;

    @Getter
    Vec2d[] wayPoints;

    double[] derivatives; // first derivatives at each waypoint

    /**
     * Constructs a Modified Akima curve graph using the provided waypoints.
     * The generated curve interpolates through these waypoints while ensuring reduced oscillations.
     * <p>
     * The waypoints must have strictly ascending x-coordinates.
     * The constructor throws an {@link IllegalStateException} if the conditions are not met.
     *
     * @param wayPoints an array of {@link Vec2d} objects representing the waypoints through which
     *                  the Modified Akima curve will pass. Each Vec2d contains x and y coordinates for a point.
     */
    public ModifiedAkimaCurveGraph(final Vec2d[] wayPoints) {
        this.wayPoints = wayPoints.clone();
        if (this.wayPoints.length < 1) {
            throw new IllegalStateException(Constants.EX_NO_WAYS);
        }
        for (int i = 0; i + 1 < wayPoints.length; i++) {
            if (wayPoints[i].getX() >= wayPoints[i + 1].getX()) {
                throw new IllegalStateException(Constants.EX_WAYS_ORDER);
            }
        }

        this.derivatives = new double[wayPoints.length];
        this.compute();
    }

    /**
     * Computes the derivatives for the Modified Akima interpolation.
     * This method calculates and assigns the derivatives for each waypoint
     * of the Modified Akima curve based on the waypoints ({@link Vec2d} objects representing x and y coordinates).
     * The computation uses weighted averages of slopes to reduce oscillations compared to standard cubic splines.
     * <p>
     * The process involves:
     * <ul>
     * - Calculating slopes between consecutive waypoints.
     * - Determining weights based on the differences of consecutive slopes.
     * - Using weighted averages to compute derivatives at each waypoint.
     * - Applying extrapolation for boundary conditions.
     * <p>
     * This method assumes the input waypoints have strictly ascending x-coordinates,
     * and the resulting curve is smooth and continuous with reduced oscillations.
     */
    private void compute() {
        final int n = this.wayPoints.length;

        if (n == 1) {
            this.derivatives[0] = 0.0;
            return;
        }

        if (n == 2) {
            final double slope = (this.wayPoints[1].getY() - this.wayPoints[0].getY()) /
                    (this.wayPoints[1].getX() - this.wayPoints[0].getX());
            this.derivatives[0] = slope;
            this.derivatives[1] = slope;
            return;
        }

        // Calculate slopes between consecutive points
        final double[] slopes = new double[n + 3];

        // Interior slopes
        for (int i = 2; i < n + 1; i++) {
            slopes[i] = (this.wayPoints[i - 1].getY() - this.wayPoints[i - 2].getY()) /
                    (this.wayPoints[i - 1].getX() - this.wayPoints[i - 2].getX());
        }

        // Extrapolate slopes for boundary conditions
        slopes[0] = 2.0 * slopes[2] - slopes[3];
        slopes[1] = 2.0 * slopes[2] - slopes[3];
        slopes[n + 1] = 2.0 * slopes[n] - slopes[n - 1];
        slopes[n + 2] = 2.0 * slopes[n] - slopes[n - 1];

        // Calculate derivatives using Modified Akima weights
        for (int i = 0; i < n; i++) {
            final double w1 = Math.abs(slopes[i + 3] - slopes[i + 2]);
            final double w2 = Math.abs(slopes[i + 1] - slopes[i]);

            if (w1 + w2 < WEIGHT_THRESHOLD) {
                // When weights are very small, use simple average
                this.derivatives[i] = 0.5 * (slopes[i + 1] + slopes[i + 2]);
            } else {
                // Modified Akima weighted average
                this.derivatives[i] = (w1 * slopes[i + 1] + w2 * slopes[i + 2]) / (w1 + w2);
            }
        }
    }

    /**
     * Computes the y-coordinate for a given x-coordinate using the Modified Akima interpolation.
     * If the Modified Akima curve comprises a single interval, this method directly returns the y-coordinate of that interval.
     * Otherwise, it determines the correct interval index for the given x-coordinate and evaluates the curve within that interval.
     *
     * @param x the x-coordinate for which the corresponding y-coordinate is to be calculated.
     *          Represents the input value for which the curve's output is computed.
     * @return the y-coordinate corresponding to the given x-coordinate on the Modified Akima curve.
     * The value is calculated based on the pre-computed derivatives.
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
        return this.evaluateHermite(intervalIndex, x);
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
     * Evaluates the Modified Akima curve at a specific x-coordinate within the given interval.
     * The method calculates the y-coordinate of the curve using pre-computed derivatives for the specified interval.
     *
     * @param intervalIndex the index of the interval within the waypoint array where the given x-coordinate resides.
     *                      This value is determined using {@link #getReferenceIndex(double)}.
     * @param x             the x-coordinate for which the y-coordinate of the curve is to be calculated.
     *                      Represents the input point to evaluate the curve function.
     * @return the y-coordinate corresponding to the given x-coordinate on the curve.
     */
    private double evaluateHermite(final int intervalIndex, final double x) {
        final Vec2d p0 = this.wayPoints[intervalIndex];
        final Vec2d p1 = this.wayPoints[intervalIndex + 1];

        final double h = p1.getX() - p0.getX();
        final double t = (x - p0.getX()) / h;

        final double t2 = t * t;
        final double t3 = t2 * t;

        // Hermite basis functions
        final double h00 = 2.0 * t3 - 3.0 * t2 + 1.0;
        final double h10 = t3 - 2.0 * t2 + t;
        final double h01 = -2.0 * t3 + 3.0 * t2;
        final double h11 = t3 - t2;

        return h00 * p0.getY() +
                h10 * h * this.derivatives[intervalIndex] +
                h01 * p1.getY() +
                h11 * h * this.derivatives[intervalIndex + 1];
    }
}
