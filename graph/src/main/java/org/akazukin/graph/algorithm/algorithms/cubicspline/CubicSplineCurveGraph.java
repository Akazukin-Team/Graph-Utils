package org.akazukin.graph.algorithm.algorithms.cubicspline;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.akazukin.graph.algorithm.algorithms.Constants;
import org.akazukin.graph.algorithm.graph.IGraphWayed;
import org.akazukin.graph.vec.Vec2d;

/**
 * Represents a cubic spline graph capable of smooth curve interpolation between defined points.
 * This class computes coefficients for cubic splines and evaluates the y-value for a given x-value using that data.
 * <p>
 * The cubic spline is defined by a set of points (or ways), and it ensures that:
 * - The curves between points are smooth and continuous.
 * - The boundary conditions at the ends are natural splines (second derivatives are zero).
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class CubicSplineCurveGraph implements IGraphWayed {
    private static final double CUBIC_COEFFICIENT = 3.0;
    private static final double QUAD_COEFFICIENT = 2.0;
    private static final double NATURAL_SPLINE_BOUNDARY = 1.0;
    private static final double ZERO_BOUNDARY = 0.0;


    @Getter
    Vec2d[] wayPoints;

    // 各区間ごとのスプライン係数
    double[] secondCoefficient; // 各通過点の2次係数
    double[] firstCoefficient; // 各区間の1次係数
    double[] thirdCoefficient; // 各区間の3次係数

    /**
     * Constructs a cubic spline curve graph using the provided waypoints.
     * The generated spline interpolates through these waypoints while ensuring a smooth curve.
     * <p>
     * The waypoints must have strictly ascending x-coordinates.
     * The constructor throws an {@link IllegalStateException} if the conditions are not met.
     *
     * @param wayPoints an array of {@link Vec2d} objects representing the waypoints through which
     *                  the cubic spline will pass. Each Vec2d contains x and y coordinates for a point.
     */
    public CubicSplineCurveGraph(final Vec2d[] wayPoints) {
        this.wayPoints = wayPoints.clone();
        if (this.wayPoints.length < 1) {
            throw new IllegalStateException(Constants.EX_NO_WAYS);
        }
        for (int i = 0; i + 1 < wayPoints.length; i++) {
            if (wayPoints[i].getX() >= wayPoints[i + 1].getX()) {
                throw new IllegalStateException(Constants.EX_WAYS_ORDER);
            }
        }

        this.secondCoefficient = new double[wayPoints.length]; // 各通過点の2次係数
        this.firstCoefficient = new double[wayPoints.length - 1]; // 各区間の1次係数
        this.thirdCoefficient = new double[wayPoints.length - 1]; // 各区間の3次係数
        this.compute();
    }

    /**
     * Computes the coefficients for the cubic spline interpolation.
     * This method calculates and assigns the first, second, and third coefficients for each segment
     * of the cubic spline curve based on the waypoints ({@link Vec2d} objects representing x and y coordinates).
     * The computation uses the natural spline boundary condition, ensuring the second derivative at the endpoints is zero.
     * <p>
     * The process involves:
     * <ul>
     * - Determining the interval widths between consecutive waypoints.
     * - Calculating the right-hand side values for the system of linear equations for the spline's second derivatives.
     * - Using forward elimination and back substitution to solve the system of equations.
     * - Calculating the first and third derivative coefficients for each interval of the spline.
     * <p>
     * This method assumes the input waypoints have strictly ascending x-coordinates,
     * and the resulting spline curve is smooth and continuous.
     */
    private void compute() {
        final int wayCount = this.wayPoints.length;

        final double[] widthsH = new double[wayCount - 1]; // 各区間のx幅

        // 各区間のx幅をintervalWidthHに格納
        for (int i = 0; i < wayCount - 1; i++) {
            widthsH[i] = this.wayPoints[i + 1].getX() - this.wayPoints[i].getX();
        }

        // 連立方程式の右辺α（各点におけるスプラインの曲率計算用）を計算
        final double[] rightSideAlpha = new double[wayCount];
        for (int i = 1; i < wayCount - 1; i++) {
            rightSideAlpha[i] =
                    CUBIC_COEFFICIENT * (((this.wayPoints[i + 1].getY() - this.wayPoints[i].getY()) / widthsH[i])
                            - ((this.wayPoints[i].getY() - this.wayPoints[i - 1].getY()) / widthsH[i - 1]));
        }

        // 三重対角行列の各種補助配列
        final double[] diagonal = new double[wayCount];  // 対角成分
        final double[] upperDiagonal = new double[wayCount]; // 非対角成分
        final double[] solution = new double[wayCount];  // 右辺ベクトル

        // 自然スプライン（端点2次導関数=0）
        diagonal[0] = diagonal[wayCount - 1] = NATURAL_SPLINE_BOUNDARY;
        upperDiagonal[0] = upperDiagonal[wayCount - 1] = ZERO_BOUNDARY;
        solution[0] = solution[wayCount - 1] = ZERO_BOUNDARY;

        // 前進消去ステップ（ガウスの消去法の一部）
        for (int i = 1; i < wayCount - 1; i++) {
            diagonal[i] = QUAD_COEFFICIENT
                    * (this.wayPoints[i + 1].getX() - this.wayPoints[i - 1].getX())
                    - widthsH[i - 1] * upperDiagonal[i - 1];
            upperDiagonal[i] = widthsH[i] / diagonal[i];
            solution[i] = (rightSideAlpha[i] - widthsH[i - 1] * solution[i - 1]) / diagonal[i];
        }

        // 後退代入ステップ（連立方程式の解を求める）
        this.secondCoefficient[wayCount - 1] = solution[wayCount - 1];
        for (int i = wayCount - 2; i >= 0; i--) {
            this.secondCoefficient[i] = solution[i] - (upperDiagonal[i] * this.secondCoefficient[i + 1]);
            this.firstCoefficient[i] =
                    ((this.wayPoints[i + 1].getY() - this.wayPoints[i].getY()) / widthsH[i])
                            - ((widthsH[i] * (this.secondCoefficient[i + 1] + (QUAD_COEFFICIENT * this.secondCoefficient[i]))) / CUBIC_COEFFICIENT);
            this.thirdCoefficient[i] =
                    (this.secondCoefficient[i + 1] - this.secondCoefficient[i]) / (CUBIC_COEFFICIENT * widthsH[i]);
        }
    }

    /**
     * Computes the y-coordinate for a given x-coordinate using the cubic spline interpolation.
     * If the cubic spline comprises a single interval, this method directly returns the y-coordinate of that interval.
     * Otherwise, it determines the correct interval index for the given x-coordinate and evaluates the spline within that interval.
     *
     * @param x the x-coordinate for which the corresponding y-coordinate is to be calculated.
     *          Represents the input value for which the spline curve's output is computed.
     * @return the y-coordinate corresponding to the given x-coordinate on the cubic spline curve.
     * The value is calculated based on the pre-computed coefficients.
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
     * Evaluates the cubic spline at a specific x-coordinate within the given interval.
     * The method calculates the y-coordinate of the spline curve using pre-computed coefficients for the specified interval.
     *
     * @param intervalIndex the index of the interval within the waypoint array where the given x-coordinate resides.
     *                      This value is determined using {@link #getReferenceIndex(double)}.
     * @param curX          the x-coordinate for which the y-coordinate of the spline is to be calculated.
     *                      Represents the input point to evaluate the spline function.
     * @return the y-coordinate corresponding to the given x-coordinate on the spline curve.
     */
    private double evaluateGraph(final int intervalIndex, final double curX) {
        final double deltaX = curX - this.wayPoints[intervalIndex].getX();
        return (((((this.thirdCoefficient[intervalIndex] * deltaX)
                + this.secondCoefficient[intervalIndex]) * deltaX)
                + this.firstCoefficient[intervalIndex]) * deltaX)
                + this.wayPoints[intervalIndex].getY();
    }
}
