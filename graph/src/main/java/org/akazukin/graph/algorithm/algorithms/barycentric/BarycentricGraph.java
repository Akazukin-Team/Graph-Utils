package org.akazukin.graph.algorithm.algorithms.barycentric;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.akazukin.graph.algorithm.algorithms.Constants;
import org.akazukin.graph.algorithm.graph.IGraphWayed;
import org.akazukin.graph.vec.Vec2d;

/**
 * Represents a barycentric interpolation graph that computes weighted averages
 * based on distances.
 * This class performs barycentric interpolation between defined points,
 * calculating weighted
 * averages of waypoints inversely proportional to their distances for any given
 * x-coordinate.
 * <p>
 * The barycentric graph is defined by a set of waypoints, and it ensures that:
 * - The waypoints have strictly ascending x-coordinates.
 * - For any given x-coordinate, the y-value is determined by weighted average
 * of waypoints based on distance.
 */
@Deprecated
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class BarycentricGraph implements IGraphWayed {
    private static final double EPSILON = 1e-10;
    private static final double DEFAULT_POWER = 3;

    @Getter
    Vec2d[] wayPoints;
    double power;
    long debug = System.currentTimeMillis();

    /**
     * Constructs a barycentric interpolation graph using the provided waypoints.
     * The generated graph calculates weighted averages based on distances for any
     * given x-coordinate.
     * <p>
     * The waypoints must have strictly ascending x-coordinates.
     * The constructor throws an {@link IllegalStateException} if the conditions are
     * not met.
     *
     * @param wayPoints an array of {@link Vec2d} objects representing the waypoints for
     *                  the barycentric interpolation. Each Vec2d contains x and y
     *                  coordinates for a point.
     */
    public BarycentricGraph(final Vec2d[] wayPoints) {
        this.wayPoints = wayPoints.clone();
        this.power = DEFAULT_POWER;
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
     * Computes the y-coordinate for a given x-coordinate using the barycentric
     * interpolation.
     * If the barycentric graph comprises a single waypoint, this method directly
     * returns the y-coordinate of that waypoint.
     * Otherwise, it calculates the weighted average of all waypoints based on
     * inverse distance weighting.
     *
     * @param x the x-coordinate for which the corresponding y-coordinate is to be
     *          calculated.
     *          Represents the input value for which the barycentric interpolation's
     *          output is computed.
     * @return the y-coordinate corresponding to the given x-coordinate using
     * barycentric interpolation.
     * The value is calculated based on weighted average of waypoints.
     */
    @Override
    public double getY(final double x) {
        if (Double.isNaN(x) || Double.isInfinite(x)) {
            throw new IllegalArgumentException(Constants.EX_INVALID_NUM + " | " + x);
        }

        if (this.wayPoints.length == 1) {
            return this.wayPoints[0].getY();
        }
        return this.evaluateGraph(x);
    }

    /**
     * Evaluates the barycentric interpolation at a specific x-coordinate.
     * The method calculates the y-coordinate using true barycentric interpolation.
     * If the x-coordinate exactly matches a waypoint, returns that waypoint's y-coordinate directly.
     *
     * @param curX the x-coordinate for which the y-coordinate using barycentric interpolation is to be calculated.
     * @return the y-coordinate corresponding to the given x-coordinate using barycentric interpolation.
     */
    private double evaluateGraph(final double curX) {
        // Check if x exactly matches any waypoint
        for (final Vec2d way : this.wayPoints) {
            if (Math.abs(way.getX() - curX) < EPSILON) {
                return way.getY();
            }
        }

        double weightSum = 0.0; // Σwi(x)
        double weightedSum = 0.0; // Σ(wi(x) * yi)

        // Calculate inverse distance weights
        for (final Vec2d way : this.wayPoints) {
            final double absDeltaX = Math.abs(way.getX() - curX);

            // Handle very small distances to avoid division by zero
            if (absDeltaX < EPSILON) {
                return way.getY();
            }

            final double weight = 1 / Math.pow(absDeltaX, 3.3);
            if (absDeltaX % 10 == 0) {
                System.out.println("deltaX: " + absDeltaX + ", weight: " + weight);
            }

            weightSum += weight;
            weightedSum += weight * way.getY();
        }

        return weightedSum / weightSum;
    }
}
