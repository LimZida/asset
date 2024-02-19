package com.mcnc.assetmgmt.assignment.controller;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mcnc.assetmgmt.assignment.service.AssignmentService;
import com.mcnc.assetmgmt.code.entity.Code;
import com.mcnc.assetmgmt.history.dto.HistoryDto;
import com.mcnc.assetmgmt.util.common.CodeAs;
import com.mcnc.assetmgmt.util.common.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * title : AssignmentController
 *
 * description :

 * reference :
 *
 * author : 임채성
 *
 * date : 2023.11.24
 **/
@RestController
@RequestMapping("/mcnc-mgmts")
@Slf4j
public class AssignmentController {
    private AssignmentService assignmentService;
    public AssignmentController(AssignmentService assignmentService) {
        this.assignmentService = assignmentService;
    }
    // 자원 할당
    @PostMapping("/assign")
    public ResponseEntity<Object> assignAsset(@RequestBody ObjectNode assignAssetObj) {
        try{
            // 할당
            Boolean result = assignmentService.saveAssetHistory(assignAssetObj);
            return Response.makeSuccessResponse(HttpStatus.OK,result);
        }catch (Exception e){
            throw Response.makeFailResponse(CodeAs.ASSET_ALLOCATE_FAIL_CODE, HttpStatus.INTERNAL_SERVER_ERROR
                    ,CodeAs.ASSET_ALLOCATE_FAIL_MESSAGE , e);
        }
    }

    // 개별 자원 할당 히스토리 리스트 불러오기
    @GetMapping("/assign-history")
    public ResponseEntity<Object> getAssetHistory(@RequestParam String assetNo) {
        try{
            Map assignHistoryList = assignmentService.getAssignHistoryList(assetNo);
            return Response.makeSuccessResponse(HttpStatus.OK , assignHistoryList);
        }catch (Exception e){
            throw Response.makeFailResponse(CodeAs.ASSET_ALLOCATE_HISTORY_FAIL_CODE, HttpStatus.INTERNAL_SERVER_ERROR
                    , CodeAs.ASSET_ALLOCATE_HISTORY_FAIL_MESSAGE, e);
        }
    }
}
