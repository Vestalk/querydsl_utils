package my.utils.querydsl_utils.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "querydsl-utils")
public class AppProperties {

    private Patterns patterns = new Patterns();

    @Data
    public static class Patterns {
        private String timePattern = "HH:mm:ss";
        private String datePattern = "yyyy-MM-dd";
        private String dateTimePattern = "yyyy-MM-dd HH:mm:ss";
    }
}
