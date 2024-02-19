package com.mcnc.assetmgmt.dashboard.dto;

import com.mcnc.assetmgmt.util.common.CodeAs;
import com.mcnc.assetmgmt.util.exception.CustomException;
import lombok.*;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;

/**
 * title : DashboardDto
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
public class DashboardDto {
    //    현재 보유하고 있는 자산코드와 이름 응답 dto
    //    ex)
    //    "assetCode": "MNT01",
    //    "assetName": "모니터",
    //    "codeCtg" : "하드웨어"
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED) //파라미터가 없는 생성자 생성
    public static class codeInfo {
        private String assetCode;
        private String assetName;
        private String codeCtg;

        @Builder
        private codeInfo(String assetCode, String assetName, String codeCtg){
            this.assetCode = assetCode;
            this.assetName = assetName;
            this.codeCtg = codeCtg;
        }
    }
    // 반입반출 기록에 대한 row 개수 요청 dto
    //  ex)
    // "rowCount" : 20
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED) //파라미터가 없는 생성자 생성
    @ToString
    public static class rowInfo {
        /*
        * DTO에서 형변환 안맞을 경우 - https://beforb.tistory.com/10
        * */
        private Integer rowCount;

        @Builder
        public rowInfo(Integer rowCount){
            this.rowCount = rowCount;
        }

        public void validate(){
            if (rowCount <0 ){
                throw new CustomException(CodeAs.REQUEST_FIELD_ERROR_CODE,
                        HttpStatus.BAD_REQUEST,CodeAs.REQUEST_FIELD_ERROR_MESSAGE , null);
            }
        }
    }

    //   반입반출 기록에 대한 asset 데이터 응답 dto
    //   ex)
    //   "assetNo": "N20-09-6923",
    //   "assetName": "model-2023",
    //   "assetDiv": "노트북",
    //   "newUserName": "박종권",
    //   "newUserId" : "jkpark2",
    //   "pastUserId" : "hylim"
    //   "pastUserName": "임현영",
    //   "lastModifiedDate": "2023-10-10",
    //   "assetStatus": "반입"
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED) //파라미터가 없는 생성자 생성
    @ToString
    public static class assetInfo {

        private String assetNo;
        private String assetName;
        private String assetDiv;
        private String newUserName;
        private String newUserId;
        private String prevUserName;
        private String prevUserId;
        private String lastModifiedDate;
        private String assetStatus;

        @Builder
        private assetInfo(String assetNo, String assetName, String assetDiv, String newUserName, String newUserId,
                          String prevUserName, String prevUserId, String lastModifiedDate, String assetStatus){
            this.assetNo = assetNo;
            this.assetName = assetName;
            this.assetDiv = assetDiv;
            this.newUserName = newUserName;
            this.newUserId = newUserId;
            this.prevUserName = prevUserName;
            this.prevUserId = prevUserId;
            this.lastModifiedDate = lastModifiedDate;
            this.assetStatus = assetStatus;
        }
    }

    //   HW의 총개수, 할당, 잉여, 고장, 노후 등 종합 상세 내역에 대한 응답 dto
    //   ex)
    //        "totalCnt": 52,
    //        "allocationAsset": 42,
    //        "notAllocationAsset": 9,
    //        "oldAsset": 40,
    //        "failureAsset": 1,
    //        "assetCodeName": "모니터",
    //        "assetCode": "AST01"
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED) //파라미터가 없는 생성자 생성
    @ToString
    public static class hwListInfo {
        private int totalCnt;
        private int allocationAsset;
        private int notAllocationAsset;
        private int oldAsset;
        private int failureAsset;
        private String assetCodeName;
        private String assetCode;

        @Builder
        private hwListInfo(int totalCnt, int allocationAsset, int notAllocationAsset,
                           int oldAsset, int failureAsset, String assetCodeName, String assetCode){
            this.totalCnt = totalCnt;
            this.allocationAsset = allocationAsset;
            this.notAllocationAsset = notAllocationAsset;
            this.oldAsset = oldAsset;
            this.failureAsset = failureAsset;
            this.assetCodeName = assetCodeName;
            this.assetCode = assetCode;
        }
    }

    //   SW의 총개수, 할당, 잉여, 고장, 노후 등 종합 상세 내역에 대한 응답 dto
    //   ex)
    //        "totalCnt": 52,
    //        "allocationAsset": 42,
    //        "notAllocationAsset": 9,
    //        "expirationAsset": 40,
    //        "soonExpirationAsset": 1,
    //        "assetCodeName": "프로그램",
    //        "assetCode": "PG01"
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED) //파라미터가 없는 생성자 생성
    @ToString
    public static class swListInfo {
        private int totalCnt;
        private int allocationAsset;
        private int notAllocationAsset;
        private int expirationAsset;
        private int soonExpirationAsset;
        private String assetCodeName;
        private String assetCode;

        @Builder
        private swListInfo(int totalCnt, int allocationAsset, int notAllocationAsset,
                           int expirationAsset, int soonExpirationAsset, String assetCodeName, String assetCode){
            this.totalCnt = totalCnt;
            this.allocationAsset = allocationAsset;
            this.notAllocationAsset = notAllocationAsset;
            this.expirationAsset = expirationAsset;
            this.soonExpirationAsset = soonExpirationAsset;
            this.assetCodeName = assetCodeName;
            this.assetCode = assetCode;
        }
    }

    // 현재 보유하고 있는 자산코드와 이름
    // ex)
    //    {
    //        "hwList": [
    //        "모니터",
    //        "노트북",
    //        "데스크탑",
    //        "서버",
    //        "스마트폰"
    //        ],
    //        "swList": [
    //        "프로그램",
    //        "운영체제"
    //        ]
    //    }
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED) //파라미터가 없는 생성자 생성
    @ToString
    public static class assetMapInfo {
        private List<String> hwList;
        private List<String> swList;

        @Builder
        private assetMapInfo(List<String> hwList, List<String> swList){
            this.hwList = hwList;
            this.swList = swList;
        }
    }
}
