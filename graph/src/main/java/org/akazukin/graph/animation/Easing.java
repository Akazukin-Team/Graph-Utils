package org.akazukin.graph.animation;

public class Easing {
    /**
     * Represents a linear easing function, where the progress value increases proportionally
     * to the elapsed time percentage.
     * This ensures a constant rate of progression over the duration of an animation or transition.
     * The {@link IEasing} interface is used to define the behavior of the easing function.
     * The input {@code timePercent} is directly returned without any modifications.
     */
    public static final IEasing LINEAR =
            timePercent -> timePercent;
    public static final IEasing BOUNCE =
            timePercent -> timePercent < 0.1
                    ? 1
                    :
                    (timePercent < 0.5
                            ? 2
                            :
                            (timePercent < 0.9
                                    ? 3
                                    : 4));

    /**
     * Creates and returns an {@link IEasing} function that defines a custom easing behavior
     * combining exponential acceleration and deceleration over specified time fractions.
     * The easing function transitions smoothly upward and then downward based on the provided
     * exponents and time division ratios.
     *
     * @param upExpo   the exponent to be used for the upward exponential transition.
     * @param downExpo the exponent to be used for the downward exponential transition.
     * @param upTime   the time fraction allocated for the upward part of the transition.
     *                 A value of 0 means no time is allocated for the upward transition.
     * @param downTime the time fraction allocated for the downward part of the transition.
     *                 A value of 0 means no time is allocated for the downward transition.
     * @return an {@link IEasing} function where the progress value is mapped according
     * to the defined exponential easing behavior.
     */
    public static IEasing getExpoUpDown(final double upExpo, final double downExpo, final double upTime, final double downTime) {
        final double upTime2;
        final double downTime2;
        if (upTime == 0 && downTime == 0) {
            upTime2 = 0.5;
            downTime2 = 0.5;
        } else {
            upTime2 = upTime / (upTime + downTime);
            downTime2 = downTime / (upTime + downTime);
        }

        return timePercent -> timePercent <= upTime2
                ? Math.pow(timePercent * (1 / upTime2), upExpo) * upTime2
                : 1 - (Math.pow((1 - timePercent) * (1 / downTime2), downExpo) * downTime2);
    }

    public static IEasing getExpoDownUp(final double downExpo, final double upExpo, final double downTime, final double upTime) {
        final double downTime2;
        final double upTime2;
        if (downTime == 0 && upTime == 0) {
            downTime2 = 0.5;
            upTime2 = 0.5;
        } else {
            downTime2 = downTime / (downTime + upTime);
            upTime2 = upTime / (downTime + upTime);
        }
/*
        return timePercent -> timePercent <= upTime2
                ? (1 - Math.pow(1 - (timePercent * (1 / upTime2)), upExpo)) * upTime2
                : downTime + (Math.pow((timePercent - downTime2) * (1 / upTime2), downExpo) * downTime2);
        */
        return timePercent -> timePercent <= downTime2
                ? (1 - Math.pow(1 - (timePercent * (1 / downTime2)), downExpo)) * downTime2
                : downTime2 + Math.pow((timePercent - downTime2) * (1 / upTime2), upExpo) * upTime2;
    }

    public static IEasing getIntermit(final double time) {
        return timePercent ->
                timePercent < time ? 0 : 1;
    }
}
