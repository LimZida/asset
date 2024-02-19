package com.mcnc.assetmgmt.history.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mcnc.assetmgmt.asset.service.common.AssetCommonComponent;
import com.mcnc.assetmgmt.code.entity.Code;
import com.mcnc.assetmgmt.code.repository.CodeRepository;
import com.mcnc.assetmgmt.history.dto.HistoryDto;
import com.mcnc.assetmgmt.history.entity.HistoryEntity;
import com.mcnc.assetmgmt.history.entity.QHistoryEntity;
import com.mcnc.assetmgmt.history.service.HistoryService;
import com.mcnc.assetmgmt.util.common.CodeAs;
import com.mcnc.assetmgmt.util.common.Response;
import com.mcnc.assetmgmt.util.exception.CustomException;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * title : HistoryService
 *
 * description : 전체 히스토리 / 히스토리 검색 기능
 *
 * reference :
 *
 * author : 임채성
 *
 * date : 2023.11.24
 **/
@Service
@RequiredArgsConstructor
@Slf4j
public class HistoryServiceImpl implements HistoryService {
    private final CodeRepository codeRepository;
    private final AssetCommonComponent assetCommonComponent;
    private final JPAQueryFactory queryFactory;
    private final QHistoryEntity qHistoryEntity = new QHistoryEntity("m");
    // 할당 히스토리
    @Transactional
    public Map getHistoryList(ObjectNode assignAssetHistoryObj) {
        try {
            int historyCount = 0;
            LocalDateTime startDateTime = null;
            LocalDateTime endDateTime = null;
            Map<Object, Object> map= new HashMap<>();

            ObjectMapper objMapper = new ObjectMapper();
            Object asset = objMapper.treeToValue(assignAssetHistoryObj.get("assetHistoryList"), HistoryDto.class);
            HistoryDto assetHistoryDto = (HistoryDto) asset;

            if(!assetHistoryDto.getAssetCategory().equals(CodeAs.ALL_CODE)){
                // 코드 가져와서 코드명 출력 하고 setAssetCategory 해서 넣기
                Code codecheck = findCodeByPk(assetHistoryDto.getAssetCategory());
                assetHistoryDto.setAssetCategory(codecheck.getCodeName());
            }
            // 시작날짜
            if(assetHistoryDto.getStartDate() != null){
                 startDateTime = LocalDateTime.parse(String.valueOf(assetHistoryDto.getStartDate()) + "000",
                        DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
            }
            // 종료날짜
            if(assetHistoryDto.getEndDate() != null){
                 endDateTime = LocalDateTime.parse(String.valueOf(assetHistoryDto.getEndDate()) + "000",
                        DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
            }
            // 히스토리 전체 개수
            long resultTotalCount = queryFactory
                    //.select(Wildcard.count) //select count(*)
                    .select(qHistoryEntity.count())
                    .from(qHistoryEntity)
                    .where(
                            historyCategoryEq(assetHistoryDto.getHistoryCategory(), assetHistoryDto.getKeyword()),
                            assetFlagEq(assetHistoryDto.getAssetFlag()),
                            assetCategoryEq(assetHistoryDto.getAssetCategory()),
                            startDateEq(startDateTime),
                            endDateEq(endDateTime)
                    ).fetchOne();

            // 히스토리 리스트
            List<HistoryEntity> result = queryFactory.selectFrom(qHistoryEntity)
                    .where(
                            historyCategoryEq(assetHistoryDto.getHistoryCategory(), assetHistoryDto.getKeyword()),
                            assetFlagEq(assetHistoryDto.getAssetFlag()),
                            assetCategoryEq(assetHistoryDto.getAssetCategory()),
                            startDateEq(startDateTime),
                            endDateEq(endDateTime)
                            )
                    .offset(assetHistoryDto.getStartIdx())
                    .limit(assetHistoryDto.getPageCntLimit())
                    .orderBy(qHistoryEntity.createDate.desc())
                    .fetch();

            List<HistoryDto.historyInfo> res = new ArrayList<>();

            for(HistoryEntity history : result) {
                HistoryDto.historyInfo historyInfo = HistoryDto.historyInfo.builder()
                        .assetNo(Response.getOrDefaultString(history.getAssetNo()))
                        .assetCategory(Response.getOrDefaultString(history.getAssetCategory()))
                        .newUserName(Response.getOrDefaultString(history.getNewUserName()))
                        .newUserDept(Response.getOrDefaultString(history.getNewUserDept()))
                        .newUserId(Response.getOrDefaultString(history.getNewUserId()))
                        .previousUserId(Response.getOrDefaultString(history.getPreviousUserId()))
                        .previousUserName(Response.getOrDefaultString(history.getPreviousUserName()))
                        .previousUserDept(Response.getOrDefaultString(history.getPreviousUserDept()))
                        .assetStatus(Response.getOrDefaultString(history.getAssetStatus()))
                        .modelName(Response.getOrDefaultString(history.getModelName()))
                        .assetFlag(Response.getOrDefaultString(history.getAssetFlag()))
                        // timestamp 형식에서 String 형식으로 변환
                        .createDate(assetCommonComponent.convertLocalDateTimeToString(history.getCreateDate()))
                        .build();

                res.add(historyInfo);
            }
            // 총 개수 저장
            map.put(CodeAs.ASSIGN_HISTORY_TOTAL_CNT, resultTotalCount);
            // 히스토리 저장
            map.put(CodeAs.ASSIGN_HISTORY_LIST, res);

            return map;
        }catch (Exception e){
            throw Response.makeFailResponse(CodeAs.HISTORY_SELECT_FAIL_CODE, HttpStatus.BAD_REQUEST,
                    CodeAs.HISTORY_SELECT_FAIL_MESSAGE, e);
        }
    }

    // null값 체크해서 넣기
    // 카테고리, 검색 키워드
    private BooleanExpression historyCategoryEq(String historyCategory, String keyWord) {
        if (historyCategory.equals(CodeAs.ALL_CODE)) {
            return null;
        }
        if (historyCategory.equals("assetNo")) {
            return qHistoryEntity.assetNo.like("%" + keyWord + "%");
        }
        if (historyCategory.equals("assetCategory")) {
            return qHistoryEntity.assetCategory.like("%" + keyWord + "%");
        }
        if (historyCategory.equals("modelName")) {
            return qHistoryEntity.modelName.like("%" + keyWord + "%");
        }
        if (historyCategory.equals("previousUserName")) {
            return qHistoryEntity.previousUserName.like("%" + keyWord + "%");
        }
        if (historyCategory.equals("newUserName")) {
            return qHistoryEntity.newUserName.like("%" + keyWord + "%");
        }
        if (historyCategory.equals("assetStatus")) {
            return qHistoryEntity.assetStatus.like("%" + keyWord + "%");
        }
        return null;
    }
    // HW / SW 구분
    private BooleanExpression assetFlagEq(String assetFlag) {
        if (assetFlag.equals(CodeAs.ALL_CODE)) {
            return null;
        }
        return qHistoryEntity.assetFlag.eq(assetFlag);
    }
    // 자산구분
    private BooleanExpression assetCategoryEq(String assetCategory) {
        if (assetCategory.equals(CodeAs.ALL_CODE)) {
            return null;
        }
        return qHistoryEntity.assetCategory.like("%" + assetCategory + "%");
    }
    // 시작날짜
    private BooleanExpression startDateEq(LocalDateTime startDate) {
        if (startDate == null) {
            return null;
        }
        return qHistoryEntity.createDate.goe(startDate);
    }
    // 종료날짜
    private BooleanExpression endDateEq(LocalDateTime endDate) {
        if (endDate == null) {
            return null;
        }
        return qHistoryEntity.createDate.loe(endDate);
    }
    //자산 코드 / 이름 불러오기
    @Transactional
    public Map getAssetNameAndCodeService() {
        Map<Object, Object> map= new HashMap<>();
        List<Code> assetCodeList = codeRepository.findByCodeType(CodeAs.ASSET);
        if(assetCodeList == null || assetCodeList.isEmpty()){
            throw new CustomException(CodeAs.DASHBOARD_ASSET_LIST_ERROR_CODE, HttpStatus.INTERNAL_SERVER_ERROR
                    ,CodeAs.DASHBOARD_ASSET_LIST_ERROR_MESSAGE , null);
        }
        List<HistoryDto.assetCodeInfo> hwList = getAssetCodeListByAsset(assetCodeList, CodeAs.CATEGORY_ASSET_DIV_HW);
        List<HistoryDto.assetCodeInfo> swList = getAssetCodeListByAsset(assetCodeList, CodeAs.CATEGORY_ASSET_DIV_SW);

        map.put("hwList", hwList);
        map.put("swList", swList);

        return map;
    }
    private Code findCodeByPk(String codePk) throws Exception {
        Code code = codeRepository.findByCode(codePk);
        if(code == null){
            throw new CustomException(CodeAs.CODE_SELECT_PK_FAIL_CODE, HttpStatus.INTERNAL_SERVER_ERROR,
                    CodeAs.CODE_SELECT_PK_FAIL_MESSAGE,null);
        }else{
            return code;
        }
    }
    // HW/SW 리스트를 받아 부서의 이름과 코드를 응답하는 함수
    private List<HistoryDto.assetCodeInfo> getAssetCodeListByAsset(List<Code> assetCodeList , String category){
        List res = new ArrayList<>();

        for(Code code : assetCodeList){
            String codeCtg = code.getCodeCtg();
            if(category.equals(codeCtg)){
                String upperCode = codeRepository.findByCode(codeCtg).getUpperCode();
                if(upperCode == null){
                    throw new CustomException(CodeAs.DASHBOARD_ASSET_CODE_ERROR_CODE, HttpStatus.INTERNAL_SERVER_ERROR
                            ,CodeAs.DASHBOARD_ASSET_CODE_ERROR_MESSAGE , null);
                }
                HistoryDto.assetCodeInfo assetInfo = mapCodeToHistoryDtoDto(code);
                res.add(assetInfo);
            }
        }
        return res;
    }
    //Entity <=> DTO 매핑함수
    private HistoryDto.assetCodeInfo mapCodeToHistoryDtoDto(Code asset) {
        return HistoryDto.assetCodeInfo.builder()
                .code(Response.getOrDefaultString(asset.getCode()))
                .codeName(Response.getOrDefaultString(asset.getCodeName()))
                .build();
    }
}
