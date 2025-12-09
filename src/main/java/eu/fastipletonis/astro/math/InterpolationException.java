package eu.fastipletonis.astro.math;

/**
 * Interpolation exception. It is thrown when it is not possible to perform
 * an interpolation or return a value.
 */
public class InterpolationException extends Exception {
    /// the _x_ value.
    private final double xValue;

    /**
     * Builds a new InterpolationException instance.
     * 
     * @param msg exception message or description
     * @param xValue the <i>x</i> invalid value 
     */
    public InterpolationException(String msg, double xValue) {
        super(msg);
        this.xValue = xValue;
    }

    /**
     * @return the <i>x</i> value
     */
    public double getXValue() {
        return xValue;
    }
}
