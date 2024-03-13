package ch.fhnw.swc.mrs.util;

import org.sql2o.converters.Converter;
import org.sql2o.converters.ConverterException;

import ch.fhnw.swc.mrs.model.PriceCategory;

/**
 * Utility class to convert from a string or price category object to an price category object.
 */
public class PriceCategoryConverter implements Converter<PriceCategory> {

    @Override
    public PriceCategory convert(Object val) throws ConverterException {
        if (val == null) {
            return null;
        }

        if (val instanceof String) {
            return PriceCategory.getPriceCategoryFromId(val.toString());
        }
        try {
            return (PriceCategory) val;
        } catch (ClassCastException ex) {
            throw new ConverterException("Don't know how to convert from type '" + val.getClass().getName()
                    + "' to type '" + PriceCategory.class.getName() + "'", ex);
        }

    }

    @Override
    public Object toDatabaseParam(PriceCategory val) {
        return val.toString();
    }

}
