package ch.fhnw.swc.mrs.util;

import java.sql.Date;
import java.time.LocalDate;

import org.sql2o.converters.Converter;
import org.sql2o.converters.ConverterException;

/**
 * Provides utility method to convert from a Date object to a LocalDate object.
 */
public class LocalDateConverter implements Converter<LocalDate> {

    @Override
    public LocalDate convert(Object val) throws ConverterException {
        if (val == null) {
            return null;
        }

        if (val instanceof Date) {
            Date date = (Date) val;
            return date.toLocalDate();
        }
        try {
            return (LocalDate) val;
        } catch (ClassCastException ex) {
            throw new ConverterException("Don't know how to convert from type '" + val.getClass().getName()
                    + "' to type '" + LocalDate.class.getName() + "'", ex);
        }

    }

    @Override
    public Object toDatabaseParam(LocalDate val) {
        return Date.valueOf(val);
    }

}
