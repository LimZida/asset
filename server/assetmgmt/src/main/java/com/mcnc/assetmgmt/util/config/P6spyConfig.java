package com.mcnc.assetmgmt.util.config;

import com.mcnc.assetmgmt.util.logging.P6spyPrettySqlFormatter;
import com.p6spy.engine.spy.P6SpyOptions;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
/**
 * title : P6spyConfig
 *
 * description : 로그 포매터 config (운영사용 x)
 *
 * reference :  https://backtony.github.io/spring/2021-08-13-spring-log-1/
 *
 * author : 임현영
 * date : 2024.01.26
 **/
@Configuration
public class P6spyConfig {
    @PostConstruct
    public void setLogMessageFormat() {
        P6SpyOptions.getActiveInstance().setLogMessageFormat(P6spyPrettySqlFormatter.class.getName());
    }
}