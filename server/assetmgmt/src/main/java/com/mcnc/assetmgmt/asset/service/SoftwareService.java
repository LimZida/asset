package com.mcnc.assetmgmt.asset.service;

import com.mcnc.assetmgmt.asset.dto.AssetCodeDto;
import com.mcnc.assetmgmt.asset.dto.SoftwareDto;
import org.springframework.http.ResponseEntity;

import java.util.List;

/**
 * title : SoftwareService
 *
 * description : SoftwareRepository,SoftwareDto 매핑용 SoftwareService 인터페이스
 *
 * reference :
 *
 * author : 임현영
 *
 * date : 2023.12.06
 **/
public interface SoftwareService {
    //Sw 자산 조회
    List<SoftwareDto.swInfo> getSwAssetListService();
    //Sw 자산 삭제
    SoftwareDto.resultInfo deleteSwAssetService(SoftwareDto.assetNoListInfo assetNoListInfo);
    //Sw 자산 추가 및 수정
    SoftwareDto.resultInfo insertSwAssetService(SoftwareDto.swInsertInfo swInsertInfo);
    SoftwareDto.resultInfo updateSwAssetService(SoftwareDto.swInfo swInfo);
    //Sw 자산 코드 조회
    AssetCodeDto.assetSwCodeInfo getSwAssetCodeService();
    //Sw 자산 기간 만료 여부
    void expireSoftware();
}
