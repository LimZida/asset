package com.mcnc.assetmgmt.history.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * title : HistoryEntity
 *
 * description : asset_log 테이블 매핑 Entity

 * reference :
 *
 * author : 임채성
 *
 * date : 2023.11.24
 **/
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "asset_log")
public class HistoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ASSET_LOG_SEQ")
    private Long id;

    @Column(name = "ASSET_NO")
    private String assetNo;

    @Column(name = "PREVIOUS_USER_ID")
    private String previousUserId;

    @Column(name = "NEW_USER_ID")
    private String newUserId;

    @Column(name = "PREVIOUS_USER_DEPT")
    private String previousUserDept;

    @Column(name = "PREVIOUS_USER_NAME")
    private String previousUserName;

    @Column(name = "NEW_USER_DEPT")
    private String newUserDept;

    @Column(name = "NEW_USER_NAME")
    private String newUserName;

    @Column(name = "ASSET_CATEGORY")
    private String assetCategory;

    @Column(name = "ASSET_STATUS")
    private String assetStatus;

    @CreationTimestamp
    @Column(name = "CREATE_DATE", updatable = false)
    private LocalDateTime createDate;

    @Column(name = "MODEL_NAME")
    private String modelName;

    @Column(name = "ASSET_FLAG")
    private String assetFlag;

    @Builder
    public HistoryEntity(Long id, String assetNo, String previousUserId, String newUserId, String previousUserDept,
                            String previousUserName, String newUserDept,
                            String assetStatus, String assetCategory,
                            String newUserName, String modelName, String assetFlag) {
        this.id = id;
        this.assetNo = assetNo;
        this.previousUserId = previousUserId;
        this.newUserId = newUserId;
        this.previousUserDept = previousUserDept;
        this.previousUserName = previousUserName;
        this.newUserDept = newUserDept;
        this.assetCategory = assetCategory;
        this.newUserName = newUserName;
        this.assetStatus = assetStatus;
        this.modelName = modelName;
        this.assetFlag = assetFlag;
    }
}