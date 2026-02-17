package com.utils.querydsl_utils.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import com.utils.querydsl_utils.servise.AbstractEntitySelectService;
import com.utils.querydsl_utils.servise.AbstractProjectionSelectService;
import com.utils.querydsl_utils.servise.AbstractSelectService;
import com.utils.querydsl_utils.servise.CommonService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Configuration
@ComponentScan(basePackages = {
        "com.utils.querydsl_utils"
})
public class Conf {

    @Bean
    @ConditionalOnMissingBean(JPAQueryFactory.class)
    public JPAQueryFactory jpaQueryFactory(EntityManager em) {
        return new JPAQueryFactory(em);
    }

    @Bean
    @ConditionalOnMissingBean(CommonService.class)
    public CommonService commonService(List<AbstractEntitySelectService<?>> entitySelectServices,
                                       List<AbstractProjectionSelectService<?, ?>> projectionSelectServices) {

        return new CommonService(initServiceMap(entitySelectServices), initServiceMap(projectionSelectServices));
    }

    private <T extends AbstractSelectService> Map<String, T> initServiceMap(List<T> services) {
        return services.stream()
                .collect(Collectors.toMap(
                        AbstractSelectService::getMasterType,
                        Function.identity(),
                        (a, b) -> {
                            String err = String
                                    .format("Duplicate Master Type: %s (%s %s)",
                                            a.getMasterType(), a.getClass().getSimpleName(), b.getClass().getSimpleName());
                            throw new IllegalStateException(err);
                        }
                ));
    }

}
