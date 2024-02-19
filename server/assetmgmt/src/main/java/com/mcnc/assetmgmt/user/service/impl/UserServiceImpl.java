package com.mcnc.assetmgmt.user.service.impl;

import com.mcnc.assetmgmt.assignment.entity.AssignmentEntity;
import com.mcnc.assetmgmt.assignment.repository.AssignmentRepository;
import com.mcnc.assetmgmt.code.dto.CodeDto;
import com.mcnc.assetmgmt.code.entity.Code;
import com.mcnc.assetmgmt.code.repository.CodeRepository;
import com.mcnc.assetmgmt.token.entity.Token;
import com.mcnc.assetmgmt.token.repository.TokenRepository;
import com.mcnc.assetmgmt.token.security.JwtFilter;
import com.mcnc.assetmgmt.token.security.JwtProvider;
import com.mcnc.assetmgmt.user.dto.UserDto;
import com.mcnc.assetmgmt.user.entity.User;
import com.mcnc.assetmgmt.user.repository.UserRepository;
import com.mcnc.assetmgmt.user.service.UserService;
import com.mcnc.assetmgmt.user.util.MCNCLdap;
import com.mcnc.assetmgmt.util.common.CodeAs;
import com.mcnc.assetmgmt.util.crypto.CryptAlgorithm;
import com.mcnc.assetmgmt.util.exception.CustomException;
import com.mcnc.assetmgmt.util.common.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.stream.Collectors;

/**
 * title : userServiceImpl
 *
 * description : 임직원 인증 및 관리에 필요한 UserService의 구현체 , 로직
 *
 * reference :  올바른 엔티티 수정 방법: https://www.inflearn.com/questions/15944/%EA%B0%95%EC%9D%98-%EC%A4%91%EC%97%90-%EA%B0%92%ED%83%80%EC%9E%85%EC%9D%98-%EC%88%98%EC%A0%95%EC%97%90-%EB%8C%80%ED%95%B4%EC%84%9C-%EB%8B%A4%EB%A4%84%EC%A3%BC%EC%85%A8%EB%8A%94%EB%8D%B0%EC%9A%94-%EB%AC%B8%EB%93%9D-%EC%97%94%ED%8B%B0%ED%8B%B0-%EC%88%98%EC%A0%95%EC%97%90-%EB%8C%80%ED%95%B4%EC%84%9C-%EA%B6%81%EA%B8%88%ED%95%B4%EC%A0%B8-%EC%A7%88%EB%AC%B8%EB%93%9C%EB%A6%BD%EB%8B%88%EB%8B%A4
 *
 *              DTO의 적용 범위 https://tecoble.techcourse.co.kr/post/2021-04-25-dto-layer-scope/
 *              더티체킹 : https://jojoldu.tistory.com/415
 *
 *
 * author : 임현영
 * date : 2023.11.10
 **/
@Service
@RequiredArgsConstructor // 자동주입
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final CodeRepository codeRepository;
    private final AssignmentRepository assignmentRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final MCNCLdap mcncLdap;
    private final CryptAlgorithm cryptAlgorithm;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;


    @Override
    @Transactional
    public  UserDto.resultInfo getUserByLoginInfoService(UserDto.loginInfo loginInfo , HttpServletResponse response) {
            UserDto.resultInfo res = UserDto.resultInfo.builder().build();
        try {
            //Ldap으로 로그인 진행
            Map loginResult = mcncLdap.ldapLogin(loginInfo.getUserId(),
                    cryptAlgorithm.decrypt(loginInfo.getUserPw()));
            if (loginResult.isEmpty()){
                throw new CustomException(CodeAs.USER_LDAP_LOGIN_FAIL_CODE , HttpStatus.INTERNAL_SERVER_ERROR, CodeAs.USER_LDAP_LOGIN_FAIL_MESSAGE , null);
            }
            log.info("Ldap 결과 : {}", loginResult.get(CodeAs.LDAP_RESULT_ID)+" , "+loginResult.get(CodeAs.LDAP_RESULT_NAME));

            //Ldap에서 받아온 userId ex) hylim
            String userId =  (String) loginResult.get(CodeAs.LDAP_RESULT_ID);

            User user = userRepository.findByUserId(userId);
            //Ldap에는 있는데 자산관리에 등록되지 않는 임직원인 경우
            if (user == null){
                throw new CustomException(CodeAs.LOGIN_SELECT_USER_FAIL_CODE , HttpStatus.INTERNAL_SERVER_ERROR, CodeAs.LOGIN_SELECT_USER_FAIL_MESSAGE , null);
            }

            //탈퇴한 사용자일 경우 (activeYn이 false(0)일 경우)
            if (user.isActiveYn() == false){
                throw new CustomException(CodeAs.USER_DELETED_CODE , HttpStatus.INTERNAL_SERVER_ERROR, CodeAs.USER_DELETED_MESSAGE , null);
            }

            // 관리자 권한 여부 확인
            /* 관리자 권한을 가진 임직원일 경우 (adminUser != null)
            *  1. 관리자 권한 정보가 들어있는 토큰을 발급한다.
            *  2. 토큰 테이블에 refresh 토큰을 저장한다.
            *  3. 로그인 결과를 true로 응답한다.
            */
            if (user.getAdminYn().equals(CodeAs.POSITIVE)){
                //관리자 처리 함수
                handleAdminLogin(user, loginInfo, response);

                //로그인 정보 응답
                res = UserDto.resultInfo.builder()
                        .result(true)
                        .build();
            }
            /*
             *  관리자 권한을 가진 임직원이 아닐 경우 (adminUser == null)
             *  1.토큰을 헤더에 발급하지 않고, 관리자가 아니라는 에러를 응답한다.
             */
            else{
                throw new CustomException(CodeAs.USER_LOGIN_FAIL_CODE , HttpStatus.INTERNAL_SERVER_ERROR,
                        CodeAs.USER_LOGIN_FAIL_MESSAGE , null);
            }
            //정상 응답
            return res;
        }
        catch (Exception e){
            System.out.println(e);
            throw Response.makeFailResponse(CodeAs.USER_LOGIN_LOGIC_FAIL_CODE,HttpStatus.INTERNAL_SERVER_ERROR
                    ,CodeAs.USER_LOGIN_LOGIC_FAIL_MESSAGE , e);
        }
    }

    // 권한 위임 및 토큰 발급 함수
    private void handleAdminLogin(User adminUser, UserDto.loginInfo loginInfo, HttpServletResponse response) {
        // userPw 암호화하여 더티체킹 업데이트
        adminUser.updateUserPw(passwordEncoder.encode(loginInfo.getUserPw()));

        // 유저 권한 부여
        Authentication authentication = authenticateUser(adminUser.getUserId(), loginInfo.getUserPw());

        // token 생성 및 Entity 저장
        String accessJWT = jwtProvider.generateAccessToken(authentication);
        String refreshJWT = jwtProvider.generateRefreshToken(authentication);
        if (refreshJWT == null || accessJWT == null || accessJWT.equals(CodeAs.BLANK) || refreshJWT.equals(CodeAs.BLANK)){
            throw new CustomException(CodeAs.JWT_GENERATE_FAIL_CODE , HttpStatus.INTERNAL_SERVER_ERROR,
                    CodeAs.JWT_GENERATE_FAIL_MESSAGE , null);
        }

        //토큰 생성
        Token token= Token.builder()
                .userId(loginInfo.getUserId())
                .refreshToken(CodeAs.JWT_HEADER+refreshJWT)
                .build();

        //refresh 토큰 저장 or 업데이트
        Token saveTokenResult = tokenRepository.save(token);
        if (saveTokenResult == null){
            throw new CustomException(CodeAs.JWT_UPSERT_FAIL_CODE , HttpStatus.INTERNAL_SERVER_ERROR,
                    CodeAs.JWT_UPSERT_FAIL_MESSAGE , null);
        }

        //동시로그인 확인용 Map에 accessToken 넣어줌
        JwtFilter.loginTokenMap.put(loginInfo.getUserId(),accessJWT);

        //헤더에 토큰 정보 저장 후 응답
        response.setHeader(HttpHeaders.AUTHORIZATION, CodeAs.JWT_HEADER+accessJWT);
    }

    //권한 위임 함수
    private Authentication authenticateUser(String userId, String userPw){
        // user 검증
        // 받아온 유저 아이디와 패스워드를 이용해 UsernamePasswordAuthenticationToken 객체 생성
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(userId, userPw);

        // authenticationToken 객체를 통해 Authentication 객체 생성
        // 이 과정에서 CustomUserDetailsService 에서 우리가 재정의한 loadUserByUsername 메서드 호출
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return authentication;
    }

    @Override
    @Transactional
    public UserDto.resultInfo getUserByLogoutService(UserDto.logoutInfo logoutInfo , HttpServletResponse response) {
            UserDto.resultInfo res = UserDto.resultInfo.builder().build();
            String userId = logoutInfo.getUserId();
        try {
            /*
            * 1. 로그아웃 시 헤더에 있는 accessToken과 현재 loginTokenMap 들어있는 accessToken이 동일할 시,
            * 로그아웃 프로세스를 진행합니다.
            *
            * 2. loginTokenMap과 헤더에 userId, Authorization 빈값으로 응답하고, token 테이블 정보를 초기화합니다.
            *
            * 3. 결과값을 응답합니다.
            *
            * */

                /*
                *
                * 1. Token 테이블에 존재하는 refreshToken 조회합니다.
                * 2. 해당 refreshToken을 빈값으로 처리합니다. (기존에 만료된 accessToken 정보를 refresh하여 다른 api 접근 하는 것을 막기 위함)
                * 3. 동시로그인 map과, 헤더에 있는 Authorization과 userId 또한 빈값으로 처리합니다.
                *
                * */
                Token tokenInfo = tokenRepository.findTokenByUserId(userId);
                if(tokenInfo == null){
                    throw new CustomException(CodeAs.SELECT_REFRESH_JWT_ERROR_CODE , HttpStatus.INTERNAL_SERVER_ERROR ,
                            CodeAs.SELECT_REFRESH_JWT_ERROR_MESSAGE , null);
                }

                //기존 수정 전 정보
                String originalState = tokenInfo.getRefreshToken();
                // refreshToken 정보 변경
                tokenInfo.updateRefreshToken(CodeAs.BLANK);
                //수정 후 정보
                String changedState = tokenInfo.getRefreshToken();
                // 정보 변경 오류 시
                if(originalState.equals(changedState)){
                    throw new CustomException(CodeAs.CHANGE_REFRESH_QUERY_JWT_ERROR_CODE , HttpStatus.INTERNAL_SERVER_ERROR ,
                            CodeAs.CHANGE_REFRESH_QUERY_JWT_ERROR_MESSAGE , null);
                }


                doNullResponse(CodeAs.BLANK,response,logoutInfo.getUserId());

                res = UserDto.resultInfo.builder()
                        .result(true)
                        .build();

            return res;
        } catch (Exception e) {
            throw Response.makeFailResponse(CodeAs.USER_LOGOUT_FAIL_CODE,
                    HttpStatus.INTERNAL_SERVER_ERROR,CodeAs.USER_LOGOUT_FAIL_MESSAGE , e);
        }
    }

    //헤더에 있는 토큰 정보를 파싱하는 함수
    public String resolveToken(String token) {
        if (StringUtils.hasText(token) && token.startsWith(CodeAs.JWT_HEADER)) {
            return token.substring(7);
        }else{
            return CodeAs.BLANK;
        }
    }

    //헤더에 빈값으로 응답하는 함수
    private void doNullResponse(String value,HttpServletResponse response, String userId){
        JwtFilter.loginTokenMap.put(userId , value);
        response.setHeader(CodeAs.AUTHORIZATION , value);
        response.setHeader(CodeAs.USER_ID , value);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto.userListInfo> getAllEmployeeService() {

        try {
            /*
             *  1. 특정 부서 (ex. 아모레 운영팀)에 대한 정보로 회원 테이블과 조인하여 임직원 리스트(Entity)를 조회한다.
             *  2. 임직원 리스트(Entity)를 DTO와 매핑해 응답한다.
             */

            // codeList(부서 정보)를 통해 모든 임직원과 매핑해 조회합니다.
            List<UserDto.userListInfo> res = getUserListByDepartments();
            return res;
        }
        catch (Exception e) {
                throw Response.makeFailResponse(CodeAs.USER_LIST_INFO_FAIL_CODE,
                        HttpStatus.INTERNAL_SERVER_ERROR, CodeAs.USER_LIST_INFO_FAIL_MESSAGE , e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto.userListInfo> getActiveEmployeeService() {

        try {
            /*
             *  1. 특정 부서 (ex. 아모레 운영팀)에 대한 정보로 회원 테이블과 조인하여 임직원 리스트(Entity)를 조회한다.
             *  2. 임직원 리스트(Entity)를 DTO와 매핑해 응답한다.
             */
            List<UserDto.userListInfo> res = getActiveUserListByDepartments();
            return res;
        }
        catch (Exception e) {
            throw Response.makeFailResponse(CodeAs.USER_LIST_INFO_FAIL_CODE,
                    HttpStatus.INTERNAL_SERVER_ERROR, CodeAs.USER_LIST_INFO_FAIL_MESSAGE , e);
        }
    }

    @Override
    @Transactional
    public List<UserDto.userListInfo> upsertEmployeeService(UserDto.userInfo userInfo, String type) {
        try{
            /*
            * 1. 요청필드에 들어있는 부서코드로 우선 코드테이블을 조회한다.
            * 2. 코드 정보를 포함한 User 객체를 만든다.
            * 3. 임직원 정보를 저장하거나, 더티체킹을 통해 업데이트 한다.
            * 3-1. 만약 업데이트라면, 할당 테이블에 있는 임직원의 정보를 동일하게 수정한다.
            * 4. 바뀐 결과와 전체 유저 리스트를 응답한다.
            * */
            Code userCode = codeRepository.findByCode(userInfo.getUserDept());
            if (userCode == null){
                throw new CustomException(CodeAs.CODE_SELECT_PK_FAIL_CODE,
                        HttpStatus.INTERNAL_SERVER_ERROR,CodeAs.CODE_SELECT_PK_FAIL_MESSAGE , null);
            }

            User user = mapUserInfoToUser(userInfo,userCode,type);

            List<UserDto.userListInfo> res = mapUserToResultInfo(user , type);

            return res;
        }
        catch (Exception e){
            throw Response.makeFailResponse(CodeAs.USER_INFO_UPSERT_FAIL_CODE,
                    HttpStatus.INTERNAL_SERVER_ERROR,CodeAs.USER_INFO_UPSERT_FAIL_MESSAGE , e );
        }
    }

    // Entity <=> DTO 매핑함수
    private User mapUserInfoToUser(UserDto.userInfo userInfo, Code userCode,String type) {
        String adminYn = "N";
        boolean activeYn = true;

        //업데이트 시 기존 정보를 사용하기 위해 쿼리 조회
        if(type.equals(CodeAs.UPDATE)){
            User user = userRepository.findByUserId(userInfo.getUserId());
            adminYn = user.getAdminYn();
            activeYn = user.isActiveYn();
        }

        return User.builder()
                .userId(userInfo.getUserId())
                .userName(userInfo.getUserName())
                .adminYn(adminYn)
                .code(userCode)
                .activeYn(activeYn)
                .createId(CodeAs.ADMIN)
                .updateId(CodeAs.ADMIN)
                .build();
    }

    @Override
    @Transactional
    public List<UserDto.userListInfo> activateEmployee(UserDto.userIdInfo userIdInfo) {
        try {
            /*
            * 1. 요청에서 받아온 userId를 통해 임직원을 조회한다.
            * 2. 임직원의 삭제여부에 따라 그에 반대로 업데이트 한다. (더티체킹을 통해 바로 수정 가능)
            * 3. 바뀐 결과와 전체 유저 리스트를 응답한다.
            * */
            User user = userRepository.findByUserId(userIdInfo.getUserId());
            //활성화 상태 변경 전
            if(user == null){
                throw new CustomException(CodeAs.USER_INFO_FAIL_CODE,
                        HttpStatus.INTERNAL_SERVER_ERROR,CodeAs.USER_INFO_FAIL_MESSAGE , null);
            }

            //기존 정보 수정 전
            boolean originalState = user.isActiveYn();
            //활성화 상태 변경
            user.updateActiveYn(user.isActiveYn() == true ? false : true);
            //기존 정보 수정 후
            boolean changedState = user.isActiveYn();

            if ( originalState == changedState){
                throw new CustomException(CodeAs.USER_INFO_DELETE_FAIL_CODE,
                        HttpStatus.INTERNAL_SERVER_ERROR,CodeAs.USER_INFO_DELETE_FAIL_MESSAGE , null);
            }

            List<UserDto.userListInfo> res = getUserListByDepartments();

            return res;
        }
        catch (Exception e){
            throw Response.makeFailResponse(CodeAs.USER_INFO_DELETE_FAIL_CODE,
                    HttpStatus.INTERNAL_SERVER_ERROR,CodeAs.USER_INFO_DELETE_FAIL_MESSAGE , e);
        }
    }

    // 유저 정보 추가 및 업데이트 함수
    private List<UserDto.userListInfo> mapUserToResultInfo(User user , String type) {
        User upsertResult = userRepository.save(user);
        if (upsertResult == null){
            throw new CustomException(CodeAs.USER_INFO_UPSERT_FAIL_CODE,
                    HttpStatus.INTERNAL_SERVER_ERROR, CodeAs.USER_INFO_UPSERT_FAIL_MESSAGE , null);
        }

        //유저 정보 업데이트 시
        if(type.equals(CodeAs.UPDATE)){
            //할당 테이블에 있는 유저 정보까지 업데이트(Cascade)
            updateUserToAssign(user);
        }

        return getUserListByDepartments();
    }

    // 할당 테이블 업데이트 함수
    private void updateUserToAssign(User user){
        // 특정 임직원의 할당 리스트 조회
        List<AssignmentEntity> assignList = assignmentRepository.findByUsageId(user.getUserId());
        if(assignList == null){
            throw new CustomException(CodeAs.ASSET_ALLOCATE_LIST_FAIL_CODE,
                    HttpStatus.INTERNAL_SERVER_ERROR, CodeAs.ASSET_ALLOCATE_LIST_FAIL_MESSAGE , null);
        }

        // 순회하며 변경된 내용 업데이트
        for(AssignmentEntity assign : assignList){
            assign.updateUsageDept(user.getCode());
            assign.updateUsageName(user.getUserName());
        }
    }

    // 부서 코드를 응답하는 함수
    private List<UserDto.userListInfo> getCodeListByDPT(){
        List<Code> codeList = codeRepository.findByCodeType(CodeAs.DEPARTMENT);
        if (codeList == null || codeList.isEmpty()) {
            throw new CustomException(CodeAs.CODE_SELECT_DPT_FAIL_CODE,
                    HttpStatus.INTERNAL_SERVER_ERROR, CodeAs.CODE_SELECT_DPT_FAIL_MESSAGE , null);
        }
        log.info("codeList size : {} , data : {}",codeList.size()+" "+codeList);
        return getUserListByDepartments();
    }

    // 받은 부서코드에 속해있는 임직원을 리스트로 응답하는 함수
    private List<UserDto.userListInfo> getUserListByDepartments(){
        List<UserDto.userListInfo> res = new ArrayList<>();

        //모든 임직원 리스트를 조회
        res.addAll(mapUserListToDto(userRepository.findAll()));

        //부서(코드) 순으로 정렬 후 응답
        List<UserDto.userListInfo> sortedRes = getListSort(res);

        return sortedRes;
    }

    // 받은 부서코드에 속해있는 활성화된 임직원을 리스트로 응답하는 함수
    private List<UserDto.userListInfo> getActiveUserListByDepartments(){
        List<UserDto.userListInfo> res = new ArrayList<>();

        //모든 임직원 리스트 조회
        res.addAll(mapUserListToDto(userRepository.findByActiveYn(true)));

        //부서(코드) 순으로 정렬 후 응답
        List<UserDto.userListInfo> sortedRes = getListSort(res);

        return sortedRes;
    }
    
    // 임직원 리스트를 순회하여 매핑하는 함수
    private List<UserDto.userListInfo> mapUserListToDto(List<User> userList) {
        List<UserDto.userListInfo> res = new ArrayList<>();
        for (User user : userList) {
            UserDto.userListInfo userListInfo = mapUserToUserListInfo(user);
            res.add(userListInfo);
        }
        return res;
    }
    // Entity <=> DTO 매핑함수
    private UserDto.userListInfo mapUserToUserListInfo(User user) {
        return UserDto.userListInfo.builder()
                .userDept(Response.getOrDefaultString(user.getCode().getCode()))
                .deptName(Response.getOrDefaultString(user.getCode().getCodeName()))
                .userName(Response.getOrDefaultString(user.getUserName()))
                .userId(Response.getOrDefaultString(user.getUserId()))
                .activeYn(user.isActiveYn())
                .build();
    }

    // Object 정렬 함수
    private List<UserDto.userListInfo> getListSort(List<UserDto.userListInfo> res){
        //람다식을 통해 더 쉽게 sort 사용 (통일성에 어긋나긴 하지만 ㅎㅎ)
        return res.stream()
                .sorted(Comparator.comparing(UserDto.userListInfo::getUserDept))
                .collect(Collectors.toList());
    }


    @Override
    @Transactional(readOnly = true)
    public UserDto.validateInfo validateEmployeeService(UserDto.userIdInfo userIdInfo) {
            UserDto.validateInfo res = UserDto.validateInfo.builder().build();
        try{
            /*
             * 1. 요청으로 들어온 userId를 user테이블에 조회하고 , LDAP에 저장되어있는지도 조회한다.
             *
             * 2-1. 해당 아이디가 둘 중에 하나라도 존재하면, id 검증이 중복 => userSelectResult : Dup
             * 2-2. 해당 아이디가 둘 다 미존재하면, id 검증 통과 => userSelectResult : notDup
             *
             * 3. 해당 결과를 응답한다.
             * */

            User user = userRepository.findByUserId(userIdInfo.getUserId());
            boolean isExistLdapUser = mcncLdap.validateLdapUser(userIdInfo.getUserId());
            //중복이면
            if(user != null || isExistLdapUser){
                res = UserDto.validateInfo.builder()
                        .userSelectResult(CodeAs.DUPLICATE)
                        .build();
            }
            //중복아니고, 아이디 미존재하면
            else{
                res = UserDto.validateInfo.builder()
                        .userSelectResult(CodeAs.NON_DUPLICATE)
                        .build();
            }

            return res;
        }
        catch (Exception e){
            throw Response.makeFailResponse(CodeAs.USER_ID_VALIDATE_FAIL_CODE,
                    HttpStatus.INTERNAL_SERVER_ERROR,CodeAs.USER_ID_VALIDATE_FAIL_MESSAGE , e );
        }
    }

    @Override
    @Transactional(readOnly = true)
    public  List<CodeDto.deptInfo> allDeptCodeService() {
        try {
            /*
            * 1. 부서(DPT) 코드에 해당하는 데이터를 전부 조회한다.
            * 2. 전체 부서를 제외하고, 모든 부서의 코드와 이름을 배열에 담는다.
            * 3. 해당 결과를 응답한다.
            * */
            List<Code> deptList = codeRepository.findByCodeTypeAndActiveYn(CodeAs.DEPARTMENT,true);
            if(deptList == null || deptList.isEmpty()){
                throw new CustomException(CodeAs.CODE_SELECT_DPT_FAIL_CODE,
                        HttpStatus.INTERNAL_SERVER_ERROR,CodeAs.CODE_SELECT_DPT_FAIL_MESSAGE , null);
            }

            List<CodeDto.deptInfo> res = mapCodeListToDeptInfoList(deptList);

            return res;
        }
        catch (Exception e){
            throw Response.makeFailResponse(CodeAs.CODE_SELECT_ALL_DPT_FAIL_CODE,
                    HttpStatus.INTERNAL_SERVER_ERROR,CodeAs.CODE_SELECT_ALL_DPT_FAIL_MESSAGE ,e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public  List<CodeDto.deptInfo> activeDeptCodeService() {
        try {
            /*
             * 1. 부서(DPT) 코드에 활성화된 데이터를 전부 조회한다.(Active만)
             * 2. 전체 부서를 제외하고, 모든 부서의 코드와 이름을 배열에 담는다.
             * 3. 해당 결과를 응답한다.
             * */
            List<Code> deptList = codeRepository.findByCodeTypeAndActiveYn(CodeAs.DEPARTMENT, true);
            if(deptList == null || deptList.isEmpty()){
                throw new CustomException(CodeAs.CODE_SELECT_DPT_FAIL_CODE,
                        HttpStatus.INTERNAL_SERVER_ERROR,CodeAs.CODE_SELECT_DPT_FAIL_MESSAGE , null);
            }

            List<CodeDto.deptInfo> res = mapCodeListToDeptInfoList(deptList);

            return res;
        }
        catch (Exception e){
            throw Response.makeFailResponse(CodeAs.CODE_SELECT_ALL_DPT_FAIL_CODE,
                    HttpStatus.INTERNAL_SERVER_ERROR,CodeAs.CODE_SELECT_ALL_DPT_FAIL_MESSAGE ,e);
        }
    }

    // 부서 리스트를 받아 부서의 이름과 코드를 응답하는 함수
    private List<CodeDto.deptInfo> mapCodeListToDeptInfoList(List<Code> deptList){
        List<CodeDto.deptInfo> res = new ArrayList<>();
        for(Code dept : deptList){
            //전체 부서가 아닐 시(= 각 팀일시) 응답
            if(!dept.getCodeName().equals(CodeAs.ALL)){
                CodeDto.deptInfo deptInfo = mapCodeToCodeDto(dept);
                res.add(deptInfo);
            }
        }
        return res;
    }

    // Entity <=> DTO 매핑함수
    private CodeDto.deptInfo mapCodeToCodeDto(Code dept) {
        return CodeDto.deptInfo.builder()
                .deptCode(Response.getOrDefaultString(dept.getCode()))
                .deptName(Response.getOrDefaultString(dept.getCodeName()))
                .build();
    }

}
