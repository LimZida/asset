package com.mcnc.assetmgmt.assignment.repository;

import com.mcnc.assetmgmt.code.entity.Code;
import org.springframework.data.jpa.repository.JpaRepository;
import com.mcnc.assetmgmt.assignment.entity.AssignmentEntity;

import java.util.List;
import java.util.Optional;

/**
 * title : AssignmentRepository
 *
 * description : 자원할당 Repository

 * reference :
 *
 * author : 임채성
 *
 * date : 23.11.24
 **/
public interface AssignmentRepository extends JpaRepository<AssignmentEntity, Long> {
    boolean existsByAssetNo(String assetNo);
    AssignmentEntity findByAssetNo(String assetNo);
    AssignmentEntity findByAssetNoAndDeleteYn(String assetNo, String deleteYn);
    List<AssignmentEntity> findByAssetNoAndDeleteYnAndAssetStatusOrderByCreateDateDesc(String assetNo, String deleteYn, Code assetStatus);
    List<AssignmentEntity> findByAssetNoAndDeleteYnOrderByCreateDateDesc(String assetNo, String deleteYn);
    AssignmentEntity findByAssetNoAndUsageId(String assetNo, String prevUserId);
    List<AssignmentEntity> findByAssetNoAndUsageIdOrderByCreateDateDesc(String assetNo, String prevUserId);
    int countByAssetNoAndAssetStatus (String assetNo, Code assetStatus);
    List<AssignmentEntity> findByUsageId(String usageId);
}
