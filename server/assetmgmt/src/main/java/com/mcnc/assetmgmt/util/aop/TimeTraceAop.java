package com.mcnc.assetmgmt.util.aop;

import com.mcnc.assetmgmt.util.common.Response;
import com.mcnc.assetmgmt.util.exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * title : TimeTraceAop
 *
 * description : 함수 실행 시간에 대해 AOP 적용
 *
 * reference :  시간 aop : https://hseungyeon.tistory.com/349
 *              aop 어노테이션 : https://programforlife.tistory.com/107 , https://code-lab1.tistory.com/193
 *              프록시 패턴 : https://velog.io/@newtownboy/%EB%94%94%EC%9E%90%EC%9D%B8%ED%8C%A8%ED%84%B4-%ED%94%84%EB%A1%9D%EC%8B%9C%ED%8C%A8%ED%84%B4Proxy-Pattern
 *
 * author : 임현영
 * date : 2023.10.24
 **/
@Slf4j
@Aspect
@Component
public class TimeTraceAop {
    // 공통관심사항을 적용할 곳 Controller만 타겟팅  (trlog, security 부분 제외)
    @Around("execution(* com.mcnc.assetmgmt..controller..*(..)) " +
            "&& !execution(* com.mcnc.assetmgmt.trlog..*(..)) " +
            "&& !execution(* com.mcnc.assetmgmt.token.security..*(..))")

    /*
    * 매 함수의 실행시간을 체크하는 기능
    * */
    public Object execute(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();    // 시작 시각

        try {
            return joinPoint.proceed();
        }
        catch (CustomException e){
            log.info("함수 실행 중 {} 메소드의 오류 캐치",getMethod(joinPoint));
            throw Response.makeFailResponse(e.getErrorCode(),
                    HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), e.getE());
        }
        finally {
            long finish = System.currentTimeMillis();   // 종료 시각
            long timeMs = finish - start;   // 호출 시간

            log.info("--------------------------------------------------------------------------------");
            log.info("실행 함수 : {}",joinPoint.getSignature().toShortString());
            log.info("서비스 수행시간 : {}", timeMs + "ms");
            log.info("--------------------------------------------------------------------------------");
        }
    }

    // JoinPoint로 메서드 정보 가져오기
    private String getMethod(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        return signature.toShortString();
    }
}