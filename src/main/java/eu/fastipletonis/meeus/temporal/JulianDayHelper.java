/*
 *   Helper for calculating the astronomical Julian day.
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
package eu.fastipletonis.meeus.temporal;

import static java.time.temporal.ChronoField.DAY_OF_MONTH;
import static java.time.temporal.ChronoField.MONTH_OF_YEAR;
import static java.time.temporal.ChronoField.NANO_OF_DAY;
import static java.time.temporal.ChronoField.YEAR;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.temporal.JulianFields;
import java.time.temporal.TemporalAccessor;

/**
 * Helper for calculating the astronomical Julian day.
 * <p>
 * The standard Java implementation for {@link JulianFields#JULIAN_DAY} only
 * implements long results. Astronomical calculations often require a Julian
 * Day able to handle the time of the day as well.
 * <p>
 * Please notice that this implementation completely ignores the time zone.
 * The user should ensure the use of the appropriate time frame.
 * <p>
 * The Julian day is calculated according to Meeus' algorithm explained in
 * Chapter 7.
 */
public class JulianDayHelper {
    // Math context for the required calculations.
    private static final MathContext MC = new MathContext(20, RoundingMode.DOWN);
    // Constants related to the Julian calendar cut-off.
    private static final int CUTOFF_YEAR = 1582;
    private static final int CUTOFF_MONTH = 10;
    private static final int CUTOFF_DAY = 15;
    // Constants converted to big decimals.
    private static final BigDecimal C0_5 = new BigDecimal("0.5");
    private static final BigDecimal C1 = BigDecimal.ONE;
    private static final BigDecimal C2 = new BigDecimal(2);
    private static final BigDecimal C4 = new BigDecimal(4);
    private static final BigDecimal C30_6001 = new BigDecimal("30.6001");
    private static final BigDecimal C100 = new BigDecimal(100);
    private static final BigDecimal C122_1 = new BigDecimal("122.1");
    private static final BigDecimal C365_25 = new BigDecimal("365.25");
    private static final BigDecimal C1524 = new BigDecimal(1524);
    private static final BigDecimal C1524_5 = new BigDecimal("1524.5");
    private static final BigDecimal C4716 = new BigDecimal(4716);
    private static final BigDecimal C36524_25 = new BigDecimal("36524.25");
    private static final BigDecimal C1867216_25 = new BigDecimal("1867216.25");
    private static final BigDecimal C2299161 = new BigDecimal(2299161);
    // Format for string conversion in DecimalTime.
    private static final String FMT_DEC_DATETIME = "%d-%d-%f";

    /**
     * We cannot rely on the presence of isBefore(), so we use the required
     * fields to check if the date is Julian.
     * 
     * @param year year to be checked
     * @param month month to be checked
     * @param day day to be checked
     * 
     * @return true if the date should be treated as Julian-based.
     */
    private static boolean isJulian(int year, int month, int day) {
        if (year < CUTOFF_YEAR) return true;
        else if (year > CUTOFF_YEAR) return false;
        else if (month < CUTOFF_MONTH) return true;
        else if (month > CUTOFF_MONTH) return false;
        else return day < CUTOFF_DAY;
    }

    /**
     * Checks if the given temporal accessor can be used to extract a Julian
     * day.
     * <p>
     * The following fields are checked:
     * <ul>
     *   <li>YEAR</li>
     *   <li>MONTH_OF_YEAR</li>
     *   <li>DAY_OF_MONTH</li>
     *   <li>NANO_OF_DAY</li>
     * </ul>
     * <p>
     * Both {@link java.time.LocalDateTime} and {@link java.time.ZonedDateTime}
     * satisfy these prerequisites.
     * 
     * @see java.time.temporal.TemporalField Information on specific fields.
     * 
     * @param temporal 
     */
    public static boolean isSupported(TemporalAccessor temporal) {
        return temporal.isSupported(YEAR)
                && temporal.isSupported(MONTH_OF_YEAR)
                && temporal.isSupported(DAY_OF_MONTH)
                && temporal.isSupported(NANO_OF_DAY);
    }

    /**
     * Returns the Julian day from the given temporal accessor as a BigDecimal
     * value.
     * <p>
     * The temporal accessor must support the following fields:
     * <ul>
     *   <li>YEAR</li>
     *   <li>MONTH_OF_YEAR</li>
     *   <li>DAY_OF_MONTH</li>
     *   <li>NANO_OF_DAY</li>
     * </ul>
     * <p>
     * Both {@link java.time.LocalDateTime} and {@link java.time.ZonedDateTime}
     * satisfy these prerequisites.
     * 
     * @param temporal the temporal accessor
     * @return the Julian Day as a BigDecimal value
     */
    public static BigDecimal getBigDecimalFrom(TemporalAccessor temporal) {
        final int y0 = temporal.get(YEAR);
        final int m0 = temporal.get(MONTH_OF_YEAR);
        final int d0 = temporal.get(DAY_OF_MONTH);
        final boolean isJulian = isJulian(y0, m0, d0);
        final BigDecimal y = (m0 > 2)? new BigDecimal(y0) : new BigDecimal(y0 -1);
        final BigDecimal m = (m0 > 2)? new BigDecimal(m0) : new BigDecimal(m0 + 12);
        final BigDecimal dt = DecimalTime.asBigDecimal(temporal);
        final BigDecimal d = dt.add(new BigDecimal(d0));
        final BigDecimal a = y.divideToIntegralValue(C100, MC);
        final BigDecimal b = isJulian? BigDecimal.ZERO : C2.subtract(a).add(a.divideToIntegralValue(C4, MC));
        return C365_25.multiply(y.add(C4716)).setScale(0, RoundingMode.DOWN)
                .add(C30_6001.multiply(m.add(C1)).setScale(0, RoundingMode.DOWN))
                .add(d).add(b).subtract(C1524_5);
    }

    /**
     * Returns the Julian day from the given temporal accessor as a double
     * floating-point value.
     * <p>
     * The temporal accessor must support the following fields:
     * <ul>
     *   <li>YEAR</li>
     *   <li>MONTH_OF_YEAR</li>
     *   <li>DAY_OF_MONTH</li>
     *   <li>NANO_OF_DAY</li>
     * </ul>
     * <p>
     * Both {@link java.time.LocalDateTime} and {@link java.time.ZonedDateTime}
     * satisfy these prerequisites.
     * 
     * @param temporal the temporal accessor
     * @return the Julian Day as a value of type double
     */
    public static double getDoubleFrom(TemporalAccessor temporal) {
        final int y0 = temporal.get(YEAR);
        final int m0 = temporal.get(MONTH_OF_YEAR);
        final int d0 = temporal.get(DAY_OF_MONTH);
        final boolean isJulian = isJulian(y0, m0, d0);
        final double y = (m0 > 2)? (double) y0 : (double) (y0 -1);
        final double m = (m0 > 2)? (double) m0 : (double) (m0 + 12);
        final double dt = DecimalTime.asDouble(temporal);
        final double d = dt + d0;
        final double a = Math.floor(y / 100);
        final double b = isJulian? 0.0d : 2.0d - a + Math.floor(a / 4);
        return Math.floor(365.25d * (y + 4716.0d))
                + Math.floor(30.6001d * (m + 1))
                + d + b - 1524.5d;
    }

    /**
     * Prepares the string for conversion from decimal time.
     * <p>
     * The output will be fed to {@link DecimalTime#parseDateTime()} for
     * processing, so it will be in the format <code>1957-10-04.81</code>.
     * 
     * @param e E parameter
     * @param c C parameter
     * @param dayTime decimal day-time
     * @return a string representing the date-time
     */
    private static String computeDecimalDateTime(int e, int c, double day) {
        final int month = e < 14? e - 1 : e - 13;
        final int year = month > 2? c - 4716 : c - 4715;
        return FMT_DEC_DATETIME.formatted(year, month, day);
    }

    /**
     * Converts a Julian Day into a calendar date, storing the result in a
     * {@link LocalDateTime} object.
     * <p>
     * The conversion uses the procedure described in Chapter 7 to convert
     * a Julian Day back to a calendar date.
     * 
     * @param jd the Julian Day to convert.
     * 
     * @throws DateTimeException if a negative Julian Day is passed as an
     *                           argument
     * @return a LocalDatTime object representing the Julian Day
     */
    public static LocalDateTime getLocalDateTimeFrom(double jd) {
        if (jd < 0) {
            throw new DateTimeException(
                "Cannot convert a negative julian date: " + jd
            );
        }
        final double jdc = jd + 0.5d;
        final double z = Math.floor(jdc);
        final double f = jdc - z;
        final double a;
        if (z >= 2299161) {
            final double alpha = Math.floor((z - 1867216.25d) / 36524.25d);
            a = z + 1 + alpha - Math.floor(alpha / 4);
        } else {
            a = z;
        }
        final double b = a + 1524.0d;
        final double c = Math.floor((b - 122.1d) / 365.25d);
        final double d = Math.floor(365.25d * c);
        final double e = Math.floor((b - d) / 30.6001d);
        final double day = b - d - Math.floor(30.6001 * e) + f;
        final String decTime = computeDecimalDateTime((int) e, (int) c, day);
        return DecimalTime.parseDateTime(decTime);
    }

    /**
     * Converts a Julian Day into a calendar date, storing the result in a
     * {@link LocalDateTime} object.
     * <p>
     * The conversion uses the procedure described in Chapter 7 to convert
     * a Julian Day back to a calendar date.
     * 
     * @param jd the Julian Day to convert.
     * 
     * @throws DateTimeException if a negative Julian Day is passed as an
     *                           argument
     * @return a LocalDateTime object representing the Julian Day
     */
    public static LocalDateTime getLocalDateTimeFrom(BigDecimal jd) {
        if (jd.signum() < 0) {
            throw new DateTimeException(
                "Cannot convert a negative julian date: " + jd
            );
        }
        final BigDecimal jdc = jd.add(C0_5);
        final BigDecimal z = jdc.setScale(0, RoundingMode.DOWN);
        final BigDecimal f = jdc.subtract(z);
        final BigDecimal a;
        if (z.compareTo(C2299161) >= 0) {
            final BigDecimal alpha = z.subtract(C1867216_25)
                    .divideToIntegralValue(C36524_25, MC);
            a = z.add(C1).add(alpha)
                    .subtract(alpha.divideToIntegralValue(C4, MC));
        } else {
            a = z;
        }
        final BigDecimal b = a.add(C1524);
        final BigDecimal c = b.subtract(C122_1).divideToIntegralValue(C365_25, MC);
        final BigDecimal d = C365_25.multiply(c).setScale(0, RoundingMode.DOWN);
        final BigDecimal e = b.subtract(d).divideToIntegralValue(C30_6001, MC);
        final double day = b
                .subtract(d)
                .subtract(C30_6001.multiply(e).setScale(0, RoundingMode.DOWN))
                .add(f)
                .doubleValue();
        final String decTime = computeDecimalDateTime(e.intValue(), c.intValue(), day);
        return DecimalTime.parseDateTime(decTime);
    }
}
