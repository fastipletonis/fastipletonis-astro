package eu.fastipletonis.astro.testconv;

import java.time.LocalDateTime;

import org.junit.jupiter.params.converter.ArgumentConversionException;
import org.junit.jupiter.params.converter.SimpleArgumentConverter;

import eu.fastipletonis.astro.temporal.Queries;

/**
 * Converter for arrays of double-precision arguments. The values must be
 * separated with <code>;</code>.
 */
public class JulianDayConverter extends SimpleArgumentConverter {

    @Override
    protected Object convert(Object source, Class<?> targetType) throws ArgumentConversionException {
        if (source instanceof String && Double.class.isAssignableFrom(targetType)) {
            return LocalDateTime.parse((String) source).query(Queries.JULIAN_DAY);
        } else {
            String fmt = "Conversion from %s to %s not supported";
            String msg = String.format(fmt, source.getClass().toString(), targetType.toString());
            throw new IllegalArgumentException(msg);
        }
    }

    /**
     * Transforms an array of ISO datetime strings in an array of Julian days.
     * 
     * @param dates array of dates in ISO format.
     *
     * @return an array of double precision values representing julian dates
     */
    public static double[] convertArray(String[] dates) {
        double[] jds = new double[dates.length];
        for (int i = 0; i < jds.length; i++) {
            jds[i] = LocalDateTime.parse(dates[i]).query(Queries.JULIAN_DAY);
        }
        return jds;
    }
}
