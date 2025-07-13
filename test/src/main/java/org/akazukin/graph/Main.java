package org.akazukin.graph;

import org.akazukin.graph.algorithm.algorithms.hermite.CubicHermiteGraph;
import org.akazukin.graph.vec.Vec2d;
import org.akazukin.graph.vec.Vec2d2;
import org.akazukin.graph.window.Frame;
import org.akazukin.graph.window.GraphWrapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {

    public static void main(final String[] args) throws InterruptedException {
        t
        final Frame frame = new Frame();

        final Vec2d[] vec2ds;
        final Vec2d2[] vec2d2s;
        final Vec2d base = new Vec2d(0, 0);
        {
            final List<Vec2d> ways = new ArrayList<>();
            final double[] x = {22, 32, 64, 77, 105, 139, 194, 246, 253, 263, 282, 299, 319, 360, 390, 397, 399, 442, 458};
            final double[] y = {309, 267, 175, 141, 124, 124, 141, 174, 232, 301, 345, 355, 347, 322, 282, 234, 163, 112, 99};
            for (int i = 0; i < x.length; i++) {
                ways.add(new Vec2d(x[i], y[i]));
            }
                /*ways.add(new Vec2d(0, 0));
                ways.add(new Vec2d(0.025, 0.02));
                ways.add(new Vec2d(0.1, 0.2));
                ways.add(new Vec2d(0.5, 0.3));
                ways.add(new Vec2d(0.7, 0.9));
                ways.add(new Vec2d(0.75, 0.8));
                ways.add(new Vec2d(0.95, 1.3));
                ways.add(new Vec2d(1, 1));*/
            vec2ds = ways.toArray(Vec2d.EMPTY);
            vec2d2s = Arrays.stream(vec2ds).map(v -> new Vec2d2(v, null)).toArray(Vec2d2[]::new);
        }
/*
        frame.getPanel().addGraph(new GraphWrapper() {
            public final BarycentricGraph graph;

            {
                this.graph = new BarycentricGraph(vec2ds);
            }

            @Override
            public double getValue(final double x) {
                return this.graph.getY(x);
            }

            @Override
            public Vec2d[] getControlPoints() {
                return this.graph.getWayPoints();
            }
        });*/
/*
        frame.getPanel().addGraph(new GraphWrapper() {
            public final CubicSplineCurveGraph graph;

            {
                this.graph = new CubicSplineCurveGraph(vec2ds);
            }

            @Override
            public double getValue(final double x) {
                return this.graph.getY(x);
            }

            @Override
            public Vec2d[] getControlPoints() {
                return this.graph.getWays();
            }
        });
        frame.getPanel().addGraph(new GraphWrapper() {
            public final ModifiedAkimaCurveGraph graph;

            {
                this.graph = new ModifiedAkimaCurveGraph(vec2ds);
            }

            @Override
            public double getValue(final double x) {
                return this.graph.getY(x);
            }

            @Override
            public Vec2d[] getControlPoints() {
                return this.graph.getWays();
            }
        });*/
        frame.getPanel().addGraph(new GraphWrapper() {
            public final CubicHermiteGraph graph;

            {
                this.graph = new CubicHermiteGraph(vec2d2s);
            }

            @Override
            public double getValue(final double x) {
                return this.graph.getY(x);
            }

            @Override
            public Vec2d[] getControlPoints() {
                return Arrays.stream(this.graph.getWayPoints()).map(Vec2d2::getPos).toArray(Vec2d[]::new);
            }
        });/*
        frame.getPanel().addGraph(new GraphWrapper() {
            public final LinearGraph graph;

            {
                this.graph = new LinearGraph(vec2ds);
            }

            @Override
            public double getValue(final double x) {
                return this.graph.getY(x);
            }

            @Override
            public Vec2d[] getControlPoints() {
                return this.graph.getWays();
            }
        });
        frame.getPanel().addGraph(new GraphWrapper() {
            public final NearbyGraph graph;

            {
                this.graph = new NearbyGraph(vec2ds);
            }

            @Override
            public double getValue(final double x) {
                return this.graph.getY(x);
            }

            @Override
            public Vec2d[] getControlPoints() {
                return this.graph.getWays();
            }
        });
        frame.getPanel().addGraph(new GraphWrapper() {
            public final NearbyAverageGraph graph;

            {
                this.graph = new NearbyAverageGraph(vec2ds);
            }

            @Override
            public double getValue(final double x) {
                return this.graph.getY(x);
            }

            @Override
            public Vec2d[] getControlPoints() {
                return this.graph.getWays();
            }
        });
        frame.getPanel().addGraph(new GraphWrapper() {
            public final BezierCurveGraph graph;

            {
                this.graph = new BezierCurveGraph(vec2ds);
            }

            @Override
            public double getValue(final double x) {
                return this.graph.getY(x);
            }

            @Override
            public Vec2d[] getControlPoints() {
                return this.graph.getControlPoints();
            }
        });
        frame.getPanel().addGraph(new GraphWrapper() {
            public final ExpoGraph graph;

            {
                this.graph = new ExpoGraph(base, 2);
            }

            @Override
            public double getValue(final double x) {
                return this.graph.getY(x);
            }

            @Override
            public Vec2d[] getControlPoints() {
                return new Vec2d[]{this.graph.getStartPoint()};
            }
        });*/

        frame.getPanel().setGraphUnitSize(500);
        frame.getPanel().setGraphPointUnit(1);
        frame.getPanel().setGraphMargin(200);


        frame.setVisible(true);

        while (true) {
            Thread.sleep(1000);
            frame.getPanel().repaint();
        }
    }
}
