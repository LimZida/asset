package com.mcnc.assetmgmt.history.repository;

import com.mcnc.assetmgmt.history.entity.HistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
/**
 * title : HistoryRepository
 *
 * description : 히스토리 Repository

 * reference :
 *
 * author : 임채성
 *
 * date : 2023.11.24
 **/
@Repository
public interface HistoryRepository extends JpaRepository<HistoryEntity, Long> {
    List<HistoryEntity> findAll();
    List<HistoryEntity> findByAssetNoOrderByCreateDateDesc(String assetNo);
    int countByAssetNo (String assetNo);
}
