package eu.fastipletonis.astro.math;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.provider.CsvSource;

import eu.fastipletonis.astro.math.Interpolator.InternalArgs;
import eu.fastipletonis.astro.math.Interpolator.ValuePair;
import eu.fastipletonis.astro.temporal.Queries;
import eu.fastipletonis.astro.testconv.DoubleArrayConverter;
import eu.fastipletonis.astro.testconv.JulianDayConverter;

/**
 * Test suite for Interpolator.
 */
public class InterpolatorTest {
    /// What do we consider as a small difference.
    protected final static double SMALL_DIFF = 1.0e-3d;
    /// Precision for equality checks on double values.
    protected final static double PRECISION = 1.0e-6d;
    protected final static double HIGH_PRECISION = 1.0e-7d;
    /// Maximum number of iterations to find a zero
    protected final static int MAX_ITERATIONS = 1000;

    @Test
    void testValuePair() {
        final double x = 5.0d;
        final double y = 7.0d;
        final ValuePair obj = new ValuePair(x, y);
        assertEquals(x, obj.x);
        assertEquals(y, obj.y);
    }

    @ParameterizedTest
    @CsvSource(useHeadersInDisplayName = true, textBlock = """
                EXPECTED,   A,          B
                true,       0.5,        0.5
                false,      0.4,        0.5
                false,      0.00004,    0.00005
                false,      0.000004,   0.000005
                true,       0.0000004,  0.0000005
                true,       0.00000004, 0.00000005
            """)
    void testNearEqual_3args(boolean exp, double a, double b) {
        final boolean act = Interpolator.nearEqual(a, b, PRECISION);
        assertEquals(exp, act);
    }

    @ParameterizedTest
    @CsvSource(useHeadersInDisplayName = true, textBlock = """
                THROWS, DELTA,  INPUT
                false,  1.0,    1.0;2.0;3.0;4.0
                true,   1.0,    1.0;2.0;3.5;4.0
            """)
    void testCheckInterval(boolean throwsException, double delta,
            @ConvertWith(DoubleArrayConverter.class) double[] input) {
        final String inputString = Arrays.toString(input);
        try {
            Interpolator.checkInterval(input, delta, PRECISION);
            String fmt = "IllegalArgumentException expected for delta %f and input %s";
            assertFalse(throwsException, String.format(fmt, delta, inputString));
        } catch (IllegalArgumentException ex) {
            String fmt = "IllegalArgumentException not expected for delta %f and input %s";
            assertTrue(throwsException, String.format(fmt, delta, inputString));
        }
    }

    @ParameterizedTest
    @CsvSource(useHeadersInDisplayName = true, textBlock = """
                EXPECTED,                               INPUT
                -6904e-6;-6883e-6;-6860e-6;-6835e-6,    0.898013;0.891109;0.884226;0.877366;0.870531
                21e-6;23e-6;25e-6,                      -6904e-6;-6883e-6;-6860e-6;-6835e-6
                2e-6;2e-6,                              21e-6;23e-6;25e-6
            """)
    void testDiff(@ConvertWith(DoubleArrayConverter.class) double[] expected,
            @ConvertWith(DoubleArrayConverter.class) double[] input) {
        final double[] actual = Interpolator.diff(input);
        assertArrayEquals(expected, actual, PRECISION);
    }

    @Test
    void testFindExtY() {
        final String[] xDates = { "1992-05-12T00:00", "1992-05-16T00:00", "1992-05-20T00:00" };
        final double[] xv = JulianDayConverter.convertArray(xDates);
        final double[] yv = { 1.3814294d, 1.3812213d, 1.3812453d };
        final double expected = 1.3812030d;
        final Interpolator obj = new Interpolator(xv, yv, SMALL_DIFF, HIGH_PRECISION, MAX_ITERATIONS);
        final InternalArgs args = obj.new InternalArgs(1);
        final double actual = Interpolator.findExtY(args);
        assertEquals(expected, actual, HIGH_PRECISION);
    }

    @Test
    void testFindExtX() {
        final String[] xDates = { "1992-05-12T00:00", "1992-05-16T00:00", "1992-05-20T00:00" };
        final double[] xv = JulianDayConverter.convertArray(xDates);
        final double[] yv = { 1.3814294d, 1.3812213d, 1.3812453d };
        final double delta = xv[1] - xv[0];
        final double expected = 1.586385d;
        final Interpolator obj = new Interpolator(xv, yv, SMALL_DIFF, PRECISION, MAX_ITERATIONS);
        final InternalArgs args = obj.new InternalArgs(1);
        final double actual = Interpolator.findExtX(args, delta);
        assertEquals(expected, actual, PRECISION);
    }

    @ParameterizedTest
    @CsvSource(useHeadersInDisplayName = true, textBlock = """
                EXPECTED,   INPUT
                false,      1.0;1.0;1.0;1.0;1.0
                false,      0.0000;0.0001;0.0002;0.0050
                true,       0.0000;0.0001;0.0002;0.0003
            """)
    void testCanUseInterpolate3(boolean expected, @ConvertWith(DoubleArrayConverter.class) double[] input) {
        final boolean actual = Interpolator.canUseInterpolate3(SMALL_DIFF, input);
        assertEquals(expected, actual);
    }

    @Test
    void testInternalArgs() {
        final String[] xDates = { "1992-05-12T00:00", "1992-05-16T00:00", "1992-05-20T00:00" };
        final double[] xv = JulianDayConverter.convertArray(xDates);
        final double[] yv = { 1.3814294d, 1.3812213d, 1.3812453d };
        final double a = -0.0002081d;
        final double b = 0.0000240d;
        final double c = 0.0002321d;
        final Interpolator obj = new Interpolator(xv, yv, SMALL_DIFF, HIGH_PRECISION, MAX_ITERATIONS);
        final InternalArgs args = obj.new InternalArgs(1);
        assertEquals(a, args.a, HIGH_PRECISION);
        assertEquals(b, args.b, HIGH_PRECISION);
        assertEquals(c, args.c, HIGH_PRECISION);
    }

    @ParameterizedTest
    @CsvSource(useHeadersInDisplayName = true, textBlock = """
                THROWS, X,                      Y
                false,  1.0;2.0;3.0,            1.0;4.0;9.0
                true,   1.0;2.0;3.0,            1.0;4.0;9.0;16.0
                true,   1.0;2.0,                1.0;4.0
                true,   1.0;3.0;4.0,            1.0;9.0;16.0


            """)
    void testInterpolator(boolean throwsException, @ConvertWith(DoubleArrayConverter.class) double[] xv,
            @ConvertWith(DoubleArrayConverter.class) double[] yv) {
               final String xvString = Arrays.toString(xv);
               final String yvString = Arrays.toString(yv);
        try {
            new Interpolator(xv, yv, SMALL_DIFF, PRECISION, MAX_ITERATIONS);
            String fmt = "IllegalArgumentException expected for xv=%s and yv=%s";
            assertFalse(throwsException, String.format(fmt, xvString, yvString));
        } catch (IllegalArgumentException ex) {
            String fmt = "IllegalArgumentException not expected for xv=%s and yv=%s";
            assertTrue(throwsException, String.format(fmt, xvString, yvString));
        } 
    }

    @ParameterizedTest
    @CsvSource(useHeadersInDisplayName = true, textBlock = """
                EXPECTED,   A,      B
                false,      1.0,    2.0
                false,      1.0e-5, 2.0e-5
                true,       2.0,    2.0
                true,       1.0e-6, 1.1e-6
                true,       1.0e-6, 2.0e-6
            """)
    void testNearEqual_2args(boolean expected, double a, double b) {
        final String[] xDates = { "1992-05-12T00:00", "1992-05-16T00:00", "1992-05-20T00:00" };
        final double[] xv = JulianDayConverter.convertArray(xDates);
        final double[] yv = { 1.3814294d, 1.3812213d, 1.3812453d };
        final Interpolator obj = new Interpolator(xv, yv, SMALL_DIFF, PRECISION, MAX_ITERATIONS);
        final boolean actual = obj.nearEqual(a, b);
        assertEquals(expected, actual);
    }

    @Test
    void testInterpolate3() throws InterpolationException {
        double[] xv = {
                2448931.5d,
                2448932.5d,
                2448933.5d,
                2448934.5d,
                2448935.5d
        };
        double[] yv = {
                0.898013,
                0.891109,
                0.884226,
                0.877366,
                0.870531
        };

        Interpolator obj = new Interpolator(xv, yv, SMALL_DIFF, PRECISION, MAX_ITERATIONS);
        double x = LocalDateTime.parse("1992-11-08T04:21:00").query(Queries.JULIAN_DAY);
        double y = obj.interpolate3(x, 3);
        assertEquals(0.876125, y, 1e-6d);
    }

    @Test
    void testInterpolate5() {

    }

    @Test
    void testIsSmallDiff() {

    }

    @Test
    void testNearEqual() {

    }

    @ParameterizedTest
    @CsvSource(useHeadersInDisplayName = true, textBlock = """
                EXPECTED,   THROWS, INPUT
                0.876125,   false,  1992-11-08T04:21:00
                0.0,        true,   1992-11-04T04:00:00
                0.0,        true,   1992-11-10T04:00:00
            """)
    void testY(double expected, boolean throwsException, @ConvertWith(JulianDayConverter.class) Double input) {
        final String[] xDates = { "1992-11-05T00:00:00", "1992-11-06T00:00:00", "1992-11-07T00:00:00",
                "1992-11-08T00:00:00", "1992-11-09T00:00:00" };
        final double[] xv = JulianDayConverter.convertArray(xDates);
        final double[] yv = { 0.898013d, 0.891109d, 0.884226d, 0.877366d, 0.870531d };
        Interpolator obj = new Interpolator(xv, yv, SMALL_DIFF, PRECISION, MAX_ITERATIONS);
        try {
            double actual = obj.y(input);
            assertFalse(throwsException, "InterpolationException expected");
            assertEquals(expected, actual, PRECISION);
        } catch (InterpolationException ex) {
            assertTrue(throwsException, "InterpolationException not expected for value " + ex.getXValue());
        }
    }

    @Test
    void testYMaxIndexNearExactOrFail() {

    }

    @Test
    void testYMinIndexNearExactOrFail() {

    }
}
