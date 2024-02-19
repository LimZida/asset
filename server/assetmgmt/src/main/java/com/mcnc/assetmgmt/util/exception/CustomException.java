package com.mcnc.assetmgmt.util.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
/**
 * title : CustomException
 *
 * description : 에러코드와 메세지 필드가 추가된 Custom Exception
 *
 * reference :
 *
 * author : 임현영
 * date : 2023.11.20
 **/
@Getter
public class CustomException extends RuntimeException {
    private String errorCode;
    private HttpStatus status;
    private Exception e;

    public CustomException(String errorCode, HttpStatus status, String message , Exception e) {
        super(message);
        this.errorCode = errorCode;
        this.status = status;
        this.e = e != null ? e : null;
    }
}
