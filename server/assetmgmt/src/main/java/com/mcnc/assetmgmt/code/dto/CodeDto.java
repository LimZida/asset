package com.mcnc.assetmgmt.code.dto;

import com.mcnc.assetmgmt.util.common.CodeAs;
import com.mcnc.assetmgmt.util.exception.CustomException;
import lombok.*;
import org.springframework.http.HttpStatus;

import java.util.List;

/**
 * title : CodeDto
 *
 * description : 코드 json request,response 매핑용 dto
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
public class CodeDto {
    // 임직원 추가 시 필요한 부서 코드 응답 dto
    // ex)
    //        "deptCode": "DPT27",
    //        "deptName": "대표이사"
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED) //파라미터가 없는 생성자 생성
    @ToString
    public static class deptInfo {
        private String deptCode;
        private String deptName;

        @Builder
        private deptInfo(String deptCode, String deptName){
            this.deptCode = deptCode;
            this.deptName = deptName;
        }
    }

    // 카테고리 코드 및 이름 응답 dto
    // ex)
    //       "code": "CTG0101",
    //       "codeName": "자산",
    //       "upperCode": "CTG01",
    //       "codeInnerType" : "AST"
    //       "codeType": "CTG",
    //       "codeDepth": 0,
    //       "activeYn": "N"
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED) //파라미터가 없는 생성자 생성
    @ToString
    public static class categoryInfo {
        private String code;
        private String codeName;
        private String codeInnerType;
        private String upperCode;
        private String codeType;
        private int codeDepth;
        private boolean activeYn;

        @Builder
        private categoryInfo(String code, String codeName, String codeInnerType, String upperCode, String codeType, int codeDepth, boolean activeYn){
            this.code = code;
            this.codeName = codeName;
            this.codeInnerType = codeInnerType;
            this.upperCode = upperCode;
            this.codeType = codeType;
            this.codeDepth = codeDepth;
            this.activeYn = activeYn;
        }
    }

    // 카테고리 및 특정카테고리 내 코드 수정 요청 dto
    // ex)
    //    "code" : "DPT26"
    //    "codeName" : "국내사업전략"
    //    "codeRemark" : "국내사업하는곳"
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED) //파라미터가 없는 생성자 생성
    @ToString
    public static class codeModifyInfo {
        private String code;
        private String codeName;
        private String codeRemark;

        @Builder
        private codeModifyInfo(String code, String codeName, String codeRemark){
            this.code = code;
            this.codeName = codeName;
            this.codeRemark = codeRemark;
        }

        public void validate(){
            if ( code.isEmpty() || codeName.isEmpty()){
                throw new CustomException(CodeAs.REQUEST_FIELD_ERROR_CODE,
                        HttpStatus.BAD_REQUEST,CodeAs.REQUEST_FIELD_ERROR_MESSAGE , null);
            }
        }
    }

    // 특정 카테고리 내 코드 추가 요청 dto
    // ex)
    //    "code": "DPT29"
    //    "codeName" : "국내사업전략"
    //    "codeRemark" : "국내사업하는곳"
    //    "codeType" : "DPT"
    //    "codeCtg" : "CTG010101"
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED) //파라미터가 없는 생성자 생성
    @ToString
    public static class codeCreateInfo {
        private String code;
        private String codeName;
        private String codeRemark;
        private String codeType;
        private String codeCtg;

        @Builder
        private codeCreateInfo(String code, String codeName, String codeRemark, String codeType, String codeCtg){
            this.code = code;
            this.codeName = codeName;
            this.codeRemark = codeRemark;
            this.codeType = codeType;
            this.codeCtg = codeCtg;
        }

        public void validate(){
            if ( code.isEmpty() || codeType.isEmpty() || codeCtg.isEmpty()){
                throw new CustomException(CodeAs.REQUEST_FIELD_ERROR_CODE,
                        HttpStatus.BAD_REQUEST,CodeAs.REQUEST_FIELD_ERROR_MESSAGE , null);
            }
        }


    }

    // 특정 카테고리 내 속하는 코드 조회 시 응답 dto
    // ex) {
    //      "codeLIst" : [
    //        {"code": "DPT24",
    //        "codeName": "대표이사",
    //        "codeCtg": "CTG010101",
    //        "codeRemark": "회사의 대표",
    //        "activeYn": true
    //        }
    //       ],
    //       "codeType" : "DPT"
    //      }
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED) //파라미터가 없는 생성자 생성
    @ToString
    public static class codeListMapInfo {
        private List<codeListInfo> codeList;
        private String codeType;


        @Builder
        private codeListMapInfo(List<codeListInfo> codeList, String codeType){
            this.codeList = codeList;
            this.codeType = codeType;
        }
    }
    // codeListMapInfo DTO에 사용되는 object
    // ex)
    //        "code": "DPT24",
    //        "codeName": "대표이사",
    //        "codeCtg": "CTG010101",
    //        "codeRemark": "회사의 대표",
    //        "activeYn": true
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED) //파라미터가 없는 생성자 생성
    @ToString
    public static class codeListInfo {
        private String code;
        private String codeName;
        private String codeCtg;
        private String codeRemark;
        private Boolean activeYn;

        @Builder
        private codeListInfo(String code, String codeName, String codeCtg
                , String codeRemark, Boolean activeYn){
            this.code = code;
            this.codeName = codeName;
            this.codeCtg = codeCtg;
            this.codeRemark = codeRemark;
            this.activeYn = activeYn;
        }
    }

    // 카테고리 코드 추가 요청 dto
    // ex)
    //      "upperCode" : "CTG01"
    //      "codeDepth" : 1
    //      "codeName" : "윽.."
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED) //파라미터가 없는 생성자 생성
    @ToString
    public static class categoryCreateInfo {
        private String code;
        private String upperCode;
        private int codeDepth;
        private String codeName;
        private String codeType;

        @Builder
        private categoryCreateInfo(String code, String upperCode, int codeDepth, String codeName, String codeType){
            this.code = code;
            this.upperCode = upperCode;
            this.codeDepth = codeDepth;
            this.codeName = codeName;
            this.codeType = codeType;
        }

        public void validate(){
            if ( upperCode.isEmpty() || codeDepth < 0 || codeName.isEmpty()){
                throw new CustomException(CodeAs.REQUEST_FIELD_ERROR_CODE,
                        HttpStatus.BAD_REQUEST,CodeAs.REQUEST_FIELD_ERROR_MESSAGE , null);
            }
        }

    }

    // 코드 수정, 추가, 삭제 결과 응답 dto
    // ex)
    //      "result":true
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED) //파라미터가 없는 생성자 생성
    @ToString
    public static class resultInfo {
        private Boolean result;

        @Builder
        private resultInfo(Boolean result){
            this.result = result;
        }
    }

    // 코드 중복 검증 요청 dto
    // ex)
    //      "codeName":"아모레운영팀"
    //      "codeType":"DPT"
    //      "codeCtg" :"CTG010201"
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED) //파라미터가 없는 생성자 생성
    @ToString
    public static class validationCodeInfo {
        private String codeName;
        private String codeType;
        private String codeCtg;
        @Builder
        private validationCodeInfo(String codeName, String codeType, String codeCtg){
            this.codeName = codeName;
            this.codeType = codeType;
            this.codeCtg = codeCtg;
        }

        public void validate(){
            if ( codeName.isEmpty() || codeType.isEmpty()){
                throw new CustomException(CodeAs.REQUEST_FIELD_ERROR_CODE,
                        HttpStatus.BAD_REQUEST,CodeAs.REQUEST_FIELD_ERROR_MESSAGE , null);
            }
        }
    }
    // codeType 검증 요청 dto
    // ex)
    //      "codeType" : "SRT"
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED) //파라미터가 없는 생성자 생성
    @ToString
    public static class codeTypeInfo{
        private String codeType;

        @Builder
        private codeTypeInfo(String codeType){
            this.codeType = codeType;
        }

        public void validate(){
            if ( codeType.isEmpty()){
                throw new CustomException(CodeAs.REQUEST_FIELD_ERROR_CODE,
                        HttpStatus.BAD_REQUEST,CodeAs.REQUEST_FIELD_ERROR_MESSAGE , null);
            }
        }
    }

    // codeType 검증 결과 dto
    // ex)
    //      "result" : "dup"
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED) //파라미터가 없는 생성자 생성
    @ToString
    public static class codeTypeResultInfo{
        private String result;

        @Builder
        private codeTypeResultInfo(String result){
            this.result = result;
        }
    }

    // 코드 중복 검증 요청 dto
    // ex)
    //      "codeName":"아모레운영팀"
    //      "codeType":"CTG"
    //      "upperCode" : "CTG1011555"
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED) //파라미터가 없는 생성자 생성
    @ToString
    public static class validationCategoryCodeInfo {
        private String codeName;
        private String codeType;
        private String upperCode;
        @Builder
        private validationCategoryCodeInfo(String codeName, String codeType, String upperCode){
            this.codeName = codeName;
            this.codeType = codeType;
            this.upperCode = upperCode;
        }

        public void validate(){
            if ( codeName.isEmpty() || upperCode.isEmpty() || codeType.isEmpty()){
                throw new CustomException(CodeAs.REQUEST_FIELD_ERROR_CODE,
                        HttpStatus.BAD_REQUEST,CodeAs.REQUEST_FIELD_ERROR_MESSAGE , null);
            }
        }
    }

    // 코드 조회 및 삭제 요청, 중복체크 진행 후 응답 dto
    // ex)
    //     "code" : "DPT29"
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED) //파라미터가 없는 생성자 생성
    @ToString
    public static class codeInfo {
        private String code;

        @Builder
        public codeInfo(String code){
            this.code = code;
        }

        public void validate(){
            if (code.isEmpty()){
                throw new CustomException(CodeAs.REQUEST_FIELD_ERROR_CODE,
                        HttpStatus.BAD_REQUEST,CodeAs.REQUEST_FIELD_ERROR_MESSAGE , null);
            }
        }
    }
}
