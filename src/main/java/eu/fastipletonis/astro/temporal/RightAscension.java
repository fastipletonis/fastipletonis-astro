/*
 *   Routines for right time ascension.
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

import static java.time.temporal.ChronoField.HOUR_OF_DAY;
import static java.time.temporal.ChronoField.MINUTE_OF_HOUR;
import static java.time.temporal.ChronoField.NANO_OF_SECOND;
import static java.time.temporal.ChronoField.SECOND_OF_MINUTE;

import java.time.LocalTime;
import java.time.temporal.TemporalAccessor;

/**
 * Routines for converting right ascension from time to angles and vice-versa.
 */
public class RightAscension {
    // Conversion factor between decimal hours and nanoseconds
    private static final double D_HOUR_CONV = 24.0e10d;

    // Private constructor.
    private RightAscension() {
    }

    /**
     * Checks if the temporal accessor supports the required fields.
     * <p>
     * The conversion to angle requires the following fields:
     * <ul>
     * <li>{@link java.time.temporal.ChronoField#HOUR_OF_DAY}
     * <li>{@link java.time.temporal.ChronoField#MINUTE_OF_HOUR}
     * <li>{@link java.time.temporal.ChronoField#SECOND_OF_MINUTE}
     * </ul>
     * <p>
     * The conversion routine can use other fields as well, nut they are not
     * strictly required and therefore they are not checked by this method.
     * 
     * @param temporal the temporal accessor to be checked
     * 
     * @return <code>true</code> if the temporal accessor supports the required
     *         fields, <code>false</code> otherwise
     */
    public static boolean isSupported(TemporalAccessor temporal) {
        return temporal.isSupported(HOUR_OF_DAY)
                && temporal.isSupported(MINUTE_OF_HOUR)
                && temporal.isSupported(SECOND_OF_MINUTE);
    }

    /**
     * Returns the nano of seconds if supported by the temporal accessor.
     * <p>
     * This method checks only for
     * {@link java.time.temporal.ChronoField#NANO_OF_SECOND} because all the
     * other related fields, such as
     * {@link java.time.temporal.ChronoField#MILLI_OF_SECOND} and
     * {@link java.time.temporal.ChronoField#MICRO_OF_SECOND}, should already
     * be covered according to
     * {@link java.time.temporal.ChronoField#NANO_OF_SECOND} documentation.
     * 
     * @param temporal temporal accessor to get the field from
     * 
     * @return the fractional part of the second expressed in nanoseconds or
     *         <code>0</code> if not available
     */
    private static long getNanos(TemporalAccessor temporal) {
        if (temporal.isSupported(NANO_OF_SECOND))
            return temporal.getLong(NANO_OF_SECOND);
        else
            return 0;
    }

    /**
     * Converts a right ascension time to an angle expressed in degrees.
     * 
     * @param temporal temporal accessor to get the angle from
     * 
     * @return the angle expressed as a double in degrees
     */
    public static double from(TemporalAccessor temporal) {
        final double hh = (double) temporal.get(HOUR_OF_DAY);
        final double mm = (double) temporal.get(MINUTE_OF_HOUR);
        final double nanos = (double) (getNanos(temporal) * 1.0e-9d);
        final double ss = (double) temporal.get(SECOND_OF_MINUTE) + nanos;
        final double hours = hh + mm / 60.0d + ss / 3600d;
        return hours * 15.0d;
    }

    /**
     * Converts a right ascension expressed in degrees to a time.
     * 
     * @param angle the angle expressed in degrees
     * 
     * @return a local time representing the right ascension
     */
    public static LocalTime toLocalTime(double angle) {
        final long nanos = (long) (angle * D_HOUR_CONV);
        return LocalTime.ofNanoOfDay(nanos);
    }
}
