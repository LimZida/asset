package com.mcnc.assetmgmt.excel.controller;

import com.mcnc.assetmgmt.excel.dto.ExcelDto;
import com.mcnc.assetmgmt.excel.service.HardwareExcelService;
import com.mcnc.assetmgmt.excel.service.SoftwareExcelService;
import com.mcnc.assetmgmt.util.common.CodeAs;
import com.mcnc.assetmgmt.util.common.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

/**
 * title : ExcelController
 * description : hw / sw 자산 등록 및 수정 시 엑셀을 이용하는 기능
 *
 * reference : RESTful 설계 규칙 : https://gmlwjd9405.github.io/2018/09/21/rest-and-restful.html
 *                                https://dev-cool.tistory.com/32
 *
 *            @RequestBody, @ModelAttribute, @RequestParam의 차이 : https://mangkyu.tistory.com/72
 *
 * author : 임현영
 * date : 2023.11.21
 **/
@RestController
@RequiredArgsConstructor
@RequestMapping("/mcnc-mgmts/excels")
public class ExcelController {

    private final SoftwareExcelService softwareExcelService;
    private final HardwareExcelService hardwareExcelService;

    //SW 엑셀 파일 다운로드
    @GetMapping(value = "/sw-downloads")
    public void downloadSoftwareExcelFile(HttpServletResponse response) {
        try {
            //서비스로 위임
            softwareExcelService.downloadSoftwareExcelFileService(response);
        }catch (Exception e) {
            throw Response.makeFailResponse(CodeAs.ASSET_SW_EXCEL_DOWNLOAD_ERROR_CODE, HttpStatus.INTERNAL_SERVER_ERROR
                    ,CodeAs.ASSET_SW_EXCEL_DOWNLOAD_CONVERT_ERROR_MESSAGE , e);
        }
    }

    //SW 엑셀 파일 데이터 조회
    @PostMapping(value = "/sw-reads", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Object> readSoftWareExcelData(@ModelAttribute ExcelDto.excelInfo excelInfo) {
        try {
            excelInfo.validate();

            ExcelDto.resultInfo result = softwareExcelService.readAndInsertSwData(excelInfo);

            return Response.makeSuccessResponse(HttpStatus.OK , result);
        }catch (Exception e) {
            throw Response.makeFailResponse(CodeAs.ASSET_SW_EXCEL_ERROR_CODE, HttpStatus.INTERNAL_SERVER_ERROR
                    ,CodeAs.ASSET_SW_EXCEL_ERROR_MESSAGE , e);
        }
    }

    //HW 엑셀 파일 다운로드
    @GetMapping(value = "/hw-downloads")
    public void downloadHardwareExcelFile(HttpServletResponse response) {
        try {
            //서비스로 위임
            hardwareExcelService.downloadHardwareExcelFileService(response);
        }catch (Exception e) {
            throw Response.makeFailResponse(CodeAs.ASSET_HW_EXCEL_DOWNLOAD_ERROR_CODE, HttpStatus.INTERNAL_SERVER_ERROR
                    ,CodeAs.ASSET_HW_EXCEL_DOWNLOAD_CONVERT_ERROR_MESSAGE , e);
        }
    }

    //HW 엑셀 파일 조회
    @PostMapping(value = "/hw-reads", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Object> readHardwareExcelData(@ModelAttribute ExcelDto.excelInfo excelInfo) {
        try {
            excelInfo.validate();

            ExcelDto.resultInfo result = hardwareExcelService.readAndInsertHwData(excelInfo);

            return Response.makeSuccessResponse(HttpStatus.OK , result);
        }catch (Exception e) {
            throw Response.makeFailResponse(CodeAs.ASSET_HW_EXCEL_ERROR_CODE, HttpStatus.INTERNAL_SERVER_ERROR
                    ,CodeAs.ASSET_HW_EXCEL_ERROR_MESSAGE , e);
        }
    }
}