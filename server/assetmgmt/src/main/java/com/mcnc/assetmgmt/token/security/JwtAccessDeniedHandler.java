package com.mcnc.assetmgmt.token.security;

import com.mcnc.assetmgmt.util.common.CodeAs;
import com.mcnc.assetmgmt.util.common.Response;
import com.mcnc.assetmgmt.util.exception.CustomException;
import com.mcnc.assetmgmt.util.exception.dto.ErrorDto;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
/**
 * title : JwtAccessDeniedHandler
 *
 * description : AccessDeniedHandler 커스텀, 권한 없을 시 접근 제어
 *
 * reference : Spring security + JWT : https://do5do.tistory.com/14
 *             ,https://velog.io/@suhongkim98/Spring-Security-JWT%EB%A1%9C-%EC%9D%B8%EC%A6%9D-%EC%9D%B8%EA%B0%80-%EA%B5%AC%ED%98%84%ED%95%98%EA%B8%B0
 *
 *
 * author : 임현영
 * date : 2023.11.08
 **/
@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
        response.setHeader(HttpHeaders.AUTHORIZATION, CodeAs.BLANK);
        response.setHeader(CodeAs.HEADER_AUTH_INFO, CodeAs.REQUEST_AUTHORIZE_ERROR_CODE+" : "+CodeAs.REQUEST_AUTHORIZE_ERROR_MESSAGE);
        response.setHeader(CodeAs.TOKEN_ERROR, CodeAs.REQUEST_AUTHORIZE_ERROR_CODE);
        //인증은 되어 있지만, 해당 자원에 대한 권한이 없는 경우 호출됩니다. 403
        response.sendError(HttpServletResponse.SC_FORBIDDEN);
    }
}
