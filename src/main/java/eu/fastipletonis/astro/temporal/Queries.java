/*
 *   Astronomical temporal queries.
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

import java.time.temporal.TemporalQuery;

/**
 * Temporal queries used for retrieving time.
 */
public class Queries {
    // Private constructor to prevent instantiation
    private Queries() {
    }

    /**
     * Query for returning the Astronomical Julian Day as a double. Returns
     * null if the temporal object does not support Julian Days.
     * <p>
     * More information about the fields that must be supportd by the
     * TemporalAccessor is available in the documenttion for
     * {@link JulianDay#isSupported(java.time.temporal.TemporalAccessor)}.
     */
    public static final TemporalQuery<Double> JULIAN_DAY = (temporal) -> {
        if (JulianDay.isSupported(temporal)) {
            return JulianDay.from(temporal);
        } else
            return null;
    };

    /**
     * Query for returning the decimal time as a double. It returns
     * <code>null</code> if the temporal object does not support the field
     * {@link java.time.temporal.ChronoField#NANO_OF_DAY}.
     */
    public static final TemporalQuery<Double> DECIMAL_TIME = (temporal) -> {
        if (temporal.isSupported(NANO_OF_DAY)) {
            return DecimalTime.from(temporal);
        } else
            return null;
    };

    /**
     * Query for returning the right ascension in degrees from a temporal
     * object. It returns <code>null</code> if the temporal object does not
     * support the required fields.
     */
    public static final TemporalQuery<Double> RIGHT_ASCENSION = (temporal) -> {
        if (RightAscension.isSupported(temporal)) {
            return RightAscension.from(temporal);
        } else
            return null;
    };
}
