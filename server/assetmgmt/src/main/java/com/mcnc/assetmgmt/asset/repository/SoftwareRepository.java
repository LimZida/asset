package com.mcnc.assetmgmt.asset.repository;

import com.mcnc.assetmgmt.asset.entity.SoftwareEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * title : SoftwareRepository
 *
 * description :
 *
 * reference :
 *
 * author : jshong
 *
 * date : 2023-12-06
 **/

@Repository
public interface SoftwareRepository extends JpaRepository<SoftwareEntity, String> {
    SoftwareEntity save(SoftwareEntity softwareEntity);
    List<SoftwareEntity> findByDeleteYnOrderByCreateDateDesc(boolean deleteYn);
    SoftwareEntity findBySwNo(String swNo);
    List<SoftwareEntity> findByExpireYn(boolean expireYn);
}
