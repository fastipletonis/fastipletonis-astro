/*
 *   Test suite for JulianDayHelper.
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
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.math.BigDecimal;
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

public class JulianDayHelperTest {
    static final Collection<Arguments> isSupportedArgs = Arrays.asList(
        arguments(true, LocalDateTime.now()),
        arguments(true, ZonedDateTime.now()),
        arguments(false, LocalDate.now()),
        arguments(false, LocalTime.now()),
        arguments(false, Instant.now())
    );

    @ParameterizedTest
    @CsvSource(useHeadersInDisplayName = true, textBlock ="""
        EXPECTED,       INPUT
        2436116.31,     1957-10-04.81 
        1842713.0,      0333-01-27.5
        2451545.0,      2000-01-01.5
        2451179.5,      1999-01-01.0
        2446822.5,      1987-01-27.0
        2305812.5,      1600-12-31.0
        2026871.8,       837-04-10.3
        1676496.5,      -123-12-31.0
              0.0,     -4712-01-01.5   
    """)
    void testGetBigDecimalFrom(String e, String dateTime) {
        final BigDecimal expected = new BigDecimal(e);
        final int expScale = expected.scale();
        final LocalDateTime input = DecimalTime.parseDateTime(dateTime);
        final BigDecimal actual = JulianDayHelper.getBigDecimalFrom(input);
        assertEquals(expected, actual.setScale(expScale));
    }

    @ParameterizedTest
    @CsvSource(useHeadersInDisplayName = true, textBlock ="""
        EXPECTED,       INPUT
        2436116.31,     1957-10-04.81 
        1842713.0,      0333-01-27.5
        2451545.0,      2000-01-01.5
        2451179.5,      1999-01-01.0
        2446822.5,      1987-01-27.0
        2305812.5,      1600-12-31.0
        2026871.8,       837-04-10.3
        1676496.5,      -123-12-31.0
              0.0,     -4712-01-01.5   
    """)
    void testGetDoubleFrom(double expected, String dateTime) {
        final LocalDateTime input = DecimalTime.parseDateTime(dateTime);
        final double actual = JulianDayHelper.getDoubleFrom(input);
        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @CsvSource(useHeadersInDisplayName = true, textBlock ="""
        EXPECTED,               INPUT
         1957-10-04T19:26:24,   2436116.31
         0333-01-27T12:00:00,   1842713.0
        -0123-12-31T00:00:00,   1676496.5
    """)
    void testGetLocalDateTimeFrom_double(String e, double input) {
        final LocalDateTime expected = LocalDateTime.parse(e);
        final LocalDateTime actual = JulianDayHelper.getLocalDateTimeFrom(input);
        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @CsvSource(useHeadersInDisplayName = true, textBlock ="""
        EXPECTED,               INPUT
         1957-10-04T19:26:24,   2436116.31
         0333-01-27T12:00:00,   1842713.0
        -0123-12-31T00:00:00,   1676496.5
    """)
    void testGetLocalDateTimeFrom_BigDecimal(String e, String i) {
        final LocalDateTime expected = LocalDateTime.parse(e);
        final BigDecimal input = new BigDecimal(i);
        final LocalDateTime actual = JulianDayHelper.getLocalDateTimeFrom(input);
        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @FieldSource("isSupportedArgs")
    void testIsSupported(boolean expected, TemporalAccessor input) {
        final boolean actual = JulianDayHelper.isSupported(input);
        assertEquals(expected, actual);
    }
}
