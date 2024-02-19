package com.mcnc.assetmgmt.history.dto;

import com.mcnc.assetmgmt.assignment.entity.AssignmentEntity;
import com.mcnc.assetmgmt.history.entity.HistoryEntity;
import lombok.*;

import javax.persistence.Column;
import java.time.LocalDateTime;
/**
 * title : HistoryDto
 *
 * description : 히스토리 매핑 DTO

 * reference :
 *
 * author : 임채성
 *
 * date : 2023.11.24
 **/
@Getter
@Setter
@ToString
@NoArgsConstructor
public class HistoryDto {
    private Long id;

    private String assetNo;

    private String previousUserId;

    private String newUserId;

    private String previousUserDept;

    private String previousUserName;

    private String newUserDept;

    private String newUserName;

    private String assetCategory;

    private String historyCategory;

    private String keyword;

    private int startIdx;

    private int pageCntLimit;

    private Long startDate;

    private Long endDate;

    private String assetStatus;

    private String assetFlag;

    @Builder
    public HistoryDto(Long id, String assetNo, String previousUserId, String newUserId, String previousUserDept,
                      String previousUserName, String newUserDept, String assetCategory, String newUserName,
                      String historyCategory, String keyword, int startIdx, int pageCntLimit,
                      Long startDate, Long endDate, String assetStatus, String assetFlag) {
        this.id = id;
        this.assetNo = assetNo;
        this.previousUserId = previousUserId;
        this.newUserId = newUserId;
        this.previousUserDept = previousUserDept;
        this.previousUserName = previousUserName;
        this.newUserDept = newUserDept;
        this.assetCategory = assetCategory;
        this.newUserName = newUserName;
        this.historyCategory = historyCategory;
        this.keyword = keyword;
        this.startIdx = startIdx;
        this.pageCntLimit = pageCntLimit;
        this.startDate = startDate;
        this.endDate = endDate;
        this.assetStatus = assetStatus;
        this.assetFlag = assetFlag;
    }

    // 자산 코드 응답 dto
    // ex)
    //        "code": "AST01",
    //        "codeName": "모니터"
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED) //파라미터가 없는 생성자 생성
    @ToString
    public static class assetCodeInfo {
        private String code;
        private String codeName;

        @Builder
        private assetCodeInfo(String code, String codeName){
            this.code = code;
            this.codeName = codeName;
        }
    }

    //   반입반출 기록에 대한 데이터 응답 dto
    //   ex)
    //    "assetNo": "hw-test01",
    //    "previousUserId": "cslim",
    //    "previousUserDept": "전략운영팀",
    //    "previousUserName": "한상기",
    //    "newUserDept": "아모레운영팀",
    //    "newUserName": "임채성",
    //    "assetCategory": "노트북",
    //    "assetStatus": "반출",
    //    "createDate": "2023-12-06",
    //    "modelName": "Lg-gram123",
    //    "assetFlag": "HW"
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED) //파라미터가 없는 생성자 생성
    @ToString
    public static class historyInfo {
        private String assetNo;
        private String previousUserId;
        private String previousUserDept;
        private String previousUserName;
        private String newUserDept;
        private String newUserName;
        private String newUserId;
        private String assetCategory;
        private String assetStatus;
        private String createDate;
        private String modelName;
        private String assetFlag;
        @Builder
        private historyInfo(String assetNo, String previousUserId, String previousUserDept, String previousUserName,
                          String newUserDept, String newUserName, String newUserId, String assetCategory, String assetStatus,
                          String createDate, String modelName, String assetFlag){
            this.assetNo = assetNo;
            this.previousUserId = previousUserId;
            this.previousUserDept = previousUserDept;
            this.previousUserName = previousUserName;
            this.newUserDept = newUserDept;
            this.newUserName = newUserName;
            this.assetCategory = assetCategory;
            this.assetStatus = assetStatus;
            this.createDate = createDate;
            this.modelName = modelName;
            this.assetFlag = assetFlag;
            this.newUserId = newUserId;
        }
    }
}