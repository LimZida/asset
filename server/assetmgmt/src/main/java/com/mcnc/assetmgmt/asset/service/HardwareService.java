package com.mcnc.assetmgmt.asset.service;

import com.mcnc.assetmgmt.asset.dto.AssetCodeDto;
import com.mcnc.assetmgmt.asset.dto.HardwareDto;

import java.util.List;

/**
 * title : HardwareService
 *
 * description : HardwareRepository,HardwareDto 매핑용 HardwareService 인터페이스
 *
 * reference :
 *
 * author : 임현영
 * date : 2023.12.05
 **/
public interface HardwareService {
    // HW 자산 조회
    List<HardwareDto.hwInfo> getHwAssetListService();
    // HW 자산 삭제
    HardwareDto.resultInfo deleteHwAssetService(HardwareDto.assetNoListInfo assetNoListInfo);
    // HW 자산 등록
    HardwareDto.resultInfo insertHwAssetService(HardwareDto.hwInsertInfo insertInfo);
    // HW 자산 등록 및 수정
    HardwareDto.resultInfo updateHwAssetService(HardwareDto.hwInfo hwInfo);
    // HW 자산 코드 조회
    AssetCodeDto.assetHwCodeInfo getHwAssetCodeService();
    // 자산 노후화 여부
    void oldHardware();
}
