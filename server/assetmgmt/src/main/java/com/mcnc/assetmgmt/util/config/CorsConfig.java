package com.mcnc.assetmgmt.util.config;

import com.mcnc.assetmgmt.util.common.CodeAs;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

/**
 * title : CorsConfig
 *
 * description : CorsConfig 커스텀
 *
 * reference : Spring security + JWT : https://do5do.tistory.com/14
 *             ,https://velog.io/@suhongkim98/Spring-Security-JWT%EB%A1%9C-%EC%9D%B8%EC%A6%9D-%EC%9D%B8%EA%B0%80-%EA%B5%AC%ED%98%84%ED%95%98%EA%B8%B0
 *
 *
 * author : 임현영
 * date : 2023.11.08
 **/
@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration conf = new CorsConfiguration();
        conf.addAllowedMethod( "*" ); // 모든 http method에 응답을 허용하겠다.
        conf.addAllowedOriginPattern( "*" ); // 모든 출처 = ip에 응답을 허용하겠다.
        conf.addAllowedHeader( "*" ); // 모든 요청 헤더에 응답을 허용하겠다.
        conf.setAllowCredentials( true ); // 내 서버가 json으로 응답을 할 때 자바 스크립트에서 처리할 수 있게 설정하는 것
        conf.setMaxAge(3600L); ////preflight 결과를 미리 빠르게 사용하기 위해 1시간동안 캐시에 저장
        conf.setExposedHeaders(Arrays.asList(CodeAs.AUTHORIZATION, CodeAs.HEADER_AUTH_INFO,
                CodeAs.TOKEN_ERROR, CodeAs.USER_ID)); // 클라한테 접근 허용할 헤더
        source.registerCorsConfiguration( "/**", conf ); // 해당 경로에 cors 설정을 적용하겠다.
        return new CorsFilter( source );
    }

}
