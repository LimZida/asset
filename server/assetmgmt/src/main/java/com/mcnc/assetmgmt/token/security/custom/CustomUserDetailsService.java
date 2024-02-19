package com.mcnc.assetmgmt.token.security.custom;

import com.mcnc.assetmgmt.token.security.custom.entity.UserDetailsImpl;
import com.mcnc.assetmgmt.user.entity.User;
import com.mcnc.assetmgmt.user.repository.UserRepository;
import com.mcnc.assetmgmt.util.common.CodeAs;
import com.mcnc.assetmgmt.util.common.Response;
import com.mcnc.assetmgmt.util.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

/**
 * title : CustomUserDetailsService
 *
 * description : UserDetailsService interface 구현체, 기존 loadUserByUsername 커스텀
 *
 * reference : loadUserByUsername 개발방식 차용: https://velog.io/@kyu9610/Spring-Security-4.-Spring-Security-%EB%A1%9C%EA%B7%B8%EC%9D%B8
 *
 *
 * author : 임현영
 * date : 2023.11.08
 **/
@Component
@RequiredArgsConstructor // 자동주입
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String userId){
        try{
            User userEntity = userRepository.findByUserId(userId);

            if(userEntity == null){
                throw new CustomException(CodeAs.USER_INFO_FAIL_CODE,
                        HttpStatus.INTERNAL_SERVER_ERROR,CodeAs.USER_INFO_FAIL_MESSAGE , null);
            }

            UserDetailsImpl userDetailsImpl = new UserDetailsImpl(userEntity);
            return userDetailsImpl;
        } catch (Exception e){
            throw Response.makeFailResponse(CodeAs.USER_INFO_FAIL_CODE,
                    HttpStatus.INTERNAL_SERVER_ERROR,CodeAs.USER_INFO_FAIL_MESSAGE, e);
        }
    }
}
