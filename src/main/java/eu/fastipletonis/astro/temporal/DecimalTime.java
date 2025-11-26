/*
 *   Decimal Time routines.
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
package eu.fastipletonis.astro.temporal;

import static java.time.temporal.ChronoField.NANO_OF_DAY;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAccessor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Conversion routines for the decimal time.
 * <p>
 * In astronomical aplications it is usual to use a decimal time with range
 * [0, 1). This implementation uses the "civilan" time, with 0 set at midnight
 * instead of noon. To get the astronomical time of the day, just add 0.5.
 * <p>
 * Please note that these routines ignore the timezone.
 */
public class DecimalTime {
    // Constants for conversions and calculations.
    private static final long NANOS_PER_DAY_L = 86_400_000_000_000L;
    private static final double NANOS_PER_DAY_D = (double) NANOS_PER_DAY_L;
    // Regular expression for checking input
    private static final Pattern FMT_DECIMAL_DATETIME = Pattern
            .compile("([+\\-]?[0-9]{1,4})-([01]?[0-9])-([0-3]?[0-9])[.,]([0-9]+)");

    // Private constructor to prevent instantiation.
    private DecimalTime() {
    }

    /**
     * Returns a decimal time from a temporal accessor object as a
     * double-precision value.
     * <p>
     * The temporal accessor must support the field
     * {@link java.time.temporal.ChronoField#NANO_OF_DAY}
     * 
     * @param temporal temporal accessor to use
     * 
     * @return a double-precision value representing the decimal time.
     */
    public static double from(TemporalAccessor temporal) {
        final double nanos = (double) temporal.getLong(NANO_OF_DAY);
        return nanos / NANOS_PER_DAY_D;
    }

    /**
     * Converts the given decimal time to a LocalTime object.
     * 
     * @param dt decimal time
     * 
     * @return the equivalent LocalTime.
     */
    public static LocalTime toLocalTime(double dt) {
        final double nanoOfDay = dt * NANOS_PER_DAY_D;
        return LocalTime.ofNanoOfDay((long) nanoOfDay);
    }

    /**
     * Parses a string in the following format into a LocalDateTime object:
     * <code>1957-10-04.81</code>
     * <p>
     * This is useful as some texts give date-time references in this format.
     * The above string will be then translated to a LocalDateTime with the
     * content: <code>1957-10-04T19:26:24</code>.
     * <p>
     * It is possible to use negative years. Please note that the
     * floating-point part is mandatory, so for example to indicate the
     * midnight of November 1<sup>st</sup>, 2000, the correct format would
     * be: <code>2000-11-01.0</code>, or even <code>2000-11-1.0</code>.
     * <p>
     * Both "." and "," are accepted as separators.
     * 
     * @param text the string to parse.
     * 
     * @return a LocalDateTime object representing the given time
     */
    public static LocalDateTime parse(CharSequence text) {
        final Matcher m = FMT_DECIMAL_DATETIME.matcher(text);
        if (!m.find()) {
            throw new DateTimeParseException(
                    "Cannot parse text: " + text, text, 0);
        }
        try {
            final int year = Integer.parseInt(m.group(1));
            final int month = Integer.parseInt(m.group(2));
            final int day = Integer.parseInt(m.group(3));
            final double time = Double.parseDouble("0." + m.group(4));
            final LocalDate localDate = LocalDate.of(year, month, day);
            final LocalTime localTime = toLocalTime(time);
            return LocalDateTime.of(localDate, localTime);
        } catch (NumberFormatException ex) {
            throw new DateTimeParseException(
                    "Cannot parse text: " + text, text, 0, ex);
        }
    }
}
