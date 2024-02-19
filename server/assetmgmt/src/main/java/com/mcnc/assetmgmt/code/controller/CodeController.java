package com.mcnc.assetmgmt.code.controller;

import com.mcnc.assetmgmt.code.dto.CodeDto;
import com.mcnc.assetmgmt.code.service.CodeService;
import com.mcnc.assetmgmt.util.common.CodeAs;
import com.mcnc.assetmgmt.util.common.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * title : CodeController
 * description : 코드관리 기능에 대한 controller
 *
 * reference : RESTful 설계 규칙 : https://gmlwjd9405.github.io/2018/09/21/rest-and-restful.html
 *                                https://dev-cool.tistory.com/32
 *
 *             @RequestBody란? : https://dev-coco.tistory.com/95 , https://cheershennah.tistory.com/179
 *
 *             RequestParam을 DTO로 바로 받는방법  https://baessi.tistory.com/23
 *
 * author : 임현영
 * date : 2023.11.21
 **/
@RestController
@RequiredArgsConstructor //자동주입
@RequestMapping("/mcnc-mgmts/code-managements")
@Slf4j
public class CodeController {
    private final CodeService codeService;
    
    // 모든 카테고리 조회
    @GetMapping("/categorys")
    public ResponseEntity<Object> getAllCategory(){
        try{
            List<CodeDto.categoryInfo> result = codeService.getAllCategoryService();
            log.info("##### 응답 : {}",result.toString());

            return Response.makeSuccessResponse(HttpStatus.OK , result);
        }
        catch (Exception e){
            throw Response.makeFailResponse(CodeAs.CODE_CATEGORY_LIST_FAIL_CODE, HttpStatus.INTERNAL_SERVER_ERROR
                    ,CodeAs.CODE_CATEGORY_LIST_FAIL_MESSAGE , e);
        }
    }

    // 카테고리에 대해 중복체크를 진행
    @PostMapping("/categorys/validations")
    public ResponseEntity<Object> validateCategoryCode(@RequestBody CodeDto.validationCategoryCodeInfo validationCategoryCodeInfo) {
        try{
            log.info("##### 요청: {}", validationCategoryCodeInfo.toString());
            validationCategoryCodeInfo.validate();

            CodeDto.codeInfo code = codeService.validateCategoryCodeService(validationCategoryCodeInfo);
            log.info("##### 응답 : {}",code.toString());

            return Response.makeSuccessResponse(HttpStatus.OK, code);
        }
        catch (Exception e){
            throw Response.makeFailResponse(CodeAs.CODE_VALIDATE_FAIL_CODE, HttpStatus.INTERNAL_SERVER_ERROR
                    ,CodeAs.CODE_VALIDATE_FAIL_MESSAGE , e);
        }
    }

    // 카테고리의 코드타입에 대해 중복을 진행
    @PostMapping("/categorys/codetype-validations")
    public ResponseEntity<Object> validateCodeType(@RequestBody CodeDto.codeTypeInfo codeTypeInfo) {
        try{
            log.info("##### 요청: {}", codeTypeInfo.toString());
            codeTypeInfo.validate();

            CodeDto.codeTypeResultInfo result = codeService.validateCodeTypeService(codeTypeInfo);
            log.info("##### 응답 : {}",result.toString());

            return Response.makeSuccessResponse(HttpStatus.OK, result);
        }
        catch (Exception e){
            throw Response.makeFailResponse(CodeAs.CODE_VALIDATE_FAIL_CODE, HttpStatus.INTERNAL_SERVER_ERROR
                    ,CodeAs.CODE_VALIDATE_FAIL_MESSAGE , e);
        }
    }

    // 카테고리 추가
    @PostMapping("/categorys")
    public ResponseEntity<Object> createCategory(@RequestBody CodeDto.categoryCreateInfo categoryCreateInfo){
        try{
            log.info("##### 요청: {}",categoryCreateInfo.toString());
            categoryCreateInfo.validate();

//            List<CodeDto.categoryInfo> result = codeService.createCategoryService(categoryCreateInfo);
            CodeDto.resultInfo result = codeService.createCategoryService(categoryCreateInfo);
            log.info("##### 응답 : {}",result.toString());

            return Response.makeSuccessResponse(HttpStatus.OK , result);
        }
        catch (Exception e){
            throw Response.makeFailResponse(CodeAs.CODE_CATEGORY_ADD_FAIL_CODE, HttpStatus.INTERNAL_SERVER_ERROR
                    ,CodeAs.CODE_CATEGORY_ADD_FAIL_MESSAGE , e);
        }
    }

    // 카테고리 수정
    @PutMapping("/categorys")
    public ResponseEntity<Object> updateCategory(@RequestBody CodeDto.codeModifyInfo codeModifyInfo){
        try{
            log.info("##### 요청: {}",codeModifyInfo.toString());
            codeModifyInfo.validate();

            List<CodeDto.categoryInfo> result = codeService.updateCategoryService(codeModifyInfo);
            log.info("##### 응답 : {}",result.toString());

            return Response.makeSuccessResponse(HttpStatus.OK , result);
        }
        catch (Exception e){
            throw Response.makeFailResponse(CodeAs.CODE_CATEGORY_ADD_FAIL_CODE, HttpStatus.INTERNAL_SERVER_ERROR
                    ,CodeAs.CODE_CATEGORY_ADD_FAIL_MESSAGE , e);
        }
    }

    // 카테고리 활성화/비활성화
    @PutMapping("/categorys/activation")
    public ResponseEntity<Object> activateCategory(@RequestBody CodeDto.codeInfo codeInfo){
        try{
            log.info("##### 요청: {}",codeInfo.toString());
            codeInfo.validate();

            List<CodeDto.categoryInfo> result = codeService.activateCategoryService(codeInfo);
            log.info("##### 응답 : {}",result.toString());

            return Response.makeSuccessResponse(HttpStatus.OK , result);
        }
        catch (Exception e){
            throw Response.makeFailResponse(CodeAs.CODE_CATEGORY_ADD_FAIL_CODE, HttpStatus.INTERNAL_SERVER_ERROR
                    ,CodeAs.CODE_CATEGORY_ADD_FAIL_MESSAGE , e);
        }
    }

    // 특정 카테고리 내 속하는 코드 리스트 조회
    @GetMapping("")
    public ResponseEntity<Object> getCodeList(CodeDto.codeInfo codeInfo) {
        try{
            log.info("##### 요청: {}",codeInfo.toString());
            //요청값 누락 여부 판별
            codeInfo.validate();

            CodeDto.codeListMapInfo codeList = codeService.getCodeListService(codeInfo);
            log.info("##### 응답 : {}",codeList.toString());

            return Response.makeSuccessResponse(HttpStatus.OK , codeList);
        }
        catch (Exception e){
            throw Response.makeFailResponse(CodeAs.CODE_SELECT_CATEGORY_FAIL_CODE, HttpStatus.INTERNAL_SERVER_ERROR
                    ,CodeAs.CODE_SELECT_CATEGORY_FAIL_MESSAGE ,e);
        }
    }

    // 특정 카테고리 내 코드에 대해 중복체크를 진행합니다.
    @PostMapping("/validations")
    public ResponseEntity<Object> validateCode(@RequestBody CodeDto.validationCodeInfo validationCodeInfo) {
        try{
            log.info("##### 요청: {}", validationCodeInfo.toString());
            validationCodeInfo.validate();

            CodeDto.codeInfo code = codeService.validateCodeService(validationCodeInfo);
            log.info("##### 응답 : {}",code.toString());

            return Response.makeSuccessResponse(HttpStatus.OK, code);
        }
        catch (Exception e){
            throw Response.makeFailResponse(CodeAs.CODE_VALIDATE_FAIL_CODE, HttpStatus.INTERNAL_SERVER_ERROR
                    ,CodeAs.CODE_VALIDATE_FAIL_MESSAGE , e);
        }
    }

    // 특정 카테고리 내 속하는 코드 추가
    @PostMapping("")
    public ResponseEntity<Object> createCode(@RequestBody CodeDto.codeCreateInfo codeCreateInfo){
        try{
            log.info("##### 요청: {}",codeCreateInfo.toString());
            codeCreateInfo.validate();

            CodeDto.codeListMapInfo codeList = codeService.createCodeService(codeCreateInfo);
            log.info("##### 응답 : {}",codeList.toString());

            return Response.makeSuccessResponse(HttpStatus.OK, codeList);
        }

        catch (Exception e){
            throw Response.makeFailResponse(CodeAs.CODE_INSERT_CATEGORY_FAIL_CODE, HttpStatus.INTERNAL_SERVER_ERROR
                    ,CodeAs.CODE_INSERT_CATEGORY_FAIL_MESSAGE , e);
        }
    }

    // 특정 카테고리 내 코드의 코드명 혹은 비고를 수정
    @PutMapping("")
    public ResponseEntity<Object> updateCode(@RequestBody CodeDto.codeModifyInfo codeModifyInfo){
        try{
            log.info("##### 요청: {}",codeModifyInfo.toString());
            codeModifyInfo.validate();

            CodeDto.codeListMapInfo codeList = codeService.updateCodeService(codeModifyInfo);
            log.info("##### 응답 : {}",codeList.toString());
            return Response.makeSuccessResponse(HttpStatus.OK, codeList);
        }
        catch (Exception e){
            throw Response.makeFailResponse(CodeAs.CODE_INFO_MODIFY_FAIL_CODE, HttpStatus.INTERNAL_SERVER_ERROR
                    ,CodeAs.CODE_INFO_MODIFY_FAIL_MESSAGE , e);
        }
    }

    //특정 카테고리에 속하는 코드 비활성하거나 다시 되돌림 (삭제여부를 Y/N으로 업데이트)
    @PutMapping("/activation")
    public ResponseEntity<Object> activateCode(@RequestBody CodeDto.codeInfo codeInfo){
        try{
            log.info("##### 요청: {}",codeInfo.toString());
            //요청값 누락 여부 판별
            codeInfo.validate();

            CodeDto.codeListMapInfo codeList = codeService.activateCodeService(codeInfo);
            log.info("##### 응답 : {}",codeList.toString());
            return Response.makeSuccessResponse(HttpStatus.OK, codeList);
        }
        catch (Exception e){
            throw Response.makeFailResponse(CodeAs.CODE_DELETE_CATEGORY_FAIL_CODE, HttpStatus.INTERNAL_SERVER_ERROR
                    ,CodeAs.CODE_DELETE_CATEGORY_FAIL_MESSAGE , e);
        }
    }


}
