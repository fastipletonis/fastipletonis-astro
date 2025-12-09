package eu.fastipletonis.astro.math;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Interpolator from a table.
 */
public class Interpolator implements Serializable {
    /// Serial Version
    private static final long serialVersionUID = 2835649263595134L;
    /// Error message when the _x_ and _y_ arrays' lengths do not match
    private static final String fmtArraysLenMustMatch = "Arrays x[%d] and y[%d] must have the same length.";
    /// Error message when the _x_ and _y_ arrays' lengths are less than 3.
    private static final String fmtArraysLenTooShort = "Arrays must have at least 3 elements. Found: %d";
    /// Error message when the _x_ value is too small.
    private static final String fmtValueTooSmall = "Value %f is too small for interpolation";
    /// Error message when the _x_ value is too big.
    private static final String fmtValueTooBig = "Value %f is too big for interpolation";
    /// Array with the initial y coordinates
    protected final double[] yValues;
    /// Array with the initial x coordinates
    protected final double[] xValues;
    /// Array with the 1st order differences
    protected final double[] yDiff1;
    /// Array with the 2nd order differences
    protected final double[] yDiff2;
    /// Array with the 3rd order differences
    protected final double[] yDiff3;
    /// Flag to determine if we can use `interpolate3()`
    protected final boolean useInterpolation3;
    /// Precision for calculating equality when comparing doubles
    protected final double precision;
    /// Maximum index
    protected final int maxIndex;
    /// Cut-off values: acceptable range for interpolation
    protected final double xMinValue, xMaxValue;

    /**
     * Calculates the difference array.
     * 
     * @param column the column to calculate the differences for
     * 
     * @return an array of differences
     */
    protected double[] diff(double[] column) {
        if (column.length < 2) return new double[0];
        final double[] diff = new double[column.length - 1];
        for (int i = 0; i < diff.length; i++) {
            diff[i] = column[i + 1] - column[i];
        }
        return diff;
    }

    /**
     * Checks if the interpolation can use three values instead of five. It
     * works by checking if the third-order difference is always less than the
     * given <em>smallDiff</em> value.
     * 
     * @param smallDiff what is considered to be a small difference
     * @param yDiff3 the array of third-order differences
     * 
     * @return <code>true</code> if the condition is satisfied for all values
     *         in <em>yDiff3</em>.
     */
    protected boolean canUseInterpolate3(double smallDiff, double[] yDiff3) {
        for (int i = 0; i < yDiff3.length; i++) {
            if (yDiff3[i] > smallDiff) return false;
        }
        return true;
    }

    /**
     * Contruct the interpolator object. The <i>x<i> array must be in ascending
     * order and the <i>y</i> and <i>x</i> arrays must have the same length.
     * 
     * @param x ordered <i>x</i> array
     * @param y <i>y</i> array with the function results
     * @param smallDiff value that will be considered as
     *                  <em>small 3rd order difference</em> when interpolating
     *                  values
     * @param precision precision used to determine equality for double values
     */
    public Interpolator(double[] x, double[] y, double smallDiff, double precision) {
        if (x.length != y.length) {
            final String msg = String.format(fmtArraysLenMustMatch, x.length, y.length);
            throw new IllegalArgumentException(msg);
        } else if (x.length < 3) {
            final String msg = String.format(fmtArraysLenTooShort, x.length);
            throw new IllegalArgumentException(msg);
        }
        this.precision = Math.abs(precision);

        xValues = x;
        yValues = y;

        maxIndex = x.length - 1;
        xMinValue = (x[1] - x[0]) * 0.5d + x[0];
        xMaxValue = (x[maxIndex] - x[maxIndex - 1]) * 0.5d + x[maxIndex - 1];
        
        yDiff1 = diff(y);
        yDiff2 = diff(yDiff1);
        yDiff3 = diff(yDiff2);
        useInterpolation3 = canUseInterpolate3(smallDiff, yDiff3);
    }

    /**
     * Checks if the difference between the parameters <i>a</i> and <i>b</i>
     * is lower or equal to {@link Interpolator#precision}.
     * 
     * @param a first value for comparison
     * @param b second value for comparison
     * 
     * @return <code>true</code> if the values are equal within the required
     *         precision or <code>false</code> otherwise
     */
    protected boolean nearEqual(double a, double b) {
        final double diff = Math.abs(a - b);
        return diff <= precision;
    }

    /**
     * Method to interpolate using three values.
     * 
     * @param x the value for which we want an interpolated <i>y</i>
     * @param idx the index of the nearest 
     * @return the interpolated value
     */
    protected double interpolate3(double x, int idx) {
        final int idxPrev = idx - 1;
        final double x2 = xValues[idx];
        final double y2 = yValues[idx];
        // Interpolating factor
        final double n = x - x2;
        // Differences
        final double a = yDiff1[idxPrev];
        final double b = yDiff1[idx];
        final double c = yDiff2[idxPrev];
        // Interpolation
        return y2 + n / 2 * (a + b + n * c);
    }

    /**
     * 
     * @param x
     * @param idx
     * @return
     */
    protected double interpolate5(double x, int idx) {
        return 0.0d;
    }

    /**
     * Checks if the index 0 is a near-exact match according to
     * {@link #precision} or throw an exception otherwise.
     * 
     * @param x the value to check
     * @param fmt message format for the error message that should accept a
     *            <code>%d</code> argument, that will hold <i>x</i>
     * 
     * @return if the value has a near-exact match, the corresponding <i>y</i>
     *         value 
     * 
     * @throws InterpolationException this exception is thrown if a near-exact
     *                                value could not be found
     */
    protected double yMinIndexNearExactOrFail(double x, String fmt) throws InterpolationException {
        if (nearEqual(xValues[0], x)) return yValues[0];
        else {
            String msg = String.format(fmt, x);
            throw new InterpolationException(msg, x);
        }
    }

    /**
     * Checks if the value for the maximum index or the element before are
     * an exact match or throw an exception otherwise.
     * 
     * @param x the value to check for
     * @param fmt message format for the error message that should accept a
     *            <code>%d</code> argument, that will hold <i>x</i>
     * 
     * @return if the value has a near-exact match, the corresponding <i>y</i>
     *         value 
     * 
     * @throws InterpolationException this exception is thrown if a near-exact
     *                                value could not be found
     */
    protected double yMaxIndexNearExactOrFail(double x, String fmt) throws InterpolationException {
        if (nearEqual(xValues[maxIndex], x)) return yValues[maxIndex];
        else {
            String msg = String.format(fmt, x);
            throw new InterpolationException(msg, x);
        }
    }

    /**
     * Returns the <i>y</i> value for a given <i>x</i> value; if <i>x</i> is
     * not found, it is interpolated.
     * 
     * @param x the <i>x</i> value for which we want to calculate the <i>y</i>
     * @return a tabular value or an interpolated value
     * @throws InterpolationException 
     */
    public double y(double x) throws InterpolationException {
        // Search for the given value.
        final int searchIndex = Arrays.binarySearch(xValues, x);

        // If we have an exact match we return it. We also return near-exact
        // matches for index = 0.
        if (searchIndex > 0) return yValues[searchIndex];
        else if (searchIndex == 0 && x < xMinValue) return yMinIndexNearExactOrFail(x, fmtValueTooSmall);

        // No exact match. the value idx represents where the x we have should
        // be.
        final int idx = -searchIndex - 1;
        final int idxPrev = idx - 1;

        // Check for near-exact matches.
        if (idx == 0 && x < xMinValue) return yMinIndexNearExactOrFail(x, fmtValueTooSmall);
        else if (idx < maxIndex && nearEqual(xValues[idxPrev], x)) return yValues[idxPrev];
        else if (idx < maxIndex && nearEqual(xValues[idx], x)) return yValues[idx];
        else if (idx >= maxIndex && x >= xMaxValue) return yMaxIndexNearExactOrFail(x, fmtValueTooBig);

        // Check the nearest value to x and extract its index.
        final double xMin = xValues[idx - 1];
        final double xMax = xValues[idx];
        final int refIdx = (xMax - x >= x - xMin)? idx - 1: idx;
        if (useInterpolation3) return interpolate3(x, refIdx);
        else return interpolate5(x, refIdx);
    }
}
