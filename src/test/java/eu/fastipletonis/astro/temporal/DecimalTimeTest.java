/*
 *   Test suite for DecimalTime.
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

import java.time.LocalDateTime;
import java.time.LocalTime;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/// Test suite for DecimalTime.
public class DecimalTimeTest {

    /// Constructor.
    DecimalTimeTest() {
    }

    @ParameterizedTest
    @CsvSource(useHeadersInDisplayName = true, textBlock = """
                EXPECTED,   INPUT
                0.0,        00:00:00
                0.5,        12:00:00
                0.81,       19:26:24
            """)
    void testFrom(double expected, String time) {
        final LocalTime input = LocalTime.parse(time);
        final double actual = DecimalTime.from(input);
        assertEquals(expected, actual, 0.0001);
    }

    @ParameterizedTest
    @CsvSource(useHeadersInDisplayName = true, textBlock = """
                EXPECTED,   INPUT
                00:00:00,   0.0
                12:00:00,   0.5
                19:26:24,   0.81
            """)
    void testToLocalTime(String time, double input) {
        final LocalTime expected = LocalTime.parse(time);
        final LocalTime actual = DecimalTime.toLocalTime(input);
        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @CsvSource(useHeadersInDisplayName = true, textBlock = """
                EXPECTED,               INPUT
                 1957-10-04T19:26:24,   1957-10-04.81
                 0333-01-27T12:00:00,   333-1-27.5
                -0123-12-31T00:00:00,   '-123-12-31,0'
            """)
    void testParse(String e, String input) {
        final LocalDateTime expected = LocalDateTime.parse(e);
        final LocalDateTime actual = DecimalTime.parse(input);
        assertEquals(expected, actual);
    }
}
