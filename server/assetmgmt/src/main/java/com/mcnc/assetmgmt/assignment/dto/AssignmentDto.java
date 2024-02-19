package com.mcnc.assetmgmt.assignment.dto;

import com.mcnc.assetmgmt.assignment.entity.AssignmentEntity;
import com.mcnc.assetmgmt.history.entity.HistoryEntity;
import lombok.*;

import javax.persistence.Column;

/**
 * title : AssignmentDto
 *
 * description : 자원 할당 매핑 DTO
 *
 * reference :
 *
 * author : 임채성
 *
 * date : 23.11.24
 **/
@Getter
@Setter
@ToString
@NoArgsConstructor
public class AssignmentDto {
    private Long id;
    private String assetNo;
    private String usageDept;
    private String usageName;
    private String usageId;
    private String createId;
    private String updateId;
    private String deleteYn;

    // HistoryDto 추가
    private String prevUserId;

    private String newUserId;

    private String prevUserDeptName;

    private String prevUserName;

    private String newUserDeptCode;

    private String newUserDeptName;

    private String newUserName;

    private String assetCategory;

    private String modelName;

    private String assetHwSwFlag;

    private String assetStatus;

    private Long createDate;

    @Builder
    public AssignmentDto(Long id, String assetNo,String createId, String updateId, String deleteYn,
                         String prevUserId, String newUserId, String prevUserDeptName, String prevUserName,
                         String newUserDeptCode, String newUserDeptName, String newUserName, String assetCategory,
                         String modelName, String assetHwSwFlag, String assetStatus, Long createDate) {
        this.id = id;
        this.assetNo = assetNo;
        this.createId = createId;
        this.updateId = updateId;
        this.deleteYn = deleteYn;
        this.prevUserId = prevUserId;
        this.newUserId = newUserId;
        this.prevUserDeptName = prevUserDeptName;
        this.prevUserName = prevUserName;
        this.newUserDeptCode = newUserDeptCode;
        this.newUserDeptName = newUserDeptName;
        this.newUserName = newUserName;
        this.assetCategory = assetCategory;
        this.modelName = modelName;
        this.assetHwSwFlag = assetHwSwFlag;
        this.assetStatus = assetStatus;
        this.createDate = createDate;
    }
}
