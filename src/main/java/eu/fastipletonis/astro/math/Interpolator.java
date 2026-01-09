/*
 *   Interpolator.
 *
 *   Copyright (C) 2025 Marco Confalonieri <marco at marcoconfalonieri.it>
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
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
    /// Error message when the interval is not uniform.
    private static final String msgIntervalNotUniform = "The interval between samples is not uniform";

    /**
     * A value pair.
     */
    public static class ValuePair implements Serializable {
        /// Serial Version
        private static final long serialVersionUID = 2835649263976134L;

        public final double x;
        public final double y;

        /**
         * Create a new value pair.
         * 
         * @param x x
         * @param y y
         */
        protected ValuePair(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }

    /**
     * Checks if the difference between the parameters <i>a</i> and <i>b</i>
     * is lower or equal to the given precision.
     * 
     * @param a first value for comparison
     * @param b second value for comparison
     * @param precision precision required
     * 
     * @return <code>true</code> if the values are equal within the required
     *         precision or <code>false</code> otherwise
     */
    protected static boolean nearEqual(double a, double b, double precision) {
        final double diff = Math.abs(a - b);
        return diff <= precision;
    }

    /**
     * Checks if the array is equally spaced. The interval between <i>x</i>
     * values needs to be uniform for the max/min and zero methods to function
     * correctly.
     * 
     * @param v array to check
     * @param delta the reference delta
     * @param precision precision required
     * 
     * @throws IllegalArgumentException thrown if the interval is not uniform
     */
    protected static void checkInterval(double[] v, double delta, double precision) {
        for (int i = 2; i < v.length; i++) {
            if (!nearEqual(v[i] - v[i - 1], delta, precision)) {
                throw new IllegalArgumentException(msgIntervalNotUniform);
            }
        }
    }

    /**
     * Calculates the difference array.
     * 
     * @param column the column to calculate the differences for
     * 
     * @return an array of differences
     */
    protected static double[] diff(double[] column) {
        if (column.length < 2)
            return new double[0];
        final double[] diff = new double[column.length - 1];
        for (int i = 0; i < diff.length; i++) {
            diff[i] = column[i + 1] - column[i];
        }
        return diff;
    }

    /**
     * Finds the <em>extrememum</em> around the given index.
     * 
     * @param args the arguments for calculations
     * 
     * @return the extremum value Y
     */
    protected static double findExtY(InternalArgs args) {
        final double sum_ab = args.a + args.b;
        return args.y2 - (sum_ab * sum_ab) / (8 * args.c);
    }

    /**
     * Finds the multiplier in tabular units for the <em>extrememum</em> around
     * the given index. For example 
     * 
     * @param args the arguments for calculations
     * @param delta the reference delta
     * 
     * @return the <em>n</em> for the extremum
     */
    protected static double findExtX(InternalArgs args, double delta) {
        final double sum_ab = args.a + args.b;
        final double n = -sum_ab / (2 * args.c);
        return n * delta;
    }

    /**
     * Checks if the interpolation can use three values instead of five. It
     * works by checking if the third-order difference is always less than the
     * given <em>smallDiff</em> value.
     * 
     * @param smallDiff what is considered to be a small difference
     * @param yDiff3    the array of third-order differences
     * 
     * @return <code>true</code> if the condition is satisfied for all values
     *         in <em>yDiff3</em>.
     */
    protected static boolean canUseInterpolate3(double smallDiff, double[] yDiff3) {
        for (int i = 0; i < yDiff3.length; i++) {
            if (yDiff3[i] > smallDiff)
                return false;
        }
        return true;
    }

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
    /// Minimum pair
    protected final ValuePair minPair;
    /// Maximum pair
    protected final ValuePair maxPair;
    /// Zero x
    protected final Double zeroX;

    /**
     * Internal arguments.
     */
    protected class InternalArgs implements Serializable {
        /// Serial Version
        private static final long serialVersionUID = 1066492078679534L;

        // Indexes
        final int idx;
        final int idxPrev;
        // Values
        final double x2;
        final double y2;
        // Differences
        final double a;
        final double b;
        final double c;

        InternalArgs(int i) {
            idx = i;
            idxPrev = idx - 1;

            x2 = xValues[idx];
            y2 = yValues[idx];

            a = yDiff1[idxPrev];
            b = yDiff1[idx];
            c = yDiff2[idxPrev];
        }
    }

    /**
     * Contruct the interpolator object. The <i>x<i> array must be in ascending
     * order and the <i>y</i> and <i>x</i> arrays must have the same length.
     * 
     * @param x         ordered <i>x</i> array
     * @param y         <i>y</i> array with the function results
     * @param smallDiff value that will be considered as
     *                  <em>small 3rd order difference</em> when interpolating
     *                  values
     * @param precision precision used to determine equality for double values
     */
    public Interpolator(double[] x, double[] y, double smallDiff, double precision, int maxIterations) {
        if (x.length != y.length) {
            final String msg = String.format(fmtArraysLenMustMatch, x.length, y.length);
            throw new IllegalArgumentException(msg);
        } else if (x.length < 3) {
            final String msg = String.format(fmtArraysLenTooShort, x.length);
            throw new IllegalArgumentException(msg);
        }

        final double delta = x[1] - x[0];
        this.precision = Math.abs(precision);
        checkInterval(x, delta, this.precision);


        xValues = x;
        yValues = y;

        maxIndex = x.length - 1;
        xMinValue = (x[1] - x[0]) * 0.5d + x[0];
        xMaxValue = (x[maxIndex] - x[maxIndex - 1]) * 0.5d + x[maxIndex - 1];

        yDiff1 = diff(y);
        yDiff2 = diff(yDiff1);
        yDiff3 = diff(yDiff2);
        useInterpolation3 = canUseInterpolate3(smallDiff, yDiff3);

        // Find minimums, maximums and zeroes.
        int minIdx = 1, maxIdx = 1, zeroIdx = 1;
        double min = yValues[1], max = yValues[1], diffZero = Math.abs(yValues[1]);
        for (int idx = 2; idx < maxIndex; idx++) {
            if (yValues[idx] < min) {
                min = yValues[idx];
                minIdx = idx;
            } else if (yValues[idx] > max) {
                max = yValues[idx];
                maxIdx = idx;
            }
            if (Math.abs(yValues[idx]) < diffZero) {
                diffZero = Math.abs(yValues[idx]);
                zeroIdx = idx;
            }
        }
        final InternalArgs minArgs = new InternalArgs(minIdx);
        final InternalArgs maxArgs = new InternalArgs(maxIdx);
        minPair = new ValuePair(findExtX(minArgs, delta), findExtY(minArgs));
        maxPair = new ValuePair(findExtX(maxArgs, delta), findExtY(maxArgs));

        double n0 = 0d;
        double n0diff = 1.0d;
        final InternalArgs zArgs = new InternalArgs(zeroIdx);
        final double y2 = yValues[zeroIdx];
        int iterations = 0;
        boolean exceededIterations = false;
        while (!nearEqual(n0diff, 0.0d, this.precision) && !exceededIterations) {
            double n0old = n0;
            n0 = (-2.0d * y2) / (zArgs.a + zArgs.b + zArgs.c * n0);
            n0diff = n0 - n0old;
            iterations++;
            exceededIterations = (iterations > maxIterations);
        }
        zeroX = exceededIterations? null : n0 * delta;
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
        return nearEqual(a, b, precision);
    }

    /**
     * Method to interpolate using three values.
     * 
     * @param x   the value for which we want an interpolated <i>y</i>
     * @param idx the index of the nearest
     * @return the interpolated value
     */
    protected double interpolate3(double x, int idx) {
        final InternalArgs args = new InternalArgs(idx);
        // Interpolating factor
        final double n = x - args.x2;
        // Interpolation
        return args.y2 + n / 2 * (args.a + args.b + n * args.c);
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
     * @param x   the value to check
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
        if (nearEqual(xValues[0], x))
            return yValues[0];
        else {
            String msg = String.format(fmt, x);
            throw new InterpolationException(msg, x);
        }
    }

    /**
     * Checks if the value for the maximum index or the element before are
     * an exact match or throw an exception otherwise.
     * 
     * @param x   the value to check for
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
        if (nearEqual(xValues[maxIndex], x))
            return yValues[maxIndex];
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
        if (searchIndex > 0)
            return yValues[searchIndex];
        else if (searchIndex == 0 && x < xMinValue)
            return yMinIndexNearExactOrFail(x, fmtValueTooSmall);

        // No exact match. the value idx represents where the x we have should
        // be.
        final int idx = -searchIndex - 1;
        final int idxPrev = idx - 1;

        // Check for near-exact matches.
        if (idx == 0 && x < xMinValue)
            return yMinIndexNearExactOrFail(x, fmtValueTooSmall);
        else if (idx < maxIndex && nearEqual(xValues[idxPrev], x))
            return yValues[idxPrev];
        else if (idx < maxIndex && nearEqual(xValues[idx], x))
            return yValues[idx];
        else if (idx >= maxIndex && x >= xMaxValue)
            return yMaxIndexNearExactOrFail(x, fmtValueTooBig);

        // Check the nearest value to x and extract its index.
        final double xMin = xValues[idx - 1];
        final double xMax = xValues[idx];
        final int refIdx = (xMax - x >= x - xMin) ? idx - 1 : idx;
        if (useInterpolation3)
            return interpolate3(x, refIdx);
        else
            return interpolate5(x, refIdx);
    }

    /**
     * Returns the minimum value and its argument.
     * 
     * @return the minimum pair
     */
    public ValuePair getMinPair() {
        throw new UnsupportedOperationException("TODO");
        //return minPair;
    }
    /**
     * Returns the maximum value and its argument.
     * 
     * @return the maximum pair
     */
    public ValuePair getMaxPair() {
        throw new UnsupportedOperationException("TODO");
        //return maxPair;
    }
}
