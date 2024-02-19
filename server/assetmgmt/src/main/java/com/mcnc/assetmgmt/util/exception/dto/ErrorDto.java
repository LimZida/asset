package com.mcnc.assetmgmt.util.exception.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
/**
 * title : ErrorDto
 *
 * description : 에러 시 ResponseEntity Body에 들어갈 ErrorDto
 *
 * reference :
 *
 * author : 임현영
 * date : 2023.11.20
 **/
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) //파라미터가 없는 생성자 생성
public class ErrorDto {
    private String errorCode;
    private HttpStatus status;
    private String errorMessage;
    private String errorDetailMessage;

    @Builder
    private ErrorDto(String errorCode, HttpStatus status, String errorMessage, String errorDetailMessage){
        this.errorCode = errorCode;
        this.status = status;
        this.errorMessage = errorMessage;
        this.errorDetailMessage = errorDetailMessage;
    }
}
