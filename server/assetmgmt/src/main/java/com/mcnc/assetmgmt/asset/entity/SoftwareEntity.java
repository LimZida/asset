package com.mcnc.assetmgmt.asset.entity;

import com.mcnc.assetmgmt.code.entity.Code;
import com.mcnc.assetmgmt.util.common.CodeAs;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * title : SoftwareEntity
 *
 * description : builder 패턴 사용
 *
 * reference : [Spring Jpa] @Builder 사용법 - https://aamoos.tistory.com/687
 *             [JPA] column default value 넣기 - https://gksdudrb922.tistory.com/279
 *
 * author : jshong
 *
 * date : 2023-12-06
 **/

@Getter
@Entity(name = "SOFTWARE")
@DynamicInsert
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SoftwareEntity {
    @Id
    @Column(name = "SW_NO", length = 50, nullable = false)
    private String swNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SW_DIV")
    private Code swDiv;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SW_MFR")
    private Code swMfr;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SW_CATEGORY")
    private Code swCategory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SW_LOCATION")
    private Code swLocation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SW_USAGE")
    private Code swUsage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SW_OS")
    private Code swOS;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SW_OS_VERSION")
    private Code swOSVersion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SW_LICENSE")
    private Code swLicense;

    @Column(name = "SW_NAME", length = 30, nullable = false)
    private String swName;

    @Column(name = "SW_REMARKS", length = 100)
    private String swRemarks;

    @Column(name = "PURCHASE_DATE")
    private Timestamp purchaseDate;


    @Column(name = "EXPIRE_DATE")
    private Timestamp expireDate;

    @Column(name = "SW_SN", length = 20, nullable = false)
    private String swSN;

    @CreationTimestamp
    @Column(name = "CREATE_DATE", updatable = false)
    private LocalDateTime createDate;

    @UpdateTimestamp
    @Column(name = "UPDATE_DATE")
    private LocalDateTime updateDate;

    @Column(name = "CREATE_ID", length = 30)
    private String createId;

    @Column(name = "UPDATE_ID", length = 30)
    private String updateId;

    @ColumnDefault(CodeAs.NEGATIVE)
    @Column(name = "EXPIRE_YN", length = 1)
    private Boolean expireYn;

    @ColumnDefault(CodeAs.NEGATIVE)
    @Column(name = "DELETE_YN", length = 1)
    private Boolean deleteYn;

    @Column(name = "SW_QUANTITY")
    private Integer swQuantity;

    @Builder
    public SoftwareEntity(String swNo, Code swDiv, Code swCategory,
                          Code swLocation, String swName, Code swMfr, Code swUsage,
                          String swRemarks, Timestamp purchaseDate, Timestamp expireDate,
                          String swSN, LocalDateTime createDate, LocalDateTime updateDate,
                          String createId, String updateId, Boolean expireYn, Boolean deleteYn,
                          Code swOS, Code swOSVersion, Code swLicense, int swQuantity) {
        this.swNo = swNo;
        this.swDiv = swDiv;
        this.swCategory = swCategory;
        this.swLocation = swLocation;
        this.swName = swName;
        this.swMfr = swMfr;
        this.swUsage = swUsage;
        this.swRemarks = swRemarks;
        this.purchaseDate = purchaseDate;
        this.expireDate = expireDate;
        this.swSN = swSN;
        this.createDate = createDate;
        this.updateDate = updateDate;
        this.createId = createId;
        this.updateId = updateId;
        this.expireYn = expireYn;
        this.deleteYn = deleteYn;
        this.swOS = swOS;
        this.swOSVersion = swOSVersion;
        this.swLicense = swLicense;
        this.swQuantity = swQuantity;
    }

    // SW 자산 삭제 시 deleteYn N -> Y
    public void deleteSoftware(Boolean deleteYn) {
        this.deleteYn = deleteYn;
    }

    // SW 자산 만료 시 expireYn N -> Y
    public void expireSoftware(Boolean expireYn) {
        this.expireYn = expireYn;
    }
}
