package eu.fastipletonis.astro.math;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import eu.fastipletonis.astro.temporal.Queries;

/**
 * Test suite for Interpolator.
 */
public class InterpolatorTest {
    protected final static double SMALL_DIFF = 1.0e-3d;
    protected final static double PRECISION = 1.0e-6d;

    /**
     * Transforms an array of ISO datetime strings in an array of Julian days.
     * 
     * @param dates
     * @return
     */
    protected static final double[] getJulianDates(String[] dates) {
        double[] jds = new double[dates.length];
        for (int i = 0; i < jds.length; i++) {
            jds[i] = LocalDateTime.parse(dates[i]).query(Queries.JULIAN_DAY);
        }
        return jds;
    }

    /**
     * Methods for passing test arguments to TestY().
     * 
     * @return the arguments for TestY
     */
    protected static final Collection<Arguments> getArgsTestY() {
        final double[] yv = {
                0.898013d,
                0.891109d,
                0.884226d,
                0.877366d,
                0.870531d
        };
        final String[] xDates = {
                "1992-11-05T00:00:00",
                "1992-11-06T00:00:00",
                "1992-11-07T00:00:00",
                "1992-11-08T00:00:00",
                "1992-11-09T00:00:00",
        };
        final double[] xv = getJulianDates(xDates);
        return Arrays.asList(
                arguments(
                        xv, yv,
                        LocalDateTime
                                .parse("1992-11-08T04:21:00")
                                .query(Queries.JULIAN_DAY),
                        0.876125d, false),
                arguments(
                        xv, yv,
                        LocalDateTime
                                .parse("1992-11-04T04:00:00")
                                .query(Queries.JULIAN_DAY),
                        0.0d, true),
                arguments(
                        xv, yv,
                        LocalDateTime
                                .parse("1992-11-10T04:00:00")
                                .query(Queries.JULIAN_DAY),
                        0.0d, true));
    }

    @Test
    void testDiff() {

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

        Interpolator obj = new Interpolator(xv, yv, 10.0d, 1e-6d);
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

    @ParameterizedTest(name = "[{index}] xv[], xy[], {2}, {3}, {4}")
    @MethodSource("getArgsTestY")
    void testY(double[] xv, double[] yv, double input, double expected, boolean throwsException) {
        Interpolator obj = new Interpolator(xv, yv, SMALL_DIFF, PRECISION);
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
