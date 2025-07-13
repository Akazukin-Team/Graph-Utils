package org.akazukin.graph.algorithm.algorithms.hermite;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.akazukin.graph.algorithm.algorithms.Constants;
import org.akazukin.graph.algorithm.graph.IGraph;
import org.akazukin.graph.vec.Vec2d;
import org.akazukin.graph.vec.Vec2d2;

/**
 * Represents a cubic Hermite interpolation graph capable of smooth curve interpolation between defined points.
 * This class computes coefficients for cubic Hermite splines and evaluates the y-value for a given x-value using that data.
 * <p>
 * The cubic Hermite interpolation is defined by a set of points with optional tangent vectors, and it ensures that:
 * - The curves between points are smooth and continuous.
 * - Tangent vectors at control points are respected when provided.
 * - When tangent vectors are not provided (null), they are automatically calculated using finite differences.
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class CubicHermiteGraph implements IGraph {
    @Getter
    Vec2d2[] wayPoints;

    // 各区間ごとのエルミート係数
    double[] thirdCoefficient; // 各区間のa係数 (3次項)
    double[] quadCoefficient; // 各区間のb係数 (2次項)
    double[] firstCoefficient; // 各区間のc係数 (1次項)

    /**
     * Constructs a cubic Hermite interpolation graph using the provided waypoints with optional tangent vectors.
     * The generated Hermite interpolation passes through these waypoints while respecting provided tangent vectors.
     * <p>
     * The waypoints must have strictly ascending x-coordinates.
     * The constructor throws an {@link IllegalStateException} if the conditions are not met.
     *
     * @param wayPoints an array of {@link Vec2d2} objects representing the waypoints through which
     *                  the cubic Hermite interpolation will pass. Each Vec2d2 contains position (Vec2d) and
     *                  optional tangent vector (Vec2d, can be null) for a point.
     */
    public CubicHermiteGraph(final Vec2d2[] wayPoints) {
        this.wayPoints = wayPoints.clone();
        if (this.wayPoints.length < 1) {
            throw new IllegalStateException(Constants.EX_NO_WAYS);
        }
        for (int i = 0; i + 1 < wayPoints.length; i++) {
            if (wayPoints[i].getPos().getX() >= wayPoints[i + 1].getPos().getX()) {
                throw new IllegalStateException(Constants.EX_WAYS_ORDER);
            }
        }

        this.thirdCoefficient = new double[wayPoints.length - 1]; // 各区間のa係数
        this.quadCoefficient = new double[wayPoints.length - 1]; // 各区間のb係数
        this.firstCoefficient = new double[wayPoints.length - 1]; // 各区間のc係数
        this.compute();
    }

    /**
     * Computes the coefficients for the cubic Hermite interpolation.
     * This method calculates and assigns the a, b, c, and d coefficients for each segment
     * of the cubic Hermite curve based on the waypoints and their tangent vectors.
     * When tangent vectors are not provided (null), they are calculated using finite differences.
     * <p>
     * The process involves:
     * <ul>
     * - Calculating missing tangent vectors using finite differences when they are null.
     * - Computing Hermite basis function coefficients for each interval.
     * - Calculating the polynomial coefficients (a, b, c, d) for each interval of the interpolation.
     * <p>
     * This method assumes the input waypoints have strictly ascending x-coordinates,
     * and the resulting Hermite curve is smooth and continuous.
     */
    private void compute() {
        final int wayCount = this.wayPoints.length;

        // 各区間について係数を計算
        for (int i = 0; i + 1 < wayCount; i++) {
            final Vec2d p0 = this.wayPoints[i].getPos(); // Start point position
            final Vec2d p1 = this.wayPoints[i + 1].getPos(); // End point position

            // 傾きベクトルの取得または計算
            Vec2d m0 = this.wayPoints[i].getVector(); // Start point tangent vector
            Vec2d m1 = this.wayPoints[i + 1].getVector(); // End point tangent vector

            // nullの場合は有限差分で計算
            if (m0 == null) {
                m0 = this.calculateTangentVector(i);
                this.wayPoints[i] = new Vec2d2(p0, m0);
            }
            if (m1 == null) {
                m1 = this.calculateTangentVector(i + 1);
                this.wayPoints[i + 1] = new Vec2d2(p1, m1);
            }

            final double deltaX = p1.getX() - p0.getX(); // delta X between points

            // 多項式係数の計算
            this.thirdCoefficient[i] = ((2 * p0.getY()) + (m0.getY() * deltaX) + (-2 * p1.getY()) + (m1.getY() * deltaX))
                    / (deltaX * deltaX * deltaX);

            // b = (-3p₀ - 2m₀·Δx + 3p₁ - m₁·Δx) / (Δx)²
            this.quadCoefficient[i] = ((-3 * p0.getY()) + ((-2 * m0.getY()) * deltaX) + (3 * p1.getY()) + (-1 * m1.getY() * deltaX))
                    / (deltaX * deltaX);
        }
    }

    /**
     * Calculates the tangent vector for a waypoint using finite differences.
     * This method is used when the tangent vector is not provided (null) for a waypoint.
     *
     * @param index the index of the waypoint for which to calculate the tangent vector.
     * @return a {@link Vec2d} representing the calculated tangent vector.
     */
    private Vec2d calculateTangentVector(final int index) {
        final int wayCount = this.wayPoints.length;

        if (wayCount == 1) {
            return new Vec2d(1.0, 0.0); // 単一点の場合は水平方向
        }

        final int i, i2;
        if (index == 0) {
            // 最初の点：前進差分
            i = 0;
            i2 = 1;
        } else if (index + 1 == wayCount) {
            // 最後の点：後進差分
            i = wayCount - 2;
            i2 = wayCount - 1;
        } else {
            // 中間の点：中央差分
            i = index - 1;
            i2 = index + 1;
        }

        final Vec2d p0 = this.wayPoints[i].getPos();
        final Vec2d p1 = this.wayPoints[i2].getPos();
        return new Vec2d(1.0, (p1.getY() - p0.getY()) / (p1.getX() - p0.getX()));
    }

    /**
     * Computes the y-coordinate for a given x-coordinate using the cubic Hermite interpolation.
     * If the cubic Hermite interpolation comprises a single interval, this method directly returns the y-coordinate of that interval.
     * Otherwise, it determines the correct interval index for the given x-coordinate and evaluates the interpolation within that interval.
     *
     * @param x the x-coordinate for which the corresponding y-coordinate is to be calculated.
     *          Represents the input value for which the Hermite curve's output is computed.
     * @return the y-coordinate corresponding to the given x-coordinate on the cubic Hermite curve.
     * The value is calculated based on the pre-computed coefficients.
     */
    @Override
    public double getY(final double x) {
        if (Double.isNaN(x) || Double.isInfinite(x)) {
            throw new IllegalArgumentException(Constants.EX_INVALID_NUM + " | " + x);
        }

        if (this.wayPoints.length == 1) {
            return this.wayPoints[0].getPos().getY();
        }
        final int intervalIndex = this.getReferenceIndex(x);
        return this.evaluateGraph(intervalIndex, x);
    }

    /**
     * Determines the index of the interval within the array of waypoints (represented as {@link Vec2d2})
     * that contains the given x-coordinate.
     * This method performs a binary search to find the correct segment index efficiently.
     *
     * @param x the x-coordinate for which the corresponding interval index is to be found.
     *          Represents the input value whose location in the x-range of the waypoints is determined.
     * @return the index of the way interval such that {@code ways[index].getPos().getX() <= x < ways[index + 1].getPos().getX()}.
     * If {@code x} is less than the smallest x-coordinate, returns 0.
     * If {@code x} exceeds the largest x-coordinate, returns {@code ways.length - 2}.
     */
    private int getReferenceIndex(final double x) {
        final int wayCount = this.wayPoints.length;
        int left = 0;
        int right = wayCount - 2;

        if (x <= this.wayPoints[0].getPos().getX()) {
            return left;
        }
        if (x >= this.wayPoints[wayCount - 1].getPos().getX()) {
            return right;
        }

        while (left < right) {
            final int mid = (left + right + 1) / 2;
            if (this.wayPoints[mid].getPos().getX() <= x) {
                left = mid;
            } else {
                right = mid - 1;
            }
        }

        return left;
    }

    /**
     * Evaluates the cubic Hermite interpolation at a specific x-coordinate within the given interval.
     * The method calculates the y-coordinate of the Hermite curve using pre-computed coefficients for the specified interval.
     *
     * @param intervalIndex the index of the interval within the waypoint array where the given x-coordinate resides.
     *                      This value is determined using {@link #getReferenceIndex(double)}.
     * @param curX          the x-coordinate for which the y-coordinate of the Hermite interpolation is to be calculated.
     *                      Represents the input point to evaluate the Hermite function.
     * @return the y-coordinate corresponding to the given x-coordinate on the Hermite curve.
     */
    private double evaluateGraph(final int intervalIndex, final double curX) {
        final double deltaX = curX - this.wayPoints[intervalIndex].getPos().getX();

        return (((((this.thirdCoefficient[intervalIndex] * deltaX)
                + this.quadCoefficient[intervalIndex]) * deltaX)
                + this.wayPoints[intervalIndex].getVector().getY()) * deltaX)
                + this.wayPoints[intervalIndex].getPos().getY();
    }
}