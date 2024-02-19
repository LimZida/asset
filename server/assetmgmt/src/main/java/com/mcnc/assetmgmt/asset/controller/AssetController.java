package com.mcnc.assetmgmt.asset.controller;

import com.mcnc.assetmgmt.asset.dto.AssetCodeDto;
import com.mcnc.assetmgmt.asset.dto.HardwareDto;
import com.mcnc.assetmgmt.asset.dto.SoftwareDto;
import com.mcnc.assetmgmt.asset.service.HardwareService;
import com.mcnc.assetmgmt.asset.service.SoftwareService;
import com.mcnc.assetmgmt.util.common.CodeAs;
import com.mcnc.assetmgmt.util.common.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * title : AssetController
 *
 * description : Hw,Sw CRUD 컨트롤러
 *
 * reference :
 *
 * author : hylim
 *
 * date : 2023-12-06
 **/
@RequestMapping("/mcnc-mgmts/assets")
@Slf4j
@RequiredArgsConstructor
@RestController
public class AssetController extends CodeAs {
    private final HardwareService hardwareService;
    private final SoftwareService softwareService;

    //HW 코드 조회
    @GetMapping("/hardwares/codes")
    public ResponseEntity<Object> getHwAssetCode() {
        try {
            AssetCodeDto.assetHwCodeInfo codeList = hardwareService.getHwAssetCodeService();
//            log.info("##### 응답 {}", codeList.toString());

            return Response.makeSuccessResponse(HttpStatus.OK, codeList);
        } catch (Exception e){
            throw Response.makeFailResponse(CODE_SELECT_CATEGORY_FAIL_CODE, HttpStatus.INTERNAL_SERVER_ERROR, CODE_SELECT_CATEGORY_FAIL_MESSAGE, e);
        }
    }

    //HW 자산 조회
    @GetMapping("/hardwares")
    public ResponseEntity<Object> getHwAssetList() {
        try {
            List<HardwareDto.hwInfo> assetList = hardwareService.getHwAssetListService();
//            log.info("##### 응답 {}", assetList.toString());

            return Response.makeSuccessResponse(HttpStatus.OK, assetList);
        } catch (Exception e) {
            throw Response.makeFailResponse(CODE_SELECT_CATEGORY_FAIL_CODE, HttpStatus.INTERNAL_SERVER_ERROR, CODE_SELECT_CATEGORY_FAIL_MESSAGE, e);
        }
    }

    //HW 자산 등록
    @PostMapping("/hardwares")
    public ResponseEntity<Object> createHwAsset(@RequestBody HardwareDto.hwInsertInfo insertInfo) {
        try {
            log.info("##### 요청 {}", insertInfo.toString());
            HardwareDto.resultInfo result = hardwareService.insertHwAssetService(insertInfo);
            log.info("##### 응답 {}", result.toString());

            return Response.makeSuccessResponse(HttpStatus.OK, result);
        } catch (Exception e) {
            throw Response.makeFailResponse(CODE_SELECT_CATEGORY_FAIL_CODE, HttpStatus.INTERNAL_SERVER_ERROR, CODE_SELECT_CATEGORY_FAIL_MESSAGE, e);
        }
    }

    //HW 자산 수정
    @PutMapping("/hardwares")
    public ResponseEntity<Object> updateHwAsset(@RequestBody HardwareDto.hwInfo hwInfo) {
        try {
            log.info("##### 요청 {}", hwInfo.toString());
            HardwareDto.resultInfo result = hardwareService.updateHwAssetService(hwInfo);
            log.info("##### 응답 {}", result.toString());

            return Response.makeSuccessResponse(HttpStatus.OK, result);
        }  catch (Exception e) {
            throw Response.makeFailResponse(CODE_SELECT_CATEGORY_FAIL_CODE, HttpStatus.INTERNAL_SERVER_ERROR, CODE_SELECT_CATEGORY_FAIL_MESSAGE, e);
        }
    }

    //HW 자산 삭제
    @DeleteMapping("/hardwares")
    public ResponseEntity<Object> deleteHwAsset(@RequestBody HardwareDto.assetNoListInfo assetNoListInfo) {
        try {
            log.info("##### 요청 {}", assetNoListInfo.toString());
            assetNoListInfo.validate();

            HardwareDto.resultInfo result = hardwareService.deleteHwAssetService(assetNoListInfo);
            log.info("##### 응답 {}", result.toString());

            return Response.makeSuccessResponse(HttpStatus.OK, result);
        } catch (Exception e) {
            throw Response.makeFailResponse(CODE_SELECT_CATEGORY_FAIL_CODE, HttpStatus.INTERNAL_SERVER_ERROR, CODE_SELECT_CATEGORY_FAIL_MESSAGE, e);
        }
    }

    //SW 코드 조회
    @GetMapping("/softwares/codes")
    public ResponseEntity<Object> getSwAssetCode() {
        try {
            AssetCodeDto.assetSwCodeInfo codeList = softwareService.getSwAssetCodeService();
//            log.info("##### 응답 {}", codeList.toString());

            return Response.makeSuccessResponse(HttpStatus.OK, codeList);
        } catch (Exception e) {
            throw Response.makeFailResponse(CODE_SELECT_CATEGORY_FAIL_CODE, HttpStatus.INTERNAL_SERVER_ERROR, CODE_SELECT_CATEGORY_FAIL_MESSAGE, e);
        }
    }

    //SW 자산 조회
    @GetMapping("/softwares")
    public ResponseEntity<Object> getSwAssetList() {
        try {
            List<SoftwareDto.swInfo> assetList = softwareService.getSwAssetListService();
//            log.info("##### 응답 {}", assetList.toString());

            return Response.makeSuccessResponse(HttpStatus.OK, assetList);
        } catch (Exception e) {
            throw Response.makeFailResponse(CODE_SELECT_CATEGORY_FAIL_CODE, HttpStatus.INTERNAL_SERVER_ERROR, CODE_SELECT_CATEGORY_FAIL_MESSAGE, e);
        }
    }

    //SW 자산 등록
    @PostMapping("/softwares")
    public ResponseEntity<Object> createSwAsset(@RequestBody SoftwareDto.swInsertInfo swInsertInfo) {
        try {
            log.info("##### 요청 {}", swInsertInfo.toString());
            SoftwareDto.resultInfo result = softwareService.insertSwAssetService(swInsertInfo);
            log.info("##### 응답 {}", result.toString());

            return Response.makeSuccessResponse(HttpStatus.OK, result);
        } catch (Exception e) {
            throw Response.makeFailResponse(CODE_SELECT_CATEGORY_FAIL_CODE, HttpStatus.INTERNAL_SERVER_ERROR, CODE_SELECT_CATEGORY_FAIL_MESSAGE, e);
        }
    }


    //SW 자산 수정
    @PutMapping("/softwares")
    public ResponseEntity<Object> updateSwAsset(@RequestBody SoftwareDto.swInfo swInfo) {
        try {
            log.info("##### 요청 {}", swInfo.toString());
            SoftwareDto.resultInfo result = softwareService.updateSwAssetService(swInfo);
            log.info("##### 응답 {}", result.toString());

            return Response.makeSuccessResponse(HttpStatus.OK, result);
        } catch (Exception e) {
            throw Response.makeFailResponse(CODE_SELECT_CATEGORY_FAIL_CODE, HttpStatus.INTERNAL_SERVER_ERROR, CODE_SELECT_CATEGORY_FAIL_MESSAGE, e);
        }
    }

    //SW 자산 삭제
    @DeleteMapping("/softwares")
    public ResponseEntity<Object> deleteSwAsset(@RequestBody SoftwareDto.assetNoListInfo assetNoListInfo) {
        try {
            log.info("##### 요청 {}", assetNoListInfo.toString());
            assetNoListInfo.validate();

            SoftwareDto.resultInfo result = softwareService.deleteSwAssetService(assetNoListInfo);
            log.info("##### 응답 {}", result.toString());

            return Response.makeSuccessResponse(HttpStatus.OK, result);
        } catch (Exception e) {
            throw Response.makeFailResponse(CODE_SELECT_CATEGORY_FAIL_CODE, HttpStatus.INTERNAL_SERVER_ERROR, CODE_SELECT_CATEGORY_FAIL_MESSAGE, e);
        }
    }
}
