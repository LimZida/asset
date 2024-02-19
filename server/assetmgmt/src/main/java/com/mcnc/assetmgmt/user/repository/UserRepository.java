package com.mcnc.assetmgmt.user.repository;

import com.mcnc.assetmgmt.code.entity.Code;
import com.mcnc.assetmgmt.user.dto.UserDto;
import com.mcnc.assetmgmt.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * title : userRepository
 * 
 * description : 임직원 인증 및 관리에 사용하는 JPA 쿼리
 *
 * reference : 쿼리 직접 사용시 https://sundries-in-myidea.tistory.com/91
 *             JPA 메서드 명령규칙 https://zara49.tistory.com/130
 *                              https://ozofweird.tistory.com/entry/%EC%82%BD%EC%A7%88-%ED%94%BC%ED%95%98%EA%B8%B0-JpaRepository-%EA%B7%9C%EC%B9%99%EC%97%90-%EB%A7%9E%EB%8A%94-%EB%A9%94%EC%84%9C%EB%93%9C
 *
 * author : 임현영
 * date : 2023.11.10
 **/
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User save(User user);
    User findByUserId(String userId);
    User findByUserIdAndAdminYn(String userId, String adminYn);
    User findByUserIdAndActiveYn(String userId, boolean activeYn);
    List<User> findByCode(Code code);
    List<User> findByCodeAndActiveYn(Code code, boolean activeYn);
    List<User> findByActiveYn(boolean activeYn);
}
