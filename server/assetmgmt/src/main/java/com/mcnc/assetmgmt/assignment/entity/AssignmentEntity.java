package com.mcnc.assetmgmt.assignment.entity;

import com.mcnc.assetmgmt.code.entity.Code;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * title : AssignmentEntity
 *
 * description : asset_assignment 테이블 매핑 Entity

 * reference :
 *
 * author : 임채성
 *
 * date : 23.11.24
 **/
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "asset_assignment")
public class AssignmentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USAGE_SEQ")
    private Long id;

    @Column(name = "ASSET_NO")
    private String assetNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ASSET_STATUS")
    private Code assetStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USAGE_DEPT")
    private Code usageDept;

    @Column(name = "USAGE_NAME")
    private String usageName;

    @Column(name = "USAGE_ID")
    private String usageId;

    @Column(name = "CREATE_ID")
    private String createId;

    @CreationTimestamp
    @Column(name = "CREATE_DATE", updatable = false)
    private LocalDateTime createDate;

    @Column(name = "UPDATE_ID")
    private String updateId;

    @UpdateTimestamp
    @Column(name = "UPDATE_DATE")
    private LocalDateTime updateDate;

    @Column(name = "DELETE_YN")
    private String deleteYn;

    public void updateUsageDept(Code usageDept) { this.usageDept=usageDept; }
    public void updateUsageName(String usageName) { this.usageName =usageName; }

    @Builder
    public AssignmentEntity(Long id, String assetNo, Code assetStatus, Code usageDept, String usageName, String usageId,
                            String createId, String updateId, String deleteYn) {
        this.id = id;
        this.assetNo = assetNo;
        this.assetStatus = assetStatus;
        this.usageDept = usageDept;
        this.usageName = usageName;
        this.usageId = usageId;
        this.createId = createId;
        this.updateId = updateId;
        this.deleteYn = deleteYn;
    }
}
