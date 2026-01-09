/*
 *   Interpolation exception.
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
package eu.fastipletonis.astro.math;

/**
 * Interpolation exception. It is thrown when it is not possible to perform
 * an interpolation or return a value.
 */
public class InterpolationException extends Exception {
    /// the _x_ value.
    private final double xValue;

    /**
     * Builds a new InterpolationException instance.
     * 
     * @param msg exception message or description
     * @param xValue the <i>x</i> invalid value 
     */
    public InterpolationException(String msg, double xValue) {
        super(msg);
        this.xValue = xValue;
    }

    /**
     * @return the <i>x</i> value
     */
    public double getXValue() {
        return xValue;
    }
}
