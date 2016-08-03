package com.geekhub.entities.utils;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import java.time.*;
import java.sql.Date;

@Converter(autoApply = true)
public class LocalDateTimeConverter implements AttributeConverter<LocalDateTime, Date> {

    @Override
    public Date convertToDatabaseColumn(LocalDateTime localTime) {
        return localTime == null ? null : new Date(localTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
    }

    @Override
    public LocalDateTime convertToEntityAttribute(Date date) {
        if (date == null) {
            return null;
        }
        Instant instant = Instant.ofEpochMilli(date.getTime());
        LocalTime localTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).toLocalTime();
        LocalDate localDate = date.toLocalDate();
        return LocalDateTime.of(localDate, localTime);
    }
}
