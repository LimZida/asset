package com.mcnc.assetmgmt.dashboard.service;

import com.mcnc.assetmgmt.dashboard.dto.DashboardDto;

import java.util.List;

/**
 * title : DashboardService
 *
 * description : DashboardRepository,DashboardDto 매핑용 DashboardService 인터페이스
 *
 * reference :
 *
 * author : 임현영
 * date : 2023.11.08
 **/
public interface DashboardService {
    // 현재 보유하고 있는 자산코드와 이름
    DashboardDto.assetMapInfo getAssetCodeAndNameService();
    // 요청받은 날짜 이후부터 현재까지의 반입반출 기록
    List<DashboardDto.assetInfo> getInOutHistoryService(DashboardDto.rowInfo rowInfo);
    // HW의 총개수, 할당, 잉여, 고장, 노후 등 종합 상세 내역
    List<DashboardDto.hwListInfo> getHwDetailAssetListService();
    // SW의 총개수, 할당, 잉여, 고장, 노후 등 종합 상세 내역
    List<DashboardDto.swListInfo> getSwDetailAssetListService();
}
