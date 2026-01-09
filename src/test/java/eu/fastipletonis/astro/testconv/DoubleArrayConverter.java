package eu.fastipletonis.astro.testconv;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.params.converter.ArgumentConversionException;
import org.junit.jupiter.params.converter.SimpleArgumentConverter;

/**
 * Converter for arrays of double-precision arguments. The values must be
 * separated with <code>;</code>.
 */
public class DoubleArrayConverter extends SimpleArgumentConverter {

    @Override
    protected Object convert(Object source, Class<?> targetType) throws ArgumentConversionException {
        if (source instanceof String && double[].class.isAssignableFrom(targetType)) {
            String rawString = (String) source;
            List<String> rawArgs = Arrays.asList(rawString.split(";"));
            return rawArgs.stream().mapToDouble(s -> Double.parseDouble(s)).toArray();
        } else {
            String fmt = "Conversion from %s to %s not supported";
            String msg = String.format(fmt, source.getClass().toString(), targetType.toString());
            throw new IllegalArgumentException(msg);
        }
    }
}
