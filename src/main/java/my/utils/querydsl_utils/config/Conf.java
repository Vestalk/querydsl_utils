package my.utils.querydsl_utils.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import my.utils.querydsl_utils.servise.AbstractSelectService;
import my.utils.querydsl_utils.servise.CommonFieldService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class Conf {

    @Bean
    @ConditionalOnMissingBean(JPAQueryFactory.class)
    public JPAQueryFactory jpaQueryFactory(EntityManager em) {
        return new JPAQueryFactory(em);
    }

    @Bean
    @ConditionalOnMissingBean(CommonFieldService.class)
    public CommonFieldService commonService(List<AbstractSelectService> entitySelectServices) {
        return new CommonFieldService(entitySelectServices);
    }

}
