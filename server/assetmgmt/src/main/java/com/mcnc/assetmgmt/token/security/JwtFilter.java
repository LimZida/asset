package com.mcnc.assetmgmt.token.security;

import com.mcnc.assetmgmt.util.common.CodeAs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * title : JwtFilter
 *
 * description : Servlet에서 요청받기 전 Spring security filter단에서 JWT 검증 프로세스
 *
 * reference : Spring security + JWT : https://do5do.tistory.com/14
 *             ,https://velog.io/@suhongkim98/Spring-Security-JWT%EB%A1%9C-%EC%9D%B8%EC%A6%9D-%EC%9D%B8%EA%B0%80-%EA%B5%AC%ED%98%84%ED%95%98%EA%B8%B0
 *
 * author : 임현영
 * date : 2023.11.08
 **/
@Slf4j
@RequiredArgsConstructor //자동주입
public class JwtFilter extends OncePerRequestFilter {
    private final JwtProvider jwtProvider;
    //동시로그인 확인용
    public static ConcurrentHashMap<String,String> loginTokenMap = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //trId 생성
        String transactionId = generateTransactionId();
        MDC.put(CodeAs.TR_ID, transactionId);

        String accessToken = resolveToken(request, CodeAs.AUTHORIZATION);
        String userId = "";
        //login 화면에서는 userId 미존재
        if(request.getHeader(CodeAs.USER_ID) != null){
            userId = request.getHeader(CodeAs.USER_ID);
        }

        // 중복로그인 확인
        String validateDuplicateLogin = loginTokenMap.get(userId);
        if (accessToken.equals(validateDuplicateLogin)){
            log.info("토큰이 일치합니다.");
        }else if(accessToken.equals(validateDuplicateLogin) && StringUtils.hasText(validateDuplicateLogin)){
            log.info("토큰이 일치하지 않습니다.");
        }

        // 다른 환경에서 동일한 아이디 로그인으로 인한 토큰 불일치 현상 (if문 순서 중요)
        if(validateDuplicateLogin != null && !validateDuplicateLogin.equals("") && !validateDuplicateLogin.equals(accessToken)){

            log.info("##### code : {} , message : {}", CodeAs.USER_LOGIN_DUPLICATE_CODE ,CodeAs.USER_LOGIN_DUPLICATE_MESSAGE);

            setHeader(response,
                    CodeAs.BLANK,
                    CodeAs.USER_LOGIN_DUPLICATE_MESSAGE,
                    CodeAs.USER_LOGIN_DUPLICATE_CODE);

            response.sendError(HttpServletResponse.SC_FORBIDDEN);
        }
        // 토큰 일치하는 현상 혹은 로그인 시도(loginTokenMap이 비어있는 상황)
        else{
            // access token 승인 시
            if (StringUtils.hasText(accessToken) && jwtProvider.validateToken(accessToken) == CodeAs.APPROVAL_JWT_MESSAGE) {
                Authentication authentication = jwtProvider.getAuthentication(accessToken);
                SecurityContextHolder.getContext().setAuthentication(authentication); // security context에 인증 정보 저장
            // access token 기간 만료 시
            } else if (StringUtils.hasText(accessToken) && jwtProvider.validateToken(accessToken) == CodeAs.EXPIRED_JWT_ERROR_MESSAGE) {
                log.info("##### code : {} , message : {}", CodeAs.EXPIRED_ACCESS_JWT_ERROR_CODE ,CodeAs.EXPIRED_ACCESS_JWT_ERROR_MESSAGE);

                String refreshToken = "";
                if (StringUtils.hasText(userId)) {
                    // userId로 refreshToken 조회(Bearer 형태로 저장되어있기 떄문에 substring 필요)
                    refreshToken = jwtProvider.getRefreshToken(userId);
                }
                log.info("refresh tk : {}",refreshToken);

                // refresh token 승인 시
                if (StringUtils.hasText(refreshToken) && jwtProvider.validateToken(refreshToken) == CodeAs.APPROVAL_JWT_MESSAGE) {
                    // access token 재발급
                    Authentication authentication = jwtProvider.getAuthentication(refreshToken);
                    String newAccessToken = jwtProvider.generateAccessToken(authentication);
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    setHeader(response,
                            CodeAs.JWT_HEADER+newAccessToken,
                            CodeAs.REISSUE_ACCESS_JWT_ERROR_MESSAGE,
                            CodeAs.REISSUE_ACCESS_JWT_ERROR_CODE);

                    //똑같은 환경 유지를 위해 key:value 업데이트
                    loginTokenMap.put(userId,newAccessToken);

                    log.info("##### code : {} , message : {}", CodeAs.REISSUE_ACCESS_JWT_ERROR_CODE ,CodeAs.REISSUE_ACCESS_JWT_ERROR_MESSAGE);
                // refresh token 기간 만료나 공백일 시
                } else if((StringUtils.hasText(refreshToken) && jwtProvider.validateToken(refreshToken) == CodeAs.EXPIRED_JWT_ERROR_MESSAGE)
                    || refreshToken.isEmpty()){

                    //로그인 화면으로 리다이렉트 유도
                    setHeader(response,
                            CodeAs.BLANK,
                            CodeAs.EXPIRED_REFRESH_JWT_ERROR_MESSAGE,
                            CodeAs.EXPIRED_REFRESH_JWT_ERROR_CODE);

                    //다른 환경에서의 동시로그인 오류가 생기지 않도록 key:value 업데이트
                    loginTokenMap.put(userId,CodeAs.BLANK);
                    log.info("##### code : {} , message : {}", CodeAs.EXPIRED_REFRESH_JWT_ERROR_CODE ,CodeAs.EXPIRED_REFRESH_JWT_ERROR_MESSAGE);
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                }
            }
        }

        filterChain.doFilter(request, response);

    }

    // 헤더에서 토큰 추출
    public String resolveToken(HttpServletRequest request, String header) {
        String bearerToken = request.getHeader(header);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(CodeAs.JWT_HEADER)) {
            return bearerToken.substring(7);
        }else{
            return CodeAs.BLANK;
        }
    }

    // 헤더에 응답값 setting 함수
    public void setHeader(HttpServletResponse response , String author, String authInfo, String tokenError){
        response.setHeader(HttpHeaders.AUTHORIZATION, author);
        response.setHeader(CodeAs.HEADER_AUTH_INFO, authInfo);
        response.setHeader(CodeAs.TOKEN_ERROR, tokenError);
    }

    // 트랜잭션 ID 생성하기
    private String generateTransactionId() {
        return UUID.randomUUID().toString();
    }
}
