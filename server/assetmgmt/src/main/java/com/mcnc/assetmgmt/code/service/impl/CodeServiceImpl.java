package com.mcnc.assetmgmt.code.service.impl;

import com.mcnc.assetmgmt.code.dto.CodeDto;
import com.mcnc.assetmgmt.code.entity.Code;
import com.mcnc.assetmgmt.code.repository.CodeRepository;
import com.mcnc.assetmgmt.code.service.CodeService;
import com.mcnc.assetmgmt.util.common.CodeAs;
import com.mcnc.assetmgmt.util.exception.CustomException;
import com.mcnc.assetmgmt.util.common.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * title : CodeServiceImpl
 *
 * description : CodeService의 구현체, 코드관리 기능 로직
 *
 * reference :  올바른 엔티티 수정 방법: https://www.inflearn.com/questions/15944/%EA%B0%95%EC%9D%98-%EC%A4%91%EC%97%90-%EA%B0%92%ED%83%80%EC%9E%85%EC%9D%98-%EC%88%98%EC%A0%95%EC%97%90-%EB%8C%80%ED%95%B4%EC%84%9C-%EB%8B%A4%EB%A4%84%EC%A3%BC%EC%85%A8%EB%8A%94%EB%8D%B0%EC%9A%94-%EB%AC%B8%EB%93%9D-%EC%97%94%ED%8B%B0%ED%8B%B0-%EC%88%98%EC%A0%95%EC%97%90-%EB%8C%80%ED%95%B4%EC%84%9C-%EA%B6%81%EA%B8%88%ED%95%B4%EC%A0%B8-%EC%A7%88%EB%AC%B8%EB%93%9C%EB%A6%BD%EB%8B%88%EB%8B%A4
 *
 *              DTO의 적용 범위 https://tecoble.techcourse.co.kr/post/2021-04-25-dto-layer-scope/
 *              더티체킹 : https://jojoldu.tistory.com/415
 *
 *
 * author : 임현영
 * date : 2023.11.21
 **/
@Service
@RequiredArgsConstructor // 자동주입
@Slf4j
public class CodeServiceImpl implements CodeService {
    private final CodeRepository codeRepository;


    @Override
    @Transactional(readOnly = true)
    public List<CodeDto.categoryInfo> getAllCategoryService() {
        /*
         * 1. 코드 타입이 카테고리(CTG)에 해당하는 코드를 조회한다.
         * 2. 코드 Entity와 응답 DTO를 매핑한다.
         * 3. 배열로 담아 응답한다.
         * */
        try {
            // 카테고리(CTG)에 해당하는 코드 조회 후 리스트에 담아 응답
            List<CodeDto.categoryInfo> res = mapCodeListToCategoryInfo();
            return res;
        }
        catch(Exception e){
            throw Response.makeFailResponse(CodeAs.CODE_CATEGORY_LIST_FAIL_CODE,
                    HttpStatus.INTERNAL_SERVER_ERROR, CodeAs.CODE_CATEGORY_LIST_FAIL_MESSAGE , e);
        }
    }

    @Override
    @Transactional
    public CodeDto.resultInfo createCategoryService(CodeDto.categoryCreateInfo categoryCreateInfo) {
        /*
         * 1. 추가된 정보를 포함한 카테고리의 Code List 결과를 응답합니다.
         * */
        try {
            Code mappedCode = mapCategoryCreateInfoToCode(categoryCreateInfo);
            //새로 만들어진 카테고리 코드 저장
            Code saveCodeResult = saveCode(mappedCode);
            if(saveCodeResult == null){
                throw new CustomException(CodeAs.CODE_CATEGORY_ADD_FAIL_CODE,
                        HttpStatus.INTERNAL_SERVER_ERROR, CodeAs.CODE_CATEGORY_ADD_FAIL_MESSAGE , null);
            }

            //카테고리 추가 시 codeType이 있어야만 작업(맨 하위단 카테고리가 아닌 경우에 temp data가 들어가는 것을 막기 위함)
            if(StringUtils.hasText(categoryCreateInfo.getCodeType())){
                //CodeInfo Dto 매핑
                CodeDto.codeInfo codeInfo = validateCodeService(mapCodeValidationDTO(categoryCreateInfo));

                //DTO와 Entity 매핑
                Code code = mapCodeCreateInfoToCode(mapCodeCreateDTO(codeInfo,categoryCreateInfo));
                //해당 코드 저장
                saveCode(code);
            }

            // 변경되거나 추가된 카테고리(CTG)에 해당하는 코드 조회 후 리스트에 담아 응답
//            List<CodeDto.categoryInfo> res = mapCodeListToCategoryInfo();

            // 변경되거나 추가된 카테고리(CTG)에 해당하는 코드 진행 후 결과 응답
            CodeDto.resultInfo res = CodeDto.resultInfo.builder()
                    .result(true)
                    .build();

            return res;
        }
        catch (Exception e) {
            throw Response.makeFailResponse(CodeAs.CODE_CATEGORY_ADD_FAIL_CODE,
                    HttpStatus.INTERNAL_SERVER_ERROR, CodeAs.CODE_CATEGORY_ADD_FAIL_MESSAGE , e);
        }
    }

    //Entity <=> DTO 매핑함수
    private CodeDto.validationCodeInfo mapCodeValidationDTO(CodeDto.categoryCreateInfo categoryCreateInfo){
        //카테고리 내 codeType을 포함한 임시 값 저장
        return CodeDto.validationCodeInfo.builder()
                .codeName(CodeAs.TEMP)
                .codeType(categoryCreateInfo.getCodeType())
                .build();
    }

    //Entity <=> DTO 매핑함수
    private CodeDto.codeCreateInfo mapCodeCreateDTO(CodeDto.codeInfo codeInfo, CodeDto.categoryCreateInfo categoryCreateInfo){
        return CodeDto.codeCreateInfo.builder()
                .code(codeInfo.getCode())
                .codeCtg(categoryCreateInfo.getCode())
                .codeName(CodeAs.TEMP)
                .codeRemark(CodeAs.BLANK)
                .codeType(categoryCreateInfo.getCodeType())
                .build();
    }

    //Entity <=> DTO 매핑함수
    private Code mapCategoryCreateInfoToCode(CodeDto.categoryCreateInfo categoryCreateInfo) {
        return Code.builder()
                .code(categoryCreateInfo.getCode())
                .codeRemark(CodeAs.BLANK)
                .codeName(Response.getOrDefaultString(categoryCreateInfo.getCodeName()))
                .upperCode(categoryCreateInfo.getUpperCode())
                .codeDepth(categoryCreateInfo.getCodeDepth())
                .codeType(CodeAs.CATEGORY)
                .codeCtg(CodeAs.BLANK)
                .activeYn(true)
                .createId(CodeAs.ADMIN)
                .updateId(CodeAs.ADMIN)
                .build();
    }


    @Override
    @Transactional(readOnly = true)
    public CodeDto.codeTypeResultInfo validateCodeTypeService(CodeDto.codeTypeInfo codeTypeInfo) {
        /*
         * 1. 요청받은 코드 타입에 대해 중복여부를 확인합니다.
         * 2-1. 코드 타입이 있으면 DUP
         * 2-2. 코드 타입이 없으면 nonDup
         * 3. 해당 결과를 응답합니다.
         * */
        CodeDto.codeTypeResultInfo res = CodeDto.codeTypeResultInfo.builder().build();
        try{
            //코드타입 조회
            List<Code> codeList = codeRepository.findByCodeType(codeTypeInfo.getCodeType());
            if(codeList == null){
                throw new CustomException(CodeAs.CODE_SELECT_TYPE_NAME_FAIL_CODE,
                        HttpStatus.INTERNAL_SERVER_ERROR, CodeAs.CODE_SELECT_TYPE_NAME_FAIL_MESSAGE , null);
            }

            //요청 코드타입이 없으면 - 중복 X
            if(codeList.isEmpty()){
                res = CodeDto.codeTypeResultInfo.builder()
                        .result(CodeAs.NON_DUPLICATE)
                        .build();
            }
            //요청 코드타입이 있으면 - 중복 O
            else{
                res = CodeDto.codeTypeResultInfo.builder()
                        .result(CodeAs.DUPLICATE)
                        .build();
            }

            return res;
        }catch (Exception e){
            throw Response.makeFailResponse(CodeAs.CODE_CATEGORY_ADD_FAIL_CODE,
                    HttpStatus.INTERNAL_SERVER_ERROR, CodeAs.CODE_CATEGORY_ADD_FAIL_MESSAGE , e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public CodeDto.codeInfo validateCategoryCodeService(CodeDto.validationCategoryCodeInfo validationCategoryCodeInfo) {
        List<Code> typeList = new ArrayList<>();
        List<Code> nameList = new ArrayList<>();
        CodeDto.codeInfo res = CodeDto.codeInfo.builder().build();
        /*
         * 1. 요청받은 UpperCode를 가지고 있는 코드 List를 확인 후, 중복된 이름이 있는지 검증한다.
         *
         * 중복된 이름이 없는 경우)
         * 2-1. 가장 하위 단에서 카테고리를 만들 경우(UpperCode가 없는 경우), 신생 PK를 만든다.
         * 2-2. 상위나 중간 단에서 카테고리를 만들 경우(UpperCode가 있는 경우 ), 가장 마지막에 있는 코드의 PK+1을 진행해 가져온다.
         *
         * 중복된 이름이 있는 경우)
         * 2-3. PK를 빈값으로 설정한다.
         *
         * 3. 해당 결과에 대한 PK를 응답한다.
         * */
        try {
            //상위 코드
            String upperCode = validationCategoryCodeInfo.getUpperCode();
            //상위 코드에 대한 하위코드 조회
            typeList = codeRepository.findByUpperCode(upperCode);
            if (typeList == null) {
                throw new CustomException(CodeAs.CODE_UPPER_SELECT_LIST_FAIL_CODE,
                        HttpStatus.INTERNAL_SERVER_ERROR, CodeAs.CODE_UPPER_SELECT_LIST_FAIL_MESSAGE , null);
            }
            nameList = codeRepository.findByUpperCodeAndCodeNameContaining(validationCategoryCodeInfo.getUpperCode(),
                    makeTrim(validationCategoryCodeInfo.getCodeName()));
            if(typeList == null || nameList == null){
                throw new CustomException(CodeAs.CODE_SELECT_CATEGORY_FAIL_CODE,
                        HttpStatus.INTERNAL_SERVER_ERROR, CodeAs.CODE_SELECT_CATEGORY_FAIL_MESSAGE , null);
            }

            log.info("typeList size : {} data : {} ",typeList.size(),typeList);
            log.info("nameList size : {} data : {} ",nameList.size(),nameList);

            if(nameList.isEmpty()){
                //PK를 만드는 함수
                String PK = buildCode(upperCode, typeList);
                res = CodeDto.codeInfo.builder()
                        .code(PK)
                        .build();
            } else{
                res = CodeDto.codeInfo.builder()
                        .code(CodeAs.BLANK)
                        .build();
            }

            return res;

        }catch (Exception e){
            throw Response.makeFailResponse(CodeAs.CODE_VALIDATE_FAIL_CODE,
                    HttpStatus.INTERNAL_SERVER_ERROR,CodeAs.CODE_VALIDATE_FAIL_MESSAGE , e);
        }
    }


    // PK코드를 분기에 따라 만드는 함수
    private String buildCode(String upperCode, List<Code> typeList) {
        if (typeList.isEmpty()) {
            return upperCode + CodeAs.PK_FIRST;
        } else {
            return makePK(typeList);
        }
    }

    @Override
    @Transactional
    public List<CodeDto.categoryInfo> updateCategoryService(CodeDto.codeModifyInfo codeModifyInfo) {
        /*
         * 1. Code PK를 통해 카테고리 코드를 조회한다.
         * 2. 더티체킹을 통해 update를 진행 후 응답한다.
         * */
        try{
            // 코드 조회
            Code code = codeRepository.findByCode(codeModifyInfo.getCode());
            if(code == null){
                throw new CustomException(CodeAs.CODE_SELECT_PK_FAIL_CODE,
                        HttpStatus.INTERNAL_SERVER_ERROR,CodeAs.CODE_SELECT_PK_FAIL_MESSAGE , null);
            }

            // 요청받은 정보들(코드 이름, 비고) 수정 진행
            updateCodeFields(code, codeModifyInfo , CodeAs.CATEGORY);

            // 수정 후 바뀐 상태가 포함된 전체 코드 리스트 응답
            List<CodeDto.categoryInfo> res = mapCodeListToCategoryInfo();

            return res;
        }catch (Exception e){
            throw Response.makeFailResponse(CodeAs.CODE_CATEGORY_UPDATE_FAIL_CODE,
                    HttpStatus.INTERNAL_SERVER_ERROR, CodeAs.CODE_CATEGORY_UPDATE_FAIL_MESSAGE , e);
        }
    }

    @Override
    @Transactional
    public List<CodeDto.categoryInfo> activateCategoryService(CodeDto.codeInfo codeInfo) {
        try {
            /*
             * 1. 요청에서 받아온 codeInfo를 통해 카테고리 코드를 조회한다.
             * 2. 코드의 활성여부에 따라 그에 반대로 업데이트 한다. (더티체킹을 통해 바로 수정 가능)
             * 3. 바뀐 결과와 전체 코드 리스트를 응답한다.
             * */

            // 코드 조회
            Code code = codeRepository.findByCode(codeInfo.getCode());
            if(code == null){
                throw new CustomException(CodeAs.CODE_SELECT_PK_FAIL_CODE,
                        HttpStatus.INTERNAL_SERVER_ERROR,CodeAs.CODE_SELECT_PK_FAIL_MESSAGE , null);
            }
            // 활성화 상태 변경 전
            boolean originalState = code.isActiveYn();
            log.info("변경 전 : {}",originalState);
            // 활성화 상태 변경 후
            Boolean newActiveYn = code.isActiveYn() == true ? false : true;
            code.updateActiveYn(newActiveYn);
            boolean changedState = code.isActiveYn();
            log.info("변경 후 : {}",changedState);

            // 수정이 안되었으면(더티체킹이 안되었으면)
            if ( originalState == changedState){
                throw new CustomException(CodeAs.USER_INFO_DELETE_FAIL_CODE,
                        HttpStatus.INTERNAL_SERVER_ERROR,CodeAs.USER_INFO_DELETE_FAIL_MESSAGE , null);
            }
            // 수정 후 바뀐 상태가 포함된 전체 코드 리스트 응답
            List<CodeDto.categoryInfo> res = mapCodeListToCategoryInfo();

            return res;
        }catch (Exception e){
            throw Response.makeFailResponse(CodeAs.CODE_CATEGORY_DELETE_FAIL_CODE,
                    HttpStatus.INTERNAL_SERVER_ERROR, CodeAs.CODE_CATEGORY_DELETE_FAIL_MESSAGE , e);
        }
    }

    // 코드 내 카테고리(CTG) 조회하는 함수
    private List<CodeDto.categoryInfo> mapCodeListToCategoryInfo(){
        List<Code> categoryList = codeRepository.findByCodeType(CodeAs.CATEGORY);
        if (categoryList == null || categoryList.isEmpty()) {
            throw new CustomException(CodeAs.CODE_CATEGORY_SELECT_LIST_FAIL_CODE,
                    HttpStatus.INTERNAL_SERVER_ERROR, CodeAs.CODE_CATEGORY_SELECT_LIST_FAIL_MESSAGE , null);
        }

        List<CodeDto.categoryInfo> res = mapCodeListToCategoryInfoList(categoryList);
        return res;
    }

    // 카테고리 리스트를 조회해 mapping을 호출한 뒤 배열에 넣어 응답하는 함수
    private List<CodeDto.categoryInfo> mapCodeListToCategoryInfoList(List<Code> categoryList) {
        List<CodeDto.categoryInfo> res = new ArrayList<>();

        for (Code category : categoryList) {
            CodeDto.categoryInfo categoryInfo = mapCodeToCategoryInfo(category);
            res.add(categoryInfo);
        }

        return res;
    }

    // Entity <=> DTO 매핑함수
    private CodeDto.categoryInfo mapCodeToCategoryInfo(Code category) {

        //해당 카데고리에 해당하는 코드리스트 조회 (카테고리 이름과 코드타입을 같이 보여주기 위함함)
       List<Code> codeList = codeRepository.findByCodeCtg(category.getCode());
        if(codeList == null){
            throw new CustomException(CodeAs.CODE_SELECT_CATEGORY_FAIL_CODE,
                    HttpStatus.INTERNAL_SERVER_ERROR, CodeAs.CODE_SELECT_CATEGORY_FAIL_MESSAGE , null);
        }

        //카테고리 하위단 코드에만 코드타입 붙여서 응답
        String codeInnerType = codeList.size()>0?
                "("+codeList.get(CodeAs.ZERO).getCodeType()+")" : CodeAs.BLANK;

        return CodeDto.categoryInfo.builder()
                .codeInnerType(Response.getOrDefaultString(codeInnerType))
                .code(Response.getOrDefaultString(category.getCode()))
                .codeDepth(Response.getOrDefaultInt(category.getCodeDepth()))
                .codeName(Response.getOrDefaultString(category.getCodeName()))
                .codeType(Response.getOrDefaultString(category.getCodeType()))
                .activeYn(true)
                .upperCode(Response.getOrDefaultString(category.getUpperCode()))
                .build();
    }

    @Override
    @Transactional
    public CodeDto.codeListMapInfo createCodeService(CodeDto.codeCreateInfo codeCreateInfo) {
        try{
            /*
             * 1. DTO와 Entity를 매핑한다.
             * 2. 매핑된 Entity를 저장 후 결과에 따라 해당 코드의 전체 리스트 혹은 빈값을 응답한다.
             *
             * */

            //DTO와 Entity 매핑
            Code code = mapCodeCreateInfoToCode(codeCreateInfo);
            //코드 저장
            Code saveCodeResult = saveCode(code);
            if(saveCodeResult == null){
                throw new CustomException(CodeAs.CODE_INSERT_CATEGORY_FAIL_CODE,
                        HttpStatus.INTERNAL_SERVER_ERROR,CodeAs.CODE_INSERT_CATEGORY_FAIL_MESSAGE , null);
            }

            //Entity와 DTO 매핑 후 List로 해당 카테고리의 전체 리스트 반환
            CodeDto.codeListMapInfo res = mapCodeToCodeListMap(saveCodeResult.getCodeCtg());

            return res;
        }
        catch (Exception e){
            throw Response.makeFailResponse(CodeAs.CODE_INSERT_CATEGORY_FAIL_CODE,
                    HttpStatus.INTERNAL_SERVER_ERROR,CodeAs.CODE_INSERT_CATEGORY_FAIL_MESSAGE , e);
        }
    }

    // Entity <=> DTO 매핑함수
    private Code mapCodeCreateInfoToCode(CodeDto.codeCreateInfo codeCreateInfo) {
        return Code.builder()
                .code(codeCreateInfo.getCode())
                .codeType(codeCreateInfo.getCodeType())
                .codeName(Response.getOrDefaultString(codeCreateInfo.getCodeName()))
                .codeRemark(Response.getOrDefaultString(codeCreateInfo.getCodeRemark()))
                .upperCode(CodeAs.BLANK)
                .codeDepth(CodeAs.NULL_DEPTH)
                .codeCtg(codeCreateInfo.getCodeCtg())
                .createId(CodeAs.ADMIN)
                .updateId(CodeAs.ADMIN)
                .activeYn(true)
                .build();
    }

    // code 저장 혹은 업데이트 함수
    private Code saveCode(Code code) {
        Code saveResult = codeRepository.save(code);
        return saveResult != null ? saveResult : null;
    }

        
    @Override
    @Transactional(readOnly = true)
    public CodeDto.codeInfo validateCodeService(CodeDto.validationCodeInfo validationCodeInfo) {
        /*
         * 1. 검증을 위해, 요청받은 코드의 TYPE과 NAME을 각각 코드 테이블에 조회한다.
         *
         * 2-1. TYPE , NAME 둘 다 존재하지 않는 경우에는 신생 PK를 만들어 응답한다.
         * 2-2. TYPE이 존재하고 , NAME이 존재하지 않는 경우는 마지막 코드에 PK+1을 만들어 응답한다.
         * 2-3. NAME이 존재하는 경우는 NULL을 응답한다.
         * 2-4. 조회 오류인 경우에는 에러를 응답한다.
         *
         * */
            List<Code> typeList = new ArrayList<>();
            List<Code> nameList = new ArrayList<>();
            CodeDto.codeInfo res = CodeDto.codeInfo.builder().build();
        try{
            typeList = codeRepository.findByCodeType(validationCodeInfo.getCodeType());
            //요청받은 코드 TYPE과 해당 카테고리에 속하는 코드이름 공백 제외처리후 조회
            nameList = codeRepository.findByCodeTypeAndCodeNameContainingAndCodeCtg(validationCodeInfo.getCodeType(),
                    makeTrim(validationCodeInfo.getCodeName()), validationCodeInfo.getCodeCtg());
            if(typeList == null || nameList == null){
                throw new CustomException(CodeAs.CODE_SELECT_CATEGORY_FAIL_CODE,
                        HttpStatus.INTERNAL_SERVER_ERROR, CodeAs.CODE_SELECT_CATEGORY_FAIL_MESSAGE , null);
            }

            log.info("typeList size : {} data : {} ",typeList.size(),typeList);
            log.info("nameList size : {} data : {} ",nameList.size(),nameList);

            // TYPE이 없고 CODE_NAME도 없는 경우 (신생 PK)
            if (typeList.isEmpty() && nameList.isEmpty()) {
                res = CodeDto.codeInfo.builder()
                        .code(validationCodeInfo.getCodeType() + CodeAs.PK_FIRST)
                        .build();
            }
            // TYPE은 있고 CODE_NAME은 없는 경우 (기존 PK+1)
            else if (!typeList.isEmpty() && nameList.isEmpty()) {
                String nextPk = makePK(typeList);

                res = CodeDto.codeInfo.builder()
                        .code(nextPk)
                        .build();
            }
            // CODE_NAME이 있는 경우
            else if (!nameList.isEmpty()) {
                res = CodeDto.codeInfo.builder()
                        .code(CodeAs.BLANK)
                        .build();
            }

            return res;
        }
        catch (Exception e){
            throw Response.makeFailResponse(CodeAs.CODE_VALIDATE_FAIL_CODE,
                    HttpStatus.INTERNAL_SERVER_ERROR,CodeAs.CODE_VALIDATE_FAIL_MESSAGE , e);
        }
    }

    //문자열 압축기능 (공백제거)
    private String makeTrim(String codeName){
        String trimCodeName = codeName.replaceAll(CodeAs.SPACING, CodeAs.BLANK);
        return trimCodeName;
    }

    //PK 만들어주는 기능
    private String makePK(List<Code> codeList){
        Code lastCode = codeList.get(codeList.size() - 1);
        String lastCodePk = lastCode.getCode();

        int nextPkNum = Integer.parseInt(lastCodePk.substring(lastCodePk.length() - 2)) + 1;

        //카테고리일 경우 PK채번이 다름
        if(lastCode.getCodeType().equals(CodeAs.CATEGORY)){
            return lastCode.getUpperCode()+String.format(CodeAs.STRING_FORMAT, nextPkNum);
        }
        //카테고리 내 코드일 경우는 codeType으로
        return lastCode.getCodeType()+String.format(CodeAs.STRING_FORMAT, nextPkNum);
    }



    @Override
    @Transactional(readOnly = true)
    public CodeDto.codeListMapInfo getCodeListService(CodeDto.codeInfo codeInfo) {
        /*
         * 1. 특정 카테고리에 존재하는 코드 리스트를 조회한다.
         *
         * 2-1. 만약 카테고리 내 코드가 존재하면 Entity와 DTO를 매핑해 배열로 응답한다.
         * 2-2. 만약 카테고리 내 코드가 아직 존재하지 않으면(카테고리만 존재하면), 빈 배열 null값을 응답한다.
         * 2-3. 데이터 조회 오류면 에러를 응답한다.
         *
         * */
        try {
            CodeDto.codeListMapInfo res = mapCodeToCodeListMap(codeInfo.getCode());
            return res;
        }
        catch (Exception e) {
            throw Response.makeFailResponse(CodeAs.CODE_SELECT_CATEGORY_FAIL_CODE,
                    HttpStatus.INTERNAL_SERVER_ERROR, CodeAs.CODE_SELECT_CATEGORY_FAIL_MESSAGE , e);
        }
    }


    /*
    * 코드 활성화 / 비활성화 기능
    * */
    @Override
    @Transactional
    public  CodeDto.codeListMapInfo activateCodeService(CodeDto.codeInfo codeInfo) {
        try {
            /*
             * 1. 요청에서 받아온 codeInfo를 통해 코드를 조회한다.
             * 2. 코드의 활성여부에 따라 그에 반대로 업데이트 한다. (더티체킹을 통해 바로 수정 가능)
             * 3. 바뀐 결과와 전체 코드 리스트를 응답한다.
             * */

            // 코드 조회
            Code code = codeRepository.findByCode(codeInfo.getCode());
            // 활성화 상태 변경 전
            boolean originalState = code.isActiveYn();
            if(code == null){
                throw new CustomException(CodeAs.CODE_SELECT_PK_FAIL_CODE,
                        HttpStatus.INTERNAL_SERVER_ERROR,CodeAs.CODE_SELECT_PK_FAIL_MESSAGE , null);
            }
            // 활성화 상태 변경 후
            Boolean newActiveYn = code.isActiveYn() == true ? false : true;
            code.updateActiveYn(newActiveYn);
            boolean changedState = code.isActiveYn();

            // 수정이 안되었으면(더티체킹이 안되었으면)
            if ( originalState == changedState){
                throw new CustomException(CodeAs.CODE_INFO_ACTIVE_FAIL_CODE,
                        HttpStatus.INTERNAL_SERVER_ERROR,CodeAs.CODE_INFO_ACTIVE_FAIL_MESSAGE , null);
            }

//          수정 후 바뀐 상태가 포함된 전체 코드 리스트 응답
            CodeDto.codeListMapInfo res = mapCodeToCodeListMap(code.getCodeCtg());
            return res;

        }
        catch (Exception e){
            throw Response.makeFailResponse(CodeAs.CODE_INFO_MODIFY_FAIL_CODE,
                    HttpStatus.INTERNAL_SERVER_ERROR,CodeAs.CODE_INFO_MODIFY_FAIL_MESSAGE , e);
        }
    }

    @Override
    @Transactional
    public  CodeDto.codeListMapInfo updateCodeService(CodeDto.codeModifyInfo codeModifyInfo) {
        try {
            /*
            * 1. Code PK를 통해 코드를 조회한다.
            * 2. 더티체킹을 통해 update를 진행 후 응답한다.
            * */

            // 코드 조회
            Code code = codeRepository.findByCode(codeModifyInfo.getCode());
            if(code == null){
                throw new CustomException(CodeAs.CODE_SELECT_PK_FAIL_CODE,
                        HttpStatus.INTERNAL_SERVER_ERROR,CodeAs.CODE_SELECT_PK_FAIL_MESSAGE , null);
            }

            // 요청받은 정보들(코드 이름, 비고) 수정 진행
            updateCodeFields(code, codeModifyInfo , CodeAs.CODE);

            //  수정 후 바뀐 데이터가 포함된 전체 코드 리스트 응답
            CodeDto.codeListMapInfo res = mapCodeToCodeListMap(code.getCodeCtg());

            return res;
        }
        catch (Exception e){
            throw Response.makeFailResponse(CodeAs.CODE_INFO_MODIFY_FAIL_CODE,
                    HttpStatus.INTERNAL_SERVER_ERROR,CodeAs.CODE_INFO_MODIFY_FAIL_MESSAGE , e);
        }
    }

    // 카테고리 조회 후 code객체 생성 함수
    private CodeDto.codeListMapInfo mapCodeToCodeListMap(String codeCtg){

        List<Code> codeSelectList = codeRepository.findByCodeCtg(codeCtg);
        if (codeSelectList == null){
            throw new CustomException(CodeAs.CODE_SELECT_CATEGORY_FAIL_CODE,
                    HttpStatus.INTERNAL_SERVER_ERROR, CodeAs.CODE_SELECT_CATEGORY_FAIL_MESSAGE , null);
        }

        List<CodeDto.codeListInfo> infoList = new ArrayList<>();
        String codeType = CodeAs.BLANK;

        if (codeSelectList.isEmpty()) {
            // 카테고리 내 코드가 없을 경우
            Code code = Code.builder()
                    .activeYn(false)
                    .build();
            infoList.add(mapCodeToCodeListInfo(code));

        } else {
            // 카테고리 내 코드가 존재할 경우
            for (Code code : codeSelectList) {
                infoList.add(mapCodeToCodeListInfo(code));
            }
            codeType = codeSelectList.get(CodeAs.ZERO).getCodeType();
        }

        CodeDto.codeListMapInfo res = CodeDto.codeListMapInfo.builder()
                .codeList(infoList)
                .codeType(codeType)
                .build();

        return res;
    }

    // Entity <=> DTO 매핑 함수
    private CodeDto.codeListInfo mapCodeToCodeListInfo(Code code) {
        return CodeDto.codeListInfo.builder()
                .code(Response.getOrDefaultString(code.getCode()))
                .codeCtg(Response.getOrDefaultString(code.getCodeCtg()))
                .codeName(Response.getOrDefaultString(code.getCodeName()))
                .codeRemark(Response.getOrDefaultString(code.getCodeRemark()))
                .activeYn(code.isActiveYn())
                .build();
    }

    // code 테이블 필드 업데이트 함수
    private void updateCodeFields(Code code, CodeDto.codeModifyInfo codeModifyInfo, String type) {
        List<Code> dupCodeNameList = new ArrayList<>();

        //카테고리 업데이트의 경우(같은 uppercode에 속하는 코드들 조회, 자기자신은 제외)
        if(type.equals(CodeAs.CATEGORY)){
            //수정 전 이름 중복여부 확인 (수정 시 이름 조회하는데, 자기자신은 제외)
            dupCodeNameList = codeRepository.findByCodeTypeAndUpperCodeAndCodeNameContainingAndCodeIsNot
                    (code.getCodeType(),code.getUpperCode(),codeModifyInfo.getCodeName(), codeModifyInfo.getCode());
        }
        //일반 코드 업데이트의 경우 카테고리 내 소속여부 및 이름 중복여부 확인 (수정 시 이름 조회하는데, 자기자신은 제외)
        else if(type.equals(CodeAs.CODE)){
            dupCodeNameList = codeRepository.findByCodeTypeAndCodeNameContainingAndCodeIsNotAndCodeCtg
                    (code.getCodeType(), codeModifyInfo.getCodeName(), codeModifyInfo.getCode(), code.getCodeCtg());
        }

        if(dupCodeNameList == null){
            throw new CustomException(CodeAs.CODE_SELECT_TYPE_NAME_FAIL_CODE , HttpStatus.INTERNAL_SERVER_ERROR , CodeAs.CODE_SELECT_TYPE_NAME_FAIL_MESSAGE , null);
        }

        //이미 해당 코드타입에 이름이 있으면
        if(dupCodeNameList.size()>0){
            throw new CustomException(CodeAs.CODE_UPDATE_DUP_CODE , HttpStatus.INTERNAL_SERVER_ERROR , CodeAs.CODE_UPDATE_DUP_MESSAGE , null);
        }

        //수정 전 기존코드
        String originalCodeName = code.getCodeName();
        String originalCodeRemark = code.getCodeRemark();
        log.info("코드 정보 변경 전 : {}",originalCodeName+" "+originalCodeRemark);
        //수정 진행
        code.updateCodeName(codeModifyInfo.getCodeName());
        code.updateCodeRemark(codeModifyInfo.getCodeRemark());
        //수정 후 바뀐코드
        String changedCodeName = code.getCodeName();
        String changedCodeRemark = code.getCodeRemark();
        log.info("코드 정보 변경 후 : {}",changedCodeName+" "+changedCodeRemark);
    }
}
