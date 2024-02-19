package com.mcnc.assetmgmt.dashboard.controller;

import com.mcnc.assetmgmt.dashboard.dto.DashboardDto;
import com.mcnc.assetmgmt.dashboard.service.DashboardService;
import com.mcnc.assetmgmt.util.common.CodeAs;
import com.mcnc.assetmgmt.util.common.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * title : DashboardController
 * description : 대시보드 도표, 반입반출기록 기능 관련 컨트롤러
 *
 * reference : RESTful 설계 규칙 : https://gmlwjd9405.github.io/2018/09/21/rest-and-restful.html
 *                                https://dev-cool.tistory.com/32
 *
 *             @RequestBody란? : https://dev-coco.tistory.com/95 , https://cheershennah.tistory.com/179
 *
 *             RequestParam을 DTO로 바로 받는방법  https://baessi.tistory.com/23
 *
 * author : 임현영
 * date : 2023.11.14
 **/
@RestController
@RequiredArgsConstructor //자동주입
@RequestMapping("/mcnc-mgmts/dashboards/analytics")
@Slf4j
public class DashboardController {
    private final DashboardService dashboardService;

    // 현재 보유하고 있는 자산코드와 이름
    @GetMapping("")
    public ResponseEntity<Object> getAssetCodeAndName(){
        try{
            DashboardDto.assetMapInfo assetList = dashboardService.getAssetCodeAndNameService();
            log.info("##### 응답 : {}",assetList.toString());

            return Response.makeSuccessResponse(HttpStatus.OK , assetList);
        }
        catch (Exception e){
            throw Response.makeFailResponse(CodeAs.DASHBOARD_ASSET_ERROR_CODE, HttpStatus.INTERNAL_SERVER_ERROR
                    ,CodeAs.DASHBOARD_ASSET_ERROR_MESSAGE , e);
        }
    }

    // 요청받은 개수 이후부터 현재까지의 반입반출 기록
    @GetMapping("/in-outs")
    public ResponseEntity<Object> getInOutHistory(DashboardDto.rowInfo rowInfo){
        try{
            log.info("##### 요청: {}",rowInfo.toString());
            //요청 누락 필드 확인
            rowInfo.validate();

            List<DashboardDto.assetInfo> result = dashboardService.getInOutHistoryService(rowInfo);
            log.info("##### 응답 : {}",result.toString());

            return Response.makeSuccessResponse(HttpStatus.OK , result);
        }
        catch (Exception e){
            throw Response.makeFailResponse(CodeAs.DASHBOARD_IN_OUT_ERROR_CODE, HttpStatus.INTERNAL_SERVER_ERROR
                    ,CodeAs.DASHBOARD_IN_OUT_ERROR_MESSAGE , e);
        }
    }

    // HW의 총개수, 할당, 잉여, 고장, 노후 등 종합 상세 내역
    @GetMapping("/hardware-details")
    public ResponseEntity<Object> getHwDetailAssetList(){
        try{
            List<DashboardDto.hwListInfo> assetHwList = dashboardService.getHwDetailAssetListService();
            log.info("##### 응답 : {}",assetHwList.toString());
            return Response.makeSuccessResponse(HttpStatus.OK , assetHwList);
        }
        catch (Exception e){
            throw Response.makeFailResponse(CodeAs.DASHBOARD_DETAIL_ERROR_CODE, HttpStatus.INTERNAL_SERVER_ERROR
                    ,CodeAs.DASHBOARD_DETAIL_ERROR_MESSAGE , e);
        }
    }

    // SW의 총개수, 할당, 잉여, 고장, 노후 등 종합 상세 내역
    @GetMapping("/software-details")
    public ResponseEntity<Object> getSwDetailAssetList(){
        try{
            List<DashboardDto.swListInfo>  assetSwList = dashboardService.getSwDetailAssetListService();
            log.info("##### 응답 : {}",assetSwList.toString());
            
            return Response.makeSuccessResponse(HttpStatus.OK , assetSwList);
        }
        catch (Exception e){
            throw Response.makeFailResponse(CodeAs.DASHBOARD_DETAIL_ERROR_CODE, HttpStatus.INTERNAL_SERVER_ERROR
                    ,CodeAs.DASHBOARD_DETAIL_ERROR_MESSAGE , e);
        }
    }
}
