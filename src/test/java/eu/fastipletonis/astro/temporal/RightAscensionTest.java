/*
 *   Test suite for right ascension routines.
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAccessor;
import java.util.Arrays;
import java.util.Collection;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.FieldSource;

/// Test suite for RightAscension class.
public class RightAscensionTest {
    /// Precision for testing double
    private static final double PRECISION = 1.0e-5d;
    /// Test arguments
    static final Collection<Arguments> isSupportedArgs = Arrays.asList(
            arguments(true, LocalDateTime.now()),
            arguments(true, ZonedDateTime.now()),
            arguments(false, LocalDate.now()),
            arguments(true, LocalTime.now()),
            arguments(false, Instant.now()));

    /// Empty constructor.
    RightAscensionTest() {
    }

    @ParameterizedTest
    @CsvSource(useHeadersInDisplayName = true, textBlock = """
                EXPECTED,   INPUT
                0.0,        00:00:00
                138.7325,   09:14:55.8
                180.0,      12:00:00
            """)
    void testFrom(double expected, String time) {
        final LocalTime input = LocalTime.parse(time);
        final double actual = RightAscension.from(input);
        assertEquals(expected, actual, PRECISION);
    }

    @ParameterizedTest
    @CsvSource(useHeadersInDisplayName = true, textBlock = """
                EXPECTED,     INPUT
                00:00:00,     0.0
                09:14:55.800, 138.732500000001
                12:00:00,     180.0
            """)
    void testToLocalTime(String time, double input) {
        final LocalTime expected = LocalTime.parse(time);
        final LocalTime actual = RightAscension.toLocalTime(input);
        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @FieldSource("isSupportedArgs")
    void testIsSupported(boolean expected, TemporalAccessor input) {
        final boolean actual = RightAscension.isSupported(input);
        assertEquals(expected, actual);
    }
}
