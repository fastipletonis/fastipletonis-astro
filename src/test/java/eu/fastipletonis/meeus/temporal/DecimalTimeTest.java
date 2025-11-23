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
package eu.fastipletonis.meeus.temporal;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class DecimalTimeTest {
    @ParameterizedTest
    @CsvSource(useHeadersInDisplayName = true, textBlock ="""
        EXPECTED,   INPUT
        0.0,        00:00:00    
        0.5,        12:00:00
        0.81,       19:26:24
    """)
    void testAsBigDecimal(String decimalTime, String time) {
        final BigDecimal expected = new BigDecimal(decimalTime);
        final int expScale = expected.scale();
        final LocalTime input = LocalTime.parse(time);
        final BigDecimal actual = DecimalTime.asBigDecimal(input);
        assertEquals(expected, actual.setScale(expScale));
    }
    @ParameterizedTest
    @CsvSource(useHeadersInDisplayName = true, textBlock ="""
        EXPECTED,   INPUT    
        0.0,        00:00:00    
        0.5,        12:00:00
        0.81,       19:26:24
    """)
    void testAsDouble(double expected, String time) {
        final LocalTime input = LocalTime.parse(time);
        final double actual = DecimalTime.asDouble(input);
        assertEquals(expected, actual, 0.0001);
    }

    @ParameterizedTest
    @CsvSource(useHeadersInDisplayName = true, textBlock ="""
        EXPECTED,   INPUT
        00:00:00,   0.0
        12:00:00,   0.5
        19:26:24,   0.81
    """)
    void testToLocalTime_double(String time, double input) {
        final LocalTime expected = LocalTime.parse(time);
        final LocalTime actual = DecimalTime.toLocalTime(input);
        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @CsvSource(useHeadersInDisplayName = true, textBlock ="""
        EXPECTED,   INPUT
        00:00:00,   0.0
        19:26:24,   0.81
        12:00:00,   0.5
    """)
    void testToLocalTime_BigDecimal(String time, String decimalTime) {
        final LocalTime expected = LocalTime.parse(time);
        final BigDecimal input = new BigDecimal(decimalTime);
        final LocalTime actual = DecimalTime.toLocalTime(input);
        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @CsvSource(useHeadersInDisplayName = true, textBlock ="""
        EXPECTED,               INPUT
         1957-10-04T19:26:24,   1957-10-04.81
         0333-01-27T12:00:00,   333-1-27.5
        -0123-12-31T00:00:00,   '-123-12-31,0'
    """)
    void testParseDateTime(String e, String input) {
        final LocalDateTime expected = LocalDateTime.parse(e);
        final LocalDateTime actual = DecimalTime.parseDateTime(input);
        assertEquals(expected, actual);
    }
}
