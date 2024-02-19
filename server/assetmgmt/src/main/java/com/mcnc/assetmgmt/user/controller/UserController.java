package com.mcnc.assetmgmt.user.controller;

import com.mcnc.assetmgmt.asset.service.HardwareService;
import com.mcnc.assetmgmt.asset.service.SoftwareService;
import com.mcnc.assetmgmt.code.dto.CodeDto;
import com.mcnc.assetmgmt.user.dto.UserDto;
import com.mcnc.assetmgmt.user.service.UserService;
import com.mcnc.assetmgmt.util.common.CodeAs;
import com.mcnc.assetmgmt.util.common.Response;
import com.mcnc.assetmgmt.util.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * title : UserController
 * description : 임직원 인증 및 관리 controller
 *
 * reference : RESTful 설계 규칙 : https://gmlwjd9405.github.io/2018/09/21/rest-and-restful.html
 *                                https://dev-cool.tistory.com/32
 *
 *             @RequestBody란? : https://dev-coco.tistory.com/95 , https://cheershennah.tistory.com/179
 *
 *             RequestParam을 DTO로 바로 받는방법  https://baessi.tistory.com/23
 *
 * author : 임현영
 * date : 2023.11.09
 **/
@RestController
@RequiredArgsConstructor //자동주입
@RequestMapping("/mcnc-mgmts")
@Slf4j
public class UserController {
    private final UserService userService;
    private final HardwareService hardwareService;
    private final SoftwareService softwareService;

    // 로그인
    @PostMapping("/auth/login")
    public ResponseEntity<Object> login(@RequestBody UserDto.loginInfo loginInfo , HttpServletResponse response){
        try {
            log.info("##### 요청: {}",loginInfo.toString());
            loginInfo.validate();

            UserDto.resultInfo loginResult = userService.getUserByLoginInfoService(loginInfo, response);
            log.info("##### 응답: {}",loginResult.toString());

//          HW 노후 / SW 만료 업데이트 로직
            hardwareService.oldHardware();
            softwareService.expireSoftware();

            return Response.makeSuccessResponse(HttpStatus.OK,loginResult);
        }
        catch(Exception e){
           throw Response.makeFailResponse(CodeAs.USER_LOGIN_LOGIC_FAIL_CODE, HttpStatus.INTERNAL_SERVER_ERROR
                   , CodeAs.USER_LOGIN_LOGIC_FAIL_MESSAGE, e);
        }
    }

    // 로그아웃
    @GetMapping("/auth/logout")
    public ResponseEntity<Object> logout(HttpServletRequest request , HttpServletResponse response){
        try {
            //헤더에서 userId 받아오기
            UserDto.logoutInfo logoutInfo = mapHeaderToUserIdInfo(request);

            UserDto.resultInfo logoutResult = userService.getUserByLogoutService(logoutInfo , response);
            log.info("##### 응답: {}",logoutResult.toString());

            return Response.makeSuccessResponse(HttpStatus.OK,logoutResult);
        }
        catch(Exception e){
           throw Response.makeFailResponse(CodeAs.USER_LOGOUT_FAIL_CODE, HttpStatus.INTERNAL_SERVER_ERROR
                   , CodeAs.USER_LOGOUT_FAIL_MESSAGE, e);
        }
    }

    // 헤더에서 id와 token을 가져오는 함수
   private UserDto.logoutInfo mapHeaderToUserIdInfo(HttpServletRequest request){
        String userId = "";
        String accessToken = "";

       String headerUserId = request.getHeader(CodeAs.USER_ID);
       String headerAuthor = request.getHeader(CodeAs.AUTHORIZATION);

       //헤더에 userId와 auth가 둘 다 있는 경우
        if(headerUserId != null && headerAuthor!= null){
            userId = request.getHeader(CodeAs.USER_ID);
            accessToken = request.getHeader(CodeAs.AUTHORIZATION);

            log.info("헤더에서 추출 => userId : {}, accessTk : {}", userId, accessToken);
        }
        //헤더에 userId가 없는 경우
        else if(headerUserId == null && headerAuthor!= null){
            throw new CustomException(CodeAs.REQUEST_HEADER_EMPTY_CODE, HttpStatus.BAD_REQUEST,
                    CodeAs.REQUEST_HEADER_EMPTY_MESSAGE , null);
        }
        //헤더에 auth가 없는 경우
        else if(headerUserId != null && headerAuthor== null){
            throw new CustomException(CodeAs.REQUEST_HEADER_AUTH_EMPTY_CODE, HttpStatus.BAD_REQUEST,
                    CodeAs.REQUEST_HEADER_AUTH_EMPTY_MESSAGE , null);
        }
        //둘 다 없는 경우
        else{
            throw new CustomException(CodeAs.REQUEST_HEADER_ID_AUTH_EMPTY_CODE, HttpStatus.BAD_REQUEST,
                    CodeAs.REQUEST_HEADER_ID_AUTH_EMPTY_MESSAGE , null);
        }

        UserDto.logoutInfo logoutInfo = UserDto.logoutInfo.builder()
                .userId(userId)
                .accessToken(accessToken)
                .build();

        return logoutInfo;
    }

    // 모든 임직원 리스트
    @GetMapping("/employee-managements/employees")
    public ResponseEntity<Object> getAllEmployee(){
        try{
            List<UserDto.userListInfo> userList = userService.getAllEmployeeService();
            log.info("##### 응답 : {}", userList.toString());
            return Response.makeSuccessResponse(HttpStatus.OK,userList);
        }
        catch(Exception e){
           throw Response.makeFailResponse(CodeAs.USER_LIST_INFO_FAIL_CODE, HttpStatus.INTERNAL_SERVER_ERROR
                   , CodeAs.USER_LIST_INFO_FAIL_MESSAGE, e);
        }
    }

    //임직원 추가
    @PostMapping("/employee-managements/employees")
    public ResponseEntity<Object> createEmployee(@RequestBody UserDto.userInfo userInfo){
        try{
            log.info("##### 요청: {}",userInfo.toString());
            userInfo.validate();


            List<UserDto.userListInfo> userList = userService.upsertEmployeeService(userInfo , CodeAs.INSERT);
            log.info("##### 응답 : {}", userList.toString());
            return Response.makeSuccessResponse(HttpStatus.OK , userList);
        }
        catch(Exception e){
           throw Response.makeFailResponse(CodeAs.USER_INFO_INSERT_FAIL_CODE, HttpStatus.INTERNAL_SERVER_ERROR
                   , CodeAs.USER_INFO_INSERT_FAIL_MESSAGE, e);
        }
    }

    //임직원 수정
    @PutMapping("/employee-managements/employees")
    public ResponseEntity<Object> updateEmployee(@RequestBody UserDto.userInfo userInfo){
        try{
            log.info("##### 요청: {}",userInfo.toString());
            userInfo.validate();

            List<UserDto.userListInfo> userList = userService.upsertEmployeeService(userInfo , CodeAs.UPDATE);
            log.info("##### 응답 : {}", userList.toString());
            return Response.makeSuccessResponse(HttpStatus.OK , userList);
        }
        catch(Exception e){
           throw Response.makeFailResponse(CodeAs.USER_INFO_UPDATE_FAIL_CODE, HttpStatus.INTERNAL_SERVER_ERROR
                   , CodeAs.USER_INFO_UPDATE_FAIL_MESSAGE, e);
        }
    }

    //임직원 비활성화하거나 다시 되돌림 (삭제여부를 Y/N으로 업데이트)
    @PutMapping("/employee-managements/employees/activation")
    public ResponseEntity<Object> activateEmployee(@RequestBody UserDto.userIdInfo userIdInfo){
        try{
            log.info("##### 요청: {}",userIdInfo.toString());
            //요청값 누락 여부 판별
            userIdInfo.validate();

            List<UserDto.userListInfo> userList = userService.activateEmployee(userIdInfo);
            log.info("##### 응답 : {}", userList.toString());
            return Response.makeSuccessResponse(HttpStatus.OK,userList);
        }
        catch(Exception e){
           throw Response.makeFailResponse(CodeAs.USER_INFO_DELETE_FAIL_CODE, HttpStatus.INTERNAL_SERVER_ERROR
                   , CodeAs.USER_INFO_DELETE_FAIL_MESSAGE, e);
        }
    }

    //ID 중복체크
    @PostMapping("/employee-managements/employees/validations")
    public ResponseEntity<Object> validateEmployee(@RequestBody UserDto.userIdInfo userIdInfo){
        try{
            log.info("##### 요청: {}",userIdInfo.toString());
            //요청값 누락 여부 판별
            userIdInfo.validate();

            UserDto.validateInfo validateInfo = userService.validateEmployeeService(userIdInfo);
            log.info("##### 응답 : {}",validateInfo.toString());
            return Response.makeSuccessResponse(HttpStatus.OK , validateInfo);
        }
        catch(Exception e){
           throw Response.makeFailResponse(CodeAs.USER_ID_VALIDATE_FAIL_CODE, HttpStatus.INTERNAL_SERVER_ERROR
                   , CodeAs.USER_ID_VALIDATE_FAIL_MESSAGE, e);
        }
    }

    //모든 부서 코드 리스트
    @GetMapping("/employee-managements/departments")
    public ResponseEntity<Object> getAllDeptCode(){
        try{
            List<CodeDto.deptInfo> deptList = userService.allDeptCodeService();
            log.info("##### 응답 : {}", deptList.toString());
            return Response.makeSuccessResponse(HttpStatus.OK , deptList);
        }
        catch(Exception e){
           throw Response.makeFailResponse(CodeAs.CODE_SELECT_ALL_DPT_FAIL_CODE, HttpStatus.INTERNAL_SERVER_ERROR
                   , CodeAs.CODE_SELECT_ALL_DPT_FAIL_MESSAGE, e);
        }
    }
}
