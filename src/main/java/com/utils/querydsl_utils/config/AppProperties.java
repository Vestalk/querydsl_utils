package com.utils.querydsl_utils.config;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

@Data
@Component
@ConfigurationProperties(prefix = "spring.mvc")
public class AppProperties {

    private Format format = new Format();

    @PostConstruct
    public void init() {
        format.timeFormatter = DateTimeFormatter.ofPattern(format.time);
        format.dateFormatter = DateTimeFormatter.ofPattern(format.date);
        format.dateTimeFormatter = DateTimeFormatter.ofPattern(format.dateTime);
    }

    @Data
    public static class Format {
        private String time = "HH:mm:ss";
        private String date = "dd/MM/yyyy";
        private String dateTime = "dd/MM/yyyy HH:mm:ss";

        private DateTimeFormatter timeFormatter;
        private DateTimeFormatter dateFormatter;
        private DateTimeFormatter dateTimeFormatter;
    }
}
