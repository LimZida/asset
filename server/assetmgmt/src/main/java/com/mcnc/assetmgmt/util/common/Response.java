package com.mcnc.assetmgmt.util.common;

import com.mcnc.assetmgmt.trlog.entity.Trlog;
import com.mcnc.assetmgmt.trlog.repository.TrlogRepository;
import com.mcnc.assetmgmt.util.exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * title : Response
 *
 * description : 정상 / 오류 응답 시 공통으로 사용하는 함수
 *
 *               HttpStatus 정의:
 *               OK: 200 OK
 *               CREATED: 201 Created
 *               ACCEPTED: 202 Accepted
 *               NO_CONTENT: 204 No Content
 *               BAD_REQUEST: 400 Bad Request
 *               UNAUTHORIZED: 401 Unauthorized
 *               FORBIDDEN: 403 Forbidden
 *               NOT_FOUND: 404 Not Found
 *               METHOD_NOT_ALLOWED: 405 Method Not Allowed
 *               CONFLICT: 409 Conflict
 *               INTERNAL_SERVER_ERROR: 500 Internal Server Error
 *               BAD_GATEWAY: 502 Bad Gateway
 *               SERVICE_UNAVAILABLE: 503 Service Unavailable
 *               GATEWAY_TIMEOUT: 504 Gateway Timeout
 *
 * reference : static의 사용 범위 - https://velog.io/@ldevlog/17.-static%EB%A9%94%EC%84%9C%EB%93%9C%EC%9D%98-%EA%B5%AC%ED%98%84%EA%B3%BC-%ED%99%9C%EC%9A%A9-%EB%B3%80%EC%88%98%EC%9D%98-%EC%9C%A0%ED%9A%A8-%EB%B2%94%EC%9C%84
 *
 * author : 임현영
 * date : 2023.11.08
 **/
@Slf4j
@Component
public class Response {
    public static TrlogRepository trlogRepository;

    //연관관계 등록
    @Autowired
    public Response(TrlogRepository trlogRepository) {
        Response.trlogRepository = trlogRepository;
    }

    /*
    *  오류 응답하는 함수
    *
    *  1. trLog 생성 후 삽입
    *
    *  2-1. CustomException인 경우 => 기존 코드 메세지 그대로
    *  2-2. NullPointerException인 경우 => 코드 정의
    *  2-3. Exception인 경우, 예상 불가능한 경우 => Custom 생성 후 기존 메세지 그대로
    *
    *
    * */
    public static CustomException makeFailResponse(String errorCode, HttpStatus status, String errorMessage, Exception e ) {
        createTrLog(errorCode , errorMessage);
        //CustomException - 예상 가능한 오류인 경우
        if (e instanceof CustomException) {
            return (CustomException) e;
        }
        //NullPointerException - 요청값이 NULL인 경우
        else if (e instanceof NullPointerException){
            return new CustomException(CodeAs.REQUEST_NULL_ERROR_CODE, HttpStatus.BAD_REQUEST, CodeAs.REQUEST_NULL_ERROR_MESSAGE, e);
        }
        // ohter Exception - 예상 가능하지 않은 오류인 경우
        else {
            return new CustomException(errorCode, status, errorMessage, e);
        }

    }

    /*
    * 정상 응답 함수
    *
    * 1. trLog 생성 후 삽입
    * 2. 정상 status와 res 응답
    *
    * */
    public static ResponseEntity<Object> makeSuccessResponse(HttpStatus status, Object res){

        createTrLog(CodeAs.BLANK , CodeAs.BLANK);

        return ResponseEntity.status(status)
                .body(res);
    }

    /*
     * 매 응답 시 tr로그 적재
     *
     * 1. 에러가 났을 경우에는 에러코드를 담아서, 정상인 경우에는 에러코드를 담지 않고 Trlog 엔티티를 생성합니다.
     * 2. Trlog를 적재합니다.
     *
     * */
    public static void createTrLog(String errorCode, String errorMessage){
        //LoginAop에서 메소드 시작 시 적재했던 trId와 함수명 꺼내기
        String transactionId= MDC.get(CodeAs.TR_ID);
        String method = MDC.get(CodeAs.EXECUTE_METHOD);


        // Trlog 생성
        Trlog trlog= mapObjectToTrlog(errorCode,errorMessage,method,transactionId);

//        trlogRepository.save(trlog);
    }

    // Data <=> Entity 매핑 함수
    private static Trlog mapObjectToTrlog(String errorCode, String errorMessage, String method, String transactionId){
        // 현재 시간을 milliseconds로 얻기
        long currentTimeMillis = System.currentTimeMillis();
        // Timestamp 객체를 생성하여 현재 시간 표현
        Timestamp currentTimestamp = new Timestamp(currentTimeMillis);

        return  Trlog.builder()
                .errCode(errorCode)
                .errDetail(errorMessage)
                .jobName(method)
                .tranId(transactionId)
                .successYn(StringUtils.hasText(errorCode) ? CodeAs.NEGATIVE : CodeAs.POSITIVE)
                .userId(CodeAs.ADMIN)
                .createDate(currentTimestamp)
                .build();
    }

    /*
    * 각 DTO , Entity 에 공통적으로 검증하는 함수
    *
    * */
    public static String getOrDefaultString(String value) {
        return value != null && !value.equals(CodeAs.BLANK) ? value : CodeAs.NULL_TEXT;
    }

    public static Integer getOrDefaultInt(Integer value) {
        return value != null ? value : CodeAs.NULL_DEPTH;
    }

    public static Boolean getOrDefaultBool(Boolean value) {
        return value != null ? value : false;
    }

    public static Timestamp getOrDefaultTime(Timestamp value) {
        if (value == null){
//            throw new CustomException(CodeAs.TIMESTAMP_NULL_ERROR_CODE
//                    , HttpStatus.INTERNAL_SERVER_ERROR, CodeAs.TIMESTAMP_NULL_ERROR_MESSAGE, null);
            return null;
        }
        else{
            return value;
        }
    }
}