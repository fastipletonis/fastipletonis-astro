/*
 *   Test suite for Queries.
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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalQuery;
import java.util.Arrays;
import java.util.Collection;

import org.junit.jupiter.api.Named;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.FieldSource;

/// Test suite for Queries.
public class QueriesTest {
    /// Test arguments for queries.
    static final Collection<Arguments> queriesArgs = Arrays.asList(
            arguments(
                    Named.of("Queries.JULIAN_DAY", Queries.JULIAN_DAY),
                    Double.valueOf(2436116.31d),
                    LocalDateTime.of(1957, 10, 4, 19, 26, 24)),
            arguments(
                    Named.of("Queries.JULIAN_DAY", Queries.JULIAN_DAY),
                    null,
                    LocalTime.of(19, 26, 24)),
            arguments(
                    Named.of("Queries.DECIMAL_TIME", Queries.DECIMAL_TIME),
                    Double.valueOf(0.81d),
                    LocalTime.of(19, 26, 24)),
            arguments(
                    Named.of("Queries.DECIMAL_TIME", Queries.DECIMAL_TIME),
                    null,
                    LocalDate.of(1957, 10, 4)),
            arguments(
                    Named.of("Queries.RIGHT_ASCENSION", Queries.RIGHT_ASCENSION),
                    Double.valueOf(138.7325d),
                    LocalTime.of(9, 14, 55, 800_000_000)),
            arguments(
                    Named.of("Queries.RIGHT_ASCENSION", Queries.RIGHT_ASCENSION),
                    null,
                    LocalDate.of(1957, 10, 4)));

    /// Constructor.
    QueriesTest() {
    }

    @ParameterizedTest
    @FieldSource("queriesArgs")
    void testQueries(TemporalQuery<Double> query, Double expected, TemporalAccessor input) {
        final Double actual = input.query(query);
        if (expected == null)
            assertNull(actual);
        else {
            assertNotNull(actual);
            assertEquals(expected, actual, 0.0001);
        }
    }
}
