package com.bhhan.dynamo.config;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverter;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by hbh5274@gmail.com on 2020-08-06
 * Github : http://github.com/bhhan5274
 */
public class DynamoDbConfig {
    public static class LocalDateTimeConverter implements DynamoDBTypeConverter<Date, LocalDateTime> {

        @Override
        public Date convert(LocalDateTime source) {
            return Date.from(source.toInstant(ZoneOffset.UTC));
        }

        @Override
        public LocalDateTime unconvert(Date source) {
            return source.toInstant().atZone(TimeZone.getDefault().toZoneId()).toLocalDateTime();
        }
    }
}
