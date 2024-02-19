package com.mcnc.assetmgmt.asset.repository;

import com.mcnc.assetmgmt.asset.entity.HardwareEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * title : HardwareRepository
 *
 * description :
 *
 * reference :
 *
 * author : jshong
 *
 * date : 2023-12-05
 **/

@Repository
public interface HardwareRepository extends JpaRepository<HardwareEntity, String> {
    HardwareEntity save(HardwareEntity hardwareEntity);
    List<HardwareEntity> findByDeleteYnOrderByCreateDateDesc(boolean deleteYn);
    HardwareEntity findByHwNo(String hwNo);
    List<HardwareEntity> findByOldYn(boolean oldYn);
}
