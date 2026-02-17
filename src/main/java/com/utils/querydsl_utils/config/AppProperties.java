package com.utils.querydsl_utils.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "spring.mvc")
public class AppProperties {

    private Format format = new Format();

    @Data
    public static class Format {
        private String time = "HH:mm:ss";
        private String date = "dd/MM/yyyy";
        private String dateTime = "dd/MM/yyyy HH:mm:ss";
    }
}
