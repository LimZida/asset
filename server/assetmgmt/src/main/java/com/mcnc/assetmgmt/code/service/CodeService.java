package com.mcnc.assetmgmt.code.service;

import com.mcnc.assetmgmt.code.dto.CodeDto;

import java.util.List;

/**
 * title : CodeService
 *
 * description : codeRepository,CodeDto 매핑용 CodeService 인터페이스
 *
 * reference : Optional https://mangkyu.tistory.com/70
 *             메소드의 반환 값이 절대 null이 아니라면 Optional을 사용하지 않는 것이 좋다.
 *             즉, Optional은 메소드의 결과가 null이 될 수 있으며, null에 의해 오류가 발생할 가능성이 매우 높을 때 반환값으로만 사용되어야 한다.
 *
 *
 *
 * author : 임현영
 * date : 2023.11.09
 **/
public interface CodeService {
    //모든 카테고리 조회
    List<CodeDto.categoryInfo> getAllCategoryService();
    //카테고리 추가
//    List<CodeDto.categoryInfo> createCategoryService(CodeDto.categoryCreateInfo categoryCreateInfo);
    CodeDto.resultInfo createCategoryService(CodeDto.categoryCreateInfo categoryCreateInfo);
    //카테고리 추가 시 코드타입 검증
    CodeDto.codeTypeResultInfo validateCodeTypeService(CodeDto.codeTypeInfo codeTypeInfo);
    //카테고리 코드 중복체크
    CodeDto.codeInfo validateCategoryCodeService(CodeDto.validationCategoryCodeInfo validationCategoryCodeInfo);
    //카테고리 수정
    List<CodeDto.categoryInfo> updateCategoryService(CodeDto.codeModifyInfo codeModifyInfo);
    //카테고리 삭제
    List<CodeDto.categoryInfo> activateCategoryService(CodeDto.codeInfo codeInfo);
    //특정 카테고리 내 코드 조회
    CodeDto.codeListMapInfo getCodeListService(CodeDto.codeInfo codeInfo);
    //특정 카테고리 내 코드 추가
    CodeDto.codeListMapInfo createCodeService(CodeDto.codeCreateInfo codeCreateInfo);
    //코드 중복체크
    CodeDto.codeInfo validateCodeService(CodeDto.validationCodeInfo validationCodeInfo);
    //특정 카테고리에 속하는 코드 수정
    CodeDto.codeListMapInfo updateCodeService(CodeDto.codeModifyInfo codeModifyInfo);
    //특정 카테고리에 속하는 코드 활성*비활성
    CodeDto.codeListMapInfo activateCodeService(CodeDto.codeInfo codeInfo);
}
