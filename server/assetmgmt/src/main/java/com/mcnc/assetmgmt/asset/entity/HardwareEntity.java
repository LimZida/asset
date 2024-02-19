package com.mcnc.assetmgmt.asset.entity;

import com.mcnc.assetmgmt.code.entity.Code;
import com.mcnc.assetmgmt.util.common.CodeAs;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.Entity;
import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * title : HardwareEntity
 *
 * description : builder 패턴 사용
 *
 * reference : [Spring Jpa] @Builder 사용법 - https://aamoos.tistory.com/687
 *             [JPA] column default value 넣기 - https://gksdudrb922.tistory.com/279
 *
 * author : jshong
 *
 * date : 2023-12-11
 **/

@Getter
@Entity(name = "HARDWARE")
@DynamicInsert
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class HardwareEntity {
    @Id
    @Column(name = "HW_NO", length = 50, unique = true, nullable = false)
    private String hwNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "HW_DIV")
    private Code hwDiv;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "HW_CATEGORY")
    private Code hwCategory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "HW_LOCATION")
    private Code hwLocation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "HW_CPU")
    private Code hwCpu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "HW_MFR")
    private Code hwMfr;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "HW_USAGE")
    private Code hwUsage;

    @Column(name = "PRODUCE_DATE")
    private Timestamp produceDate;

    @Column(name = "HW_SSD1")
    private Integer hwSsd1;

    @Column(name = "HW_SSD2")
    private Integer hwSsd2;

    @Column(name = "HW_HDD1")
    private Integer hwHdd1;

    @Column(name = "HW_HDD2")
    private Integer hwHdd2;

    @Column(name = "HW_RAM1")
    private Integer hwRam1;

    @Column(name = "HW_RAM2")
    private Integer hwRam2;

    @Column(name = "HW_MODEL", length = 30, nullable = false)
    private String hwModel;

    @Column(name = "HW_REMARKS", length = 100)
    private String hwRemarks;

    @Column(name = "PURCHASE_DATE")
    private Timestamp purchaseDate;

    @Column(name = "HW_SN", length = 20, nullable = false)
    private String hwSN;

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
    @Column(name = "OLD_YN", length = 1)
    private Boolean oldYn;

    @ColumnDefault(CodeAs.NEGATIVE)
    @Column(name = "DELETE_YN", length = 1)
    private Boolean deleteYn;

    @ColumnDefault(CodeAs.NEGATIVE)
    @Column(name = "FAILURE_YN", length = 1)
    private Boolean failureYn;

    @Builder
    public HardwareEntity(String hwNo, Code hwDiv, Code hwCategory, Code hwLocation, Timestamp produceDate, Code hwCpu, Integer hwSsd1,
                          Integer hwSsd2, Integer hwHdd1, Integer hwHdd2, Integer hwRam1,
                          Integer hwRam2, String hwModel, Code hwMfr, Code hwUsage, String hwRemarks,
                          Timestamp purchaseDate, String hwSN, LocalDateTime createDate, LocalDateTime updateDate,
                          String createId, String updateId, Boolean oldYn, Boolean deleteYn, Boolean failureYn) {
        this.hwNo = hwNo;
        this.hwDiv = hwDiv;
        this.hwCategory = hwCategory;
        this.hwLocation = hwLocation;
        this.produceDate = produceDate;
        this.hwCpu = hwCpu;
        this.hwSsd1 = hwSsd1;
        this.hwSsd2 = hwSsd2;
        this.hwHdd1 = hwHdd1;
        this.hwHdd2 = hwHdd2;
        this.hwRam1 = hwRam1;
        this.hwRam2 = hwRam2;
        this.hwModel = hwModel;
        this.hwMfr = hwMfr;
        this.hwUsage = hwUsage;
        this.hwRemarks = hwRemarks;
        this.purchaseDate = purchaseDate;
        this.hwSN = hwSN;
        this.createDate = createDate;
        this.updateDate = updateDate;
        this.createId = createId;
        this.updateId = updateId;
        this.oldYn = oldYn;
        this.deleteYn = deleteYn;
        this.failureYn = failureYn;
    }

    // HW 자산 삭제 시 deleteYn N -> Y
    public void deleteHardware(Boolean deleteYn) {
        this.deleteYn = deleteYn;
    }

    // HW 자산 노후화(4년) 시 oldYn N -> Y
    public void oldHardware(Boolean oldYn) {
        this.oldYn = oldYn;
    }
}
