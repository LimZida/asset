package com.mcnc.assetmgmt.util.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * title : QuerydslConfig
 *
 * description : QuerydslConfig
 *
 * reference :
 *
 * author : 임채성
 * date : 2024.1.17
 **/
@Configuration
public class QuerydslConfig {
    @PersistenceContext
    private EntityManager entityManager;
    @Bean
    public JPAQueryFactory jpaQueryFactory() {
        return new JPAQueryFactory(entityManager);
    }
}
