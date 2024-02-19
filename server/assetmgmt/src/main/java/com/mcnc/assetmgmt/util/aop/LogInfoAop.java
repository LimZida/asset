package com.mcnc.assetmgmt.util.aop;

import com.mcnc.assetmgmt.trlog.entity.Trlog;
import com.mcnc.assetmgmt.trlog.repository.TrlogRepository;
import com.mcnc.assetmgmt.util.common.CodeAs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * title : LogInfoAop
 *
 * description : 요청이나 응답 파라미터에 대해 log AOP 적용
 *
 *              beforeParameterLog(JoinPoint joinPoint) => 요청에 대한 로그
 *              afterReturnLog(JoinPoint joinPoint, Object returnObj) => 응답에 대한 trlog 적재
 *
 * reference :  로그 aop : https://velog.io/@dhk22/Spring-AOP-%EA%B0%84%EB%8B%A8%ED%95%9C-AOP-%EC%A0%81%EC%9A%A9-%EC%98%88%EC%A0%9C-Logging
 *              aop 어노테이션 : https://programforlife.tistory.com/107 , https://code-lab1.tistory.com/193
 *              프록시 패턴 : https://velog.io/@newtownboy/%EB%94%94%EC%9E%90%EC%9D%B8%ED%8C%A8%ED%84%B4-%ED%94%84%EB%A1%9D%EC%8B%9C%ED%8C%A8%ED%84%B4Proxy-Pattern
 *              null 오류 : returnObj가 존재 안할 때, 접근하려하면 nullpoint 오류나니 주의하자.
 *
 * author : 임현영
 * date : 2023.10.24
 **/
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class LogInfoAop {
    // com.mcnc.assetmgmt 이하 패키지의 모든 클래스 이하 모든 메서드에 적용 (trlog, security 부분 제외)
    @Pointcut("execution(* com.mcnc.assetmgmt..*(..)) && " +
            "!execution(* com.mcnc.assetmgmt.trlog..*(..)) " +
            "&& !execution(* com.mcnc.assetmgmt.token.security..*(..))")
    private void cut(){}

    /*
    * Pointcut에 의해 필터링된 경로로 들어오는 경우 메서드 호출 전에 적용
    * 1. TrID를 생성하고, 메서드 정보와 요청을 받아옵니다.
    *
    * */
    @Before("cut()")
    public void beforeParameterLog(JoinPoint joinPoint) throws IllegalAccessException {
        // 메서드 정보 받아오기
        String method = getMethod(joinPoint);

        //trId 생성
//        String transactionId = generateTransactionId();
//        MDC.put(CodeAs.TR_ID, transactionId);
        MDC.put(CodeAs.EXECUTE_METHOD, method);
//        log.info("##### 요청 컨트롤러 method : {}",method);
    }

    /*
    * 매 작업이 끝날 때 마다 실행되는 함수, 로그를 찍기 위함
    * */
    @AfterReturning(value = "cut()", returning = "returnObj")
    public void afterReturnLog(JoinPoint joinPoint, Object returnObj){
        // 메소드 받아오기
        String method = getMethod(joinPoint);
        MDC.put(CodeAs.EXECUTE_METHOD, method);
//        log.info(method+ " ======> 메소드 종료");
    }

    // 트랜잭션 ID 생성하기
    private String generateTransactionId() {
        return UUID.randomUUID().toString();
    }

    // JoinPoint로 메서드 정보 가져오기
    private String getMethod(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        return signature.toShortString();
    }
}