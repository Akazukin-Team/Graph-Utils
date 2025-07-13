package org.akazukin.graph.algorithm.algorithms.bezier;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.akazukin.graph.algorithm.algorithms.Constants;
import org.akazukin.graph.algorithm.graph.IGraphControlled;
import org.akazukin.graph.vec.Vec2d;

/**
 * Represents a Bézier curve graph capable of smooth curve interpolation using control points.
 * This class computes coefficients for Bézier curves and evaluates the y-value for a given x-value using that data.
 * <p>
 * The Bézier curve is defined by a set of control points, and it ensures that:
 * - The curve passes through the first and last control points.
 * - The intermediate control points shape the curve but are not necessarily passed through.
 * - The curve is smooth and continuous.
 */

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class BezierCurveGraph implements IGraphControlled {
    private static final double PARAMETER_MIN = 0.0;
    private static final double PARAMETER_MAX = 1.0;
    private static final double BINARY_SEARCH_EPSILON = 1e-10;
    private static final int MAX_BINARY_SEARCH_ITERATIONS = 100;

    @Getter
    Vec2d startPoint; // start point (first control point)
    @Getter
    Vec2d endPoint; // end point (last control point)

    @Getter
    Vec2d[] controlPoints; // control points

    int degree; // Bezier curve degree
    double[] binomialCoefficients; // binomial coefficients

    /**
     * Constructs a Bézier curve graph using the provided control points.
     * The generated Bézier curve passes through the first and last control points while being shaped by the intermediate points.
     * <p>
     * The control points must contain at least two points.
     * The constructor throws an {@link IllegalStateException} if the conditions are not met.
     *
     * @param controlPoints an array of {@link Vec2d} objects representing the control points that define
     *                      the Bézier curve. Each Vec2d contains x and y coordinates for a point.
     */
    public BezierCurveGraph(final Vec2d[] controlPoints) {
        this.controlPoints = controlPoints.clone();
        if (this.controlPoints.length < 2) {
            throw new IllegalStateException(Constants.EX_NO_WAYS);
        }

        this.startPoint = this.controlPoints[0];
        this.endPoint = this.controlPoints[this.controlPoints.length - 1];

        this.degree = this.controlPoints.length - 1;
        this.binomialCoefficients = new double[this.degree + 1];
        this.compute();
    }

    /**
     * Computes the binomial coefficients for the Bézier curve interpolation.
     * This method calculates and assigns the binomial coefficients C(n,i) for each basis function
     * of the Bézier curve based on the control points ({@link Vec2d} objects representing x and y coordinates).
     * <p>
     * The process involves:
     * <ul>
     * - Calculating binomial coefficients C(n,i) = n! / (i! * (n-i)!) for each control point.
     * <p>
     * This method assumes the input control points are valid,
     * and the resulting Bézier curve is smooth and continuous.
     */

    private void compute() {
        for (int i = 0; i <= this.degree; i++) {
            this.binomialCoefficients[i] = this.binomialCoefficient(this.degree, i);
        }
    }

    /**
     * Computes the binomial coefficient C(n, k).
     *
     * @param n the upper value
     * @param k the lower value
     * @return the binomial coefficient value
     */
    private double binomialCoefficient(final int n, final int k) {
        if (k > n || k < 0) {
            return 0.0;
        }
        if (k == 0 || k == n) {
            return 1.0;
        }

        double result = 1.0;
        for (int i = 0; i < Math.min(k, n - k); i++) {
            result = result * (n - i) / (i + 1);
        }
        return result;
    }

    /**
     * Computes the y-coordinate for a given x-coordinate using the Bézier curve interpolation.
     * This method determines the parameter t corresponding to the given x-coordinate and evaluates the curve at that parameter.
     *
     * @param x the x-coordinate for which the corresponding y-coordinate is to be calculated.
     *          Represents the input value for which the Bézier curve's output is computed.
     * @return the y-coordinate corresponding to the given x-coordinate on the Bézier curve.
     * The value is calculated based on the pre-computed coefficients.
     */
    @Override
    public double getY(final double x) {
        if (Double.isNaN(x) || Double.isInfinite(x)) {
            throw new IllegalArgumentException(Constants.EX_INVALID_NUM + " | " + x);
        }

        if (this.controlPoints.length == 1) {
            return this.startPoint.getY();
        }

        final double t = this.findParameterForX(x);
        return this.evaluateY(t);
    }

    /**
     * Determines the parameter t within the Bézier curve that corresponds to the given x-coordinate.
     * This method performs a binary search to find the correct parameter value efficiently.
     *
     * @param x the x-coordinate for which the corresponding parameter t is to be found.
     *          Represents the input value whose location on the Bézier curve is determined.
     * @return the parameter t such that the Bézier curve at parameter t has x-coordinate closest to targetX.
     * Returns a value between 0.0 and 1.0.
     */
    private double findParameterForX(final double x) {
        // 境界チェック
        final double startX = this.startPoint.getX();
        final double endX = this.endPoint.getX();

        if (x <= startX) {
            return PARAMETER_MIN;
        }
        if (x >= endX) {
            return PARAMETER_MAX;
        }

        // 二分探索でtを見つける
        double tMin = PARAMETER_MIN;
        double tMax = PARAMETER_MAX;

        for (int iteration = 0; iteration < MAX_BINARY_SEARCH_ITERATIONS; iteration++) {
            final double tMid = (tMin + tMax) / 2.0;
            final double xMid = this.evaluateX(tMid);

            if (Math.abs(xMid - x) < BINARY_SEARCH_EPSILON) {
                return tMid;
            }

            if (xMid < x) {
                tMin = tMid;
            } else {
                tMax = tMid;
            }
        }

        return (tMin + tMax) / 2.0;
    }

    /**
     * Evaluates the Bézier curve at a specific parameter t to get the x-coordinate.
     * The method calculates the x-coordinate of the Bézier curve using pre-computed coefficients for the specified parameter.
     *
     * @param t the parameter value (0.0 ≤ t ≤ 1.0) at which the x-coordinate of the Bézier curve is to be calculated.
     * @return the x-coordinate corresponding to the given parameter t on the Bézier curve.
     */
    private double evaluateX(final double t) {
        double x = 0.0;
        final double t2 = 1.0 - t;

        for (int i = 0; i <= this.degree; i++) {
            final double basis = this.binomialCoefficients[i]
                    * Math.pow(t2, this.degree - i)
                    * Math.pow(t, i);
            x += basis * this.controlPoints[i].getX();
        }

        return x;
    }

    /**
     * Evaluates the Bézier curve at a specific parameter t to get the y-coordinate.
     * The method calculates the y-coordinate of the Bézier curve using pre-computed coefficients for the specified parameter.
     *
     * @param t the parameter value (0.0 ≤ t ≤ 1.0) at which the y-coordinate of the Bézier curve is to be calculated.
     * @return the y-coordinate corresponding to the given parameter t on the Bézier curve.
     */
    private double evaluateY(final double t) {
        double y = 0.0;
        final double t2 = 1.0 - t;

        for (int i = 0; i <= this.degree; i++) {
            final double basis = this.binomialCoefficients[i]
                    * Math.pow(t2, this.degree - i)
                    * Math.pow(t, i);
            y += basis * this.controlPoints[i].getY();
        }

        return y;
    }
}