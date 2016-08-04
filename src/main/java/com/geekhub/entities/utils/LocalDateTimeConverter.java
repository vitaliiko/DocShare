package com.geekhub.entities.utils;

import com.geekhub.utils.DateTimeUtils;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import java.time.LocalDateTime;
import java.util.Date;

@Converter(autoApply = true)
public class LocalDateTimeConverter implements AttributeConverter<LocalDateTime, Date> {

    @Override
    public Date convertToDatabaseColumn(LocalDateTime dateTime) {
        return DateTimeUtils.convertLocalDateTime(dateTime);
    }

    @Override
    public LocalDateTime convertToEntityAttribute(Date date) {
        return DateTimeUtils.convertDate(date);
    }
}
