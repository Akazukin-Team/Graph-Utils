package org.akazukin.graph.algorithm.algorithms.bezier;

import org.akazukin.graph.algorithm.builder.IGraphBuilder;
import org.akazukin.graph.algorithm.builder.IGraphControlledBuilder;
import org.akazukin.graph.vec.Vec2d;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Builder class for constructing instances of {@link BezierCurveGraph}.
 * This class provides methods to define control points and create a Bézier curve graph based on them.
 * <p>
 * Implements {@link IGraphBuilder} with {@link BezierCurveGraph} as the generic type.
 */
public final class BezierCurveGraphBuilder implements IGraphControlledBuilder<BezierCurveGraph>, Cloneable {
    List<Vec2d> controlPoints = new ArrayList<>();

    /**
     * Sets the collection of control points to construct the Bézier curve.
     * Each control point is represented as a {@link Vec2d} object, containing x and y coordinates.
     * The control points define the shape of the Bézier curve, and the first and last points become the points through which the curve passes.
     *
     * @param controlPoints an array of {@link Vec2d} objects representing the control points for the Bézier curve.
     */
    @Override
    public void setControlPoints(@NotNull final Vec2d[] controlPoints) {
        this.controlPoints = new ArrayList<>(Arrays.asList(controlPoints));
    }

    /**
     * Adds a control point to the list of control points used to construct the Bézier curve.
     * <p>
     * If the list of control points is {@code null}, it initializes a new {@link ArrayList}.
     * The provided control point defines the control points for the Bézier curve.
     *
     * @param controlPoint the {@link Vec2d} object representing a control point with x and y coordinates.
     */
    @Override
    public void addControlPoint(final Vec2d controlPoint) {
        if (this.controlPoints == null) {
            this.controlPoints = new ArrayList<>();
        }
        this.controlPoints.add(controlPoint);
    }

    @Override
    protected BezierCurveGraphBuilder clone() {
        final BezierCurveGraphBuilder cloned = new BezierCurveGraphBuilder();
        cloned.controlPoints = new ArrayList<>(this.controlPoints);
        return cloned;
    }

    /**
     * Builds a {@link BezierCurveGraph} instance using the provided control points.
     * The control points define the control points for the Bézier curve.
     *
     * @return a new instance of {@link BezierCurveGraph} constructed with the current collection of control points.
     * Throws an exception if the number of control points is lower than 2.
     */
    @Override
    public BezierCurveGraph build() {
        return new BezierCurveGraph(this.controlPoints.toArray(Vec2d.EMPTY));
    }
}