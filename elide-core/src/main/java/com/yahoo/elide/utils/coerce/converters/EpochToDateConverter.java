/*
 * Copyright 2016, Yahoo Inc.
 * Licensed under the Apache License, Version 2.0
 * See LICENSE file in project root for terms.
 */
package com.yahoo.elide.utils.coerce.converters;

import com.yahoo.elide.core.exceptions.InvalidAttributeException;

import org.apache.commons.beanutils.Converter;
import org.apache.commons.lang3.ClassUtils;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;

/**
 * Convert epoch(in string or long) to Date
 */
public class EpochToDateConverter implements Converter {

    @Override
    public <T> T convert(Class<T> cls, Object value) {
        try {
            if (ClassUtils.isAssignable(value.getClass(), String.class)) {
                return stringToDate(cls, (String) value);
            } else if (ClassUtils.isAssignable(value.getClass(), Number.class, true)) {
                return numberToDate(cls, (Number) value);
            } else {
                throw new UnsupportedOperationException(value.getClass().getSimpleName() + " is not a valid epoch");
            }
        } catch (IndexOutOfBoundsException | ReflectiveOperationException
                | UnsupportedOperationException | IllegalArgumentException e) {
            throw new InvalidAttributeException("Unknown " + cls.getSimpleName() + " value " + value, e);
        }
    }

    private static <T> T numberToDate(Class<T> cls, Number epoch) throws ReflectiveOperationException {
        if (ClassUtils.isAssignable(cls, java.sql.Date.class)) {
            return (T) new java.sql.Date(epoch.longValue());
        } else if (ClassUtils.isAssignable(cls, Timestamp.class)) {
            return (T) new Timestamp(epoch.longValue());
        } else if (ClassUtils.isAssignable(cls, Time.class)) {
            return (T) new Time(epoch.longValue());
        } else if (ClassUtils.isAssignable(cls, Date.class)) {
            return (T) new Date(epoch.longValue());
        } else {
            throw new UnsupportedOperationException("Cannot convert to " + cls.getSimpleName());
        }
    }

    private static <T> T stringToDate(Class<T> cls, String epoch) throws ReflectiveOperationException {
        return numberToDate(cls, Long.parseLong(epoch));
    }
}
