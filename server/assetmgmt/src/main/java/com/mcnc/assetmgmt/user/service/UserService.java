package com.mcnc.assetmgmt.user.service;

import com.mcnc.assetmgmt.code.dto.CodeDto;
import com.mcnc.assetmgmt.user.dto.UserDto;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * title : userService
 *
 * description : userRepository,UserDto 매핑용 UserService 인터페이스
 *
 * reference :
 *
 * author : 임현영
 * date : 2023.11.08
 **/

public interface UserService {
    //로그인
    UserDto.resultInfo getUserByLoginInfoService(UserDto.loginInfo loginInfo , HttpServletResponse response);
    //로그아웃
    UserDto.resultInfo getUserByLogoutService(UserDto.logoutInfo logoutInfo , HttpServletResponse response);
    //모든 임직원 리스트
    List<UserDto.userListInfo> getAllEmployeeService();
    // 활성화된 임직원 리스트
    List<UserDto.userListInfo> getActiveEmployeeService();
    //임직원 추가 및 수정
    List<UserDto.userListInfo> upsertEmployeeService(UserDto.userInfo userInfo, String type);
    //임직원 삭제
    List<UserDto.userListInfo> activateEmployee(UserDto.userIdInfo userIdInfo);
    //ID 중복체크
    UserDto.validateInfo validateEmployeeService(UserDto.userIdInfo userIdInfo);
    //모든 부서 코드 리스트
    List<CodeDto.deptInfo> allDeptCodeService();
    // 활성화된 부서 코드 리스트
    List<CodeDto.deptInfo> activeDeptCodeService();
}
