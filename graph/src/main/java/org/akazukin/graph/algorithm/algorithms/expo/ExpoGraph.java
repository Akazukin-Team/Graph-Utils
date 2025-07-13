package org.akazukin.graph.algorithm.algorithms.expo;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.akazukin.graph.algorithm.algorithms.Constants;
import org.akazukin.graph.algorithm.graph.IGraph;
import org.akazukin.graph.vec.Vec2d;

/**
 * Represents an exponentiation graph capable of a smooth curve.
 * This class evaluates the y-value for a given x-value using the start point for exponentiation and the exponentiation amount.
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class ExpoGraph implements IGraph {
    public static final String EX_ZERO_AMOUNT = "The exponentiation amount must not be 0.";
    @Getter
    Vec2d startPoint;
    @Getter
    double amount;

    /**
     * Constructs an exponentiation curve graph using the provided waypoints.
     * The generated exponentiation function through these waypoints while ensuring an exponentiation curve.
     * <p>
     * The waypoints must have strictly ascending x-coordinates.
     * The constructor throws an {@link IllegalStateException} if the conditions are not met.
     *
     * @param startPoint the array of waypoints (represented as {@link Vec2d}) that define the curve.
     *                   Each Vec2d contains x and y coordinates for a point.
     * @param amount     the exponentiation amount, which determines the steepness of the curve.
     *                   The value must not be 0.
     */
    public ExpoGraph(final Vec2d startPoint, final double amount) {
        if (amount == 0) {
            throw new IllegalArgumentException(EX_ZERO_AMOUNT);
        }
        this.startPoint = startPoint;
        this.amount = amount;
    }

    /**
     * Computes the y-coordinate for a given x-coordinate using the exponentiation functions.
     * If the exponentiation functions a single interval, this method directly returns the y-coordinate of that interval.
     * Otherwise, it determines the correct interval index for the given x-coordinate and evaluates the exponentiation within that interval.
     *
     * @param x the x-coordinate for which the corresponding y-coordinate is to be calculated.
     *          Represents the input value for which the exponentiation's output is computed.
     * @return the y-coordinate corresponding to the given x-coordinate on the exponentiation.
     * The value is calculated based on the pre-computed angles.
     */
    @Override
    public double getY(final double x) {
        if (Double.isNaN(x) || Double.isInfinite(x)) {
            throw new IllegalArgumentException(Constants.EX_INVALID_NUM + " | " + x);
        }

        return this.startPoint.getY()
                + Math.pow(x - this.startPoint.getX(), this.amount);
    }
}
