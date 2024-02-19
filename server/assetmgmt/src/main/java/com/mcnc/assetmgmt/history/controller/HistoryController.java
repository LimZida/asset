package com.mcnc.assetmgmt.history.controller;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mcnc.assetmgmt.assignment.service.AssignmentService;
import com.mcnc.assetmgmt.history.service.HistoryService;
import com.mcnc.assetmgmt.util.common.CodeAs;
import com.mcnc.assetmgmt.util.common.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
/**
 * title : HistoryController
 *
 * description :

 * reference :
 *
 * author : 임채성
 *
 * date : 2023.11.24
 **/
@RestController
@RequestMapping("/mcnc-mgmts/history")
@Slf4j
@RequiredArgsConstructor
public class HistoryController {
    private final HistoryService historyService;

    // 할당 코드, 이름 불러오기
    @GetMapping("/analytics")
    public ResponseEntity<Object> getAssetNameAndCode() {
        try{
            Map assetList = historyService.getAssetNameAndCodeService();
            return Response.makeSuccessResponse(HttpStatus.OK , assetList);
        }
        catch (Exception e){
            throw Response.makeFailResponse(CodeAs.HISTORY_ASSET_ERROR_CODE,HttpStatus.INTERNAL_SERVER_ERROR
                    ,CodeAs.HISTORY_ASSET_ERROR_MESSAGE,e);
        }
    }

    // 전체 자원 할당 히스토리 리스트 불러오기
    @PostMapping("/history-total")
    public ResponseEntity<Object> assetHistory(@RequestBody ObjectNode assignAssetHistoryObj) {
        try{
            Map historyList = historyService.getHistoryList(assignAssetHistoryObj);
            return Response.makeSuccessResponse(HttpStatus.OK , historyList);
        }
        catch (Exception e){
            throw Response.makeFailResponse(CodeAs.HISTORY_VIEW_FAIL_CODE,HttpStatus.INTERNAL_SERVER_ERROR
                    ,CodeAs.HISTORY_VIEW_FAIL_MESSAGE,e);
        }
    }
    
}
