package com.mcnc.assetmgmt.util.exception;

import com.mcnc.assetmgmt.util.common.CodeAs;
import com.mcnc.assetmgmt.util.exception.dto.ErrorDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
/**
 * title : CustomExceptionHandler
 *
 * description : custom 예외 발생 시 처리해주는 핸들러
 *
 * reference :  Exception Handler : https://velog.io/@kiiiyeon/%EC%8A%A4%ED%94%84%EB%A7%81-ExceptionHandler%EB%A5%BC-%ED%86%B5%ED%95%9C-%EC%98%88%EC%99%B8%EC%B2%98%EB%A6%AC
 *
 *
 * author : 임현영
 * date : 2023.11.20
 **/
@RestControllerAdvice
@Slf4j
public class CustomExceptionHandler {

    /*
    * 현재 CustomException으로 모든 예외처리를 정의하고 있으므로, 해당 에러를 다루게 됩니다.
    *
    * 1. CustomException에 정의한 status, errorCdoe, message, datailMessage의 정보로 Dto를 생성합니다.
    * 2. 서버 log 기록 및 확인용 Log를 찍습니다.
    * 3. 클라이언트에게 Error 정보를 담은 dto 바디와 헤더를 같이 응답합니다.
    *
    *
    * */
    @ExceptionHandler({CustomException.class})
    public ResponseEntity<ErrorDto> handleCustomServiceException(CustomException ex) {
        HttpStatus status = ex.getStatus();
        String errorCode = ex.getErrorCode();
        String errorMessage = ex.getMessage();
        String errorDetailMessage = ex.getE() != null? ex.getE().toString() : CodeAs.BLANK;

//      개발용
//        if(ex.getE() != null){
//            ex.getE().printStackTrace();
//        }

        //응답 에러 로그 생성
        //(함수만 호출에서 그 안에서 로그)
        makeResFailLog(errorCode,errorMessage,errorDetailMessage);

        //헤더에 에러코드 응답
        HttpHeaders headers = new HttpHeaders();
        headers.add(CodeAs.ERROR_CODE, errorCode);

        ErrorDto errorDto = ErrorDto.builder()
                .errorCode(errorCode)
                .errorMessage(errorMessage)
                .errorDetailMessage(errorDetailMessage)
                .status(status)
                .build();

        return ResponseEntity.status(status)
                .headers(headers)
                .body(errorDto);
    }
    /*
    * 실패 에러 로그
    *
    * 1. StringBuilder를 통해 에러를 자체적으로 찍습니다.
    *
    * */
    public void makeResFailLog(String errorCode, String errorMessage, String errorDetailMessage){
        StringBuilder keyAndValue = new StringBuilder();

        keyAndValue.append(" { ");
        keyAndValue.append("errorCode : ");
        keyAndValue.append(errorCode);
        keyAndValue.append(" , ");
        keyAndValue.append("errorMessage : ");
        keyAndValue.append(errorMessage);
        keyAndValue.append(" , ");
        keyAndValue.append("errorDetailMessage : ");
        keyAndValue.append(errorDetailMessage);
        keyAndValue.append(" } ");

        log.error("##### 응답 : {}", keyAndValue);
    }
}
