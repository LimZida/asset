package com.mcnc.assetmgmt.user.dto;

import com.mcnc.assetmgmt.util.common.CodeAs;
import com.mcnc.assetmgmt.util.exception.CustomException;
import lombok.*;
import org.springframework.http.HttpStatus;

/**
 * title : UserDto
 *
 * description : 임직원 json request,response 매핑용 dto
 *
 * reference : cannot deserialize from object value 해결법 : https://charactermail.tistory.com/488, https://azurealstn.tistory.com/74
 *             request 받았는데 dto 매핑이 안되는 경우 : https://velog.io/@ssol_916/RequestBody%EB%A1%9C-%EB%B0%9B%EC%95%98%EB%8A%94%EB%8D%B0-null%EC%9D%B8-%EA%B2%BD%EC%9A%B0
 *             기본 생성자의 의미 : https://velog.io/@jakeseo_me/%EA%B0%84%EB%8B%A8%EC%A0%95%EB%A6%AC-%EC%9E%90%EB%B0%94%EC%97%90%EC%84%9C-%EA%B8%B0%EB%B3%B8-%EC%83%9D%EC%84%B1%EC%9E%90%EC%9D%98-%EC%9D%98%EB%AF%B8-feat.-Java-Reflection-Jackson-JPA
 *             빌더 : https://velog.io/@mooh2jj/%EC%98%AC%EB%B0%94%EB%A5%B8-%EC%97%94%ED%8B%B0%ED%8B%B0-Builder-%EC%82%AC%EC%9A%A9%EB%B2%95
 *                   https://pamyferret.tistory.com/67
 *                   https://velog.io/@taegon1998/Spring-%ED%9A%8C%EC%9B%90%EA%B0%80%EC%9E%85-%EA%B5%AC%ED%98%84%ED%95%98%EA%B8%B0DTO-builder
 *             dto 깔끔히 관리하기 : https://velog.io/@p4rksh/Spring-Boot%EC%97%90%EC%84%9C-%EA%B9%94%EB%81%94%ED%95%98%EA%B2%8C-DTO-%EA%B4%80%EB%A6%AC%ED%95%98%EA%B8%B0
 *                                https://velog.io/@aidenshin/DTO%EC%97%90-%EA%B4%80%ED%95%9C-%EA%B3%A0%EC%B0%B0
 *
 * author : 임현영
 * date : 2023.11.09
 **/
public class UserDto {
    // 유저 로그인 dto
    // ex)
    //      "userId" : "hylim"
    //      "userPw' : "amore1@#"
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED) //파라미터가 없는 생성자 생성
    @ToString
    public static class loginInfo {
        private String userId;
        private String userPw;

        @Builder
        private loginInfo(String userId, String userPw){
            this.userId = userId;
            this.userPw = userPw;
        }

        public void validate(){
            if ( userId.isEmpty() || userPw.isEmpty() || userId == null || userPw == null){
                throw new CustomException(CodeAs.REQUEST_FIELD_ERROR_CODE,
                        HttpStatus.BAD_REQUEST,CodeAs.REQUEST_FIELD_ERROR_MESSAGE , null);
            }
        }
    }

    // 로그인 결과 T/F dto
    // ex)
    //      "result" : true
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED) //파라미터가 없는 생성자 생성
    @ToString
    public static class resultInfo{
        private Boolean result;

        @Builder
        private resultInfo(Boolean result){
            this.result = result;
        }
    }


    // 임직원 정보 응답(리스트로) dto
    // ex)
    //    "userDept": "DPT24",
    //    "deptName": "대표이사",
    //    "userName": "이문영",
    //    "userId": "mylee@mcnc.co.kr",
    //    "activeYn": true
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED) //파라미터가 없는 생성자 생성
    @ToString
    public static class userListInfo {
        private String userId;
        private String deptName;
        private String userName;
        private boolean activeYn;
        private String userDept;

        @Builder
        private userListInfo(String userId, String deptName, String userName, boolean activeYn, String userDept){
            this.userId = userId;
            this.deptName = deptName;
            this.userName = userName;
            this.activeYn = activeYn;
            this.userDept = userDept;
        }
    }

    // 임직원 추가 및 정보 수정 요청 dto
    // ex)
    //      "userId" : "hylim"
    //      "userDept" : "DPT04"
    //      "userName" : "임현영"
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED) //파라미터가 없는 생성자 생성
    @ToString
    public static class userInfo {
        private String userId;
        private String userDept;
        private String userName;

        @Builder
        private userInfo(String userId, String userDept, String userName){
            this.userId = userId;
            this.userDept = userDept;
            this.userName = userName;
        }

        public void validate(){
            if ( userId.isEmpty() || userDept.isEmpty() || userName.isEmpty()){
                throw new CustomException(CodeAs.REQUEST_FIELD_ERROR_CODE,
                        HttpStatus.BAD_REQUEST,CodeAs.REQUEST_FIELD_ERROR_MESSAGE , null);
            }
        }

    }

    // 임직원 정보 삭제 요청 및 중복체크 요청 시 사용하는 dto
    // ex)
    //      "userId" : "hylim"
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED) //파라미터가 없는 생성자 생성
    @ToString
    public static class userIdInfo {
        private String userId;

        @Builder
        private userIdInfo(String userId){
            this.userId=userId;
        }

        public void validate(){
            if (userId.isEmpty()){
                throw new CustomException(CodeAs.REQUEST_FIELD_ERROR_CODE,
                        HttpStatus.BAD_REQUEST,CodeAs.REQUEST_FIELD_ERROR_MESSAGE , null);
            }
        }
    }

    // 임직원 로그아웃 시 사용하는 dto
    // ex)
    //      "userId" : "hylim"
    //      "accessToken" : "Bearer ..."
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED) //파라미터가 없는 생성자 생성
    public static class logoutInfo {
        private String userId;
        private String accessToken;

        @Builder
        private logoutInfo(String userId, String accessToken){
            this.userId=userId;
            this.accessToken = accessToken;
        }

        public void validate(){
            if (userId.isEmpty()){
                throw new CustomException(CodeAs.REQUEST_FIELD_ERROR_CODE,
                        HttpStatus.BAD_REQUEST,CodeAs.REQUEST_FIELD_ERROR_MESSAGE , null);
            }
        }
    }

    // 임직원 ID 검증 후 응답에 사용하는 dto
    // ex)
    //      "userSelectResult" : "100"
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED) //파라미터가 없는 생성자 생성
    @ToString
    public static class validateInfo {
        private String userSelectResult;

        @Builder
        private validateInfo(String userSelectResult){
            this.userSelectResult = userSelectResult;
        }
    }


}
