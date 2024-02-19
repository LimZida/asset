package com.mcnc.assetmgmt.token.security.custom.entity;

import com.mcnc.assetmgmt.user.entity.User;
import com.mcnc.assetmgmt.util.common.CodeAs;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.Collection;
/**
 * title : PrincipalDetails
 *
 * description : UserDetails interface 구현체, user entity 사용
 *
 * reference : 생성자 User 매개변수 차용: https://velog.io/@kyu9610/Spring-Security-4.-Spring-Security-%EB%A1%9C%EA%B7%B8%EC%9D%B8
 *
 * author : 임현영
 * date : 2023.11.08
 **/
public class UserDetailsImpl implements UserDetails {
    private User user;
    private ArrayList<GrantedAuthority> auth = new ArrayList<>();


    public UserDetailsImpl(User user) {
        this.user = user;
    }

    // 해당 User의 권한을 리턴
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        //역할 부여 가능
        auth.add(new SimpleGrantedAuthority(CodeAs.JWT_ROLE));
        return auth;
    }

    @Override
    public String getUsername() {
        return user.getUserId();
    }

    @Override
    public String getPassword() {
        return user.getUserPw();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
