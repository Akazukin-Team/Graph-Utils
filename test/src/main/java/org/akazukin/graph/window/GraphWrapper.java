package org.akazukin.graph.window;

import org.akazukin.graph.vec.Vec2d;

public interface GraphWrapper {
    /**
     * Returns the value for the given x.
     *
     * @param x the x coordinate
     * @return the value at the given x
     */
    double getValue(double x);

    Vec2d[] getControlPoints();
}
