package org.akazukin.graph.animation;

/**
 * Functional interface representing an easing function used for animations and transitions.
 * <p>
 * Implementations of this interface define how the progress of an animation is calculated over time.
 * The time percentage should be provided as input, representing the elapsed time as a fraction of the total duration.
 * The result of the implementation determines the progress value of the animation at the given time.
 */
@FunctionalInterface
public interface IEasing {
    /**
     * Calculates the progress value of an animation based on the given time percentage.
     * The calculation depends on the implementation of the easing function.
     *
     * @param timePercent the time percentage of the animation as a double ranging from 0.0 to 1.0.
     *                    A value of 0.0 represents the start of the animation, and 1.0 represents the end.
     * @return the computed progress value as a double, where the result depends on the easing function's implementation.
     * The returned value should be a value between 0.0 and 1.0, representing the progress of the animation.
     */
    double getProgress(double timePercent);
}
