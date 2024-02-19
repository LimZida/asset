package com.mcnc.assetmgmt.dashboard.service.impl;

import com.mcnc.assetmgmt.asset.entity.HardwareEntity;
import com.mcnc.assetmgmt.asset.entity.SoftwareEntity;
import com.mcnc.assetmgmt.asset.service.common.AssetCommonComponent;
import com.mcnc.assetmgmt.assignment.entity.AssignmentEntity;
import com.mcnc.assetmgmt.assignment.repository.AssignmentRepository;
import com.mcnc.assetmgmt.code.entity.Code;
import com.mcnc.assetmgmt.code.repository.CodeRepository;
import com.mcnc.assetmgmt.dashboard.dto.DashboardDto;
import com.mcnc.assetmgmt.dashboard.repository.DashboardHardwareRepository;
import com.mcnc.assetmgmt.dashboard.repository.DashboardHistoryRepository;
import com.mcnc.assetmgmt.dashboard.repository.DashboardSoftwareRepository;
import com.mcnc.assetmgmt.dashboard.service.DashboardService;
import com.mcnc.assetmgmt.history.entity.HistoryEntity;
import com.mcnc.assetmgmt.util.common.CodeAs;
import com.mcnc.assetmgmt.util.exception.CustomException;
import com.mcnc.assetmgmt.util.common.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * title : DashboardServiceImpl
 *
 * description :  DashboardService의 구현체 , 대시보드 자산현황, 반입반출기록 로직
 *
 * reference :  올바른 엔티티 수정 방법: https://www.inflearn.com/questions/15944/%EA%B0%95%EC%9D%98-%EC%A4%91%EC%97%90-%EA%B0%92%ED%83%80%EC%9E%85%EC%9D%98-%EC%88%98%EC%A0%95%EC%97%90-%EB%8C%80%ED%95%B4%EC%84%9C-%EB%8B%A4%EB%A4%84%EC%A3%BC%EC%85%A8%EB%8A%94%EB%8D%B0%EC%9A%94-%EB%AC%B8%EB%93%9D-%EC%97%94%ED%8B%B0%ED%8B%B0-%EC%88%98%EC%A0%95%EC%97%90-%EB%8C%80%ED%95%B4%EC%84%9C-%EA%B6%81%EA%B8%88%ED%95%B4%EC%A0%B8-%EC%A7%88%EB%AC%B8%EB%93%9C%EB%A6%BD%EB%8B%88%EB%8B%A4
 *
 *              DTO의 적용 범위 https://tecoble.techcourse.co.kr/post/2021-04-25-dto-layer-scope/
 *
 *
 *
 * author : 임현영
 * date : 2023.11.24
 **/
@Service
@RequiredArgsConstructor // 자동주입
@Slf4j
public class DashboardServiceImpl implements DashboardService {
    private final CodeRepository codeRepository;
    private final DashboardHistoryRepository dashboardHistoryRepository;
    private final DashboardHardwareRepository dashboardHardwareRepository;
    private final DashboardSoftwareRepository dashboardSoftwareRepository;
    private final AssignmentRepository assignmentRepository;

    @Override
    @Transactional(readOnly = true)
    public DashboardDto.assetMapInfo getAssetCodeAndNameService() {
        /*
        * 현재 보유하고 있는 자산코드와 이름
        *
        * 1. Code 테이블에서 ASSET 카테고리의 자산 중 하드웨어를 조회합니다.
        * 2. 조회한 결과에서 카테고리의 코드를 통해 자산의 이름을 조회합니다.
        * 3. Entity와 DTO를 매핑해 응답합니다.
        *
        * */
        try{

            List<Code> assetCodeList = codeRepository.findByCodeType(CodeAs.ASSET);
            if(assetCodeList == null || assetCodeList.isEmpty()){
                throw new CustomException(CodeAs.DASHBOARD_ASSET_LIST_ERROR_CODE, HttpStatus.INTERNAL_SERVER_ERROR
                        ,CodeAs.DASHBOARD_ASSET_LIST_ERROR_MESSAGE , null);
            }

            //hw와 sw를 구별하여 따로 List로 응답
            List<String> hwList = getAssetCodeListByAsset(assetCodeList, CodeAs.CATEGORY_ASSET_DIV_HW);
            List<String> swList = getAssetCodeListByAsset(assetCodeList, CodeAs.CATEGORY_ASSET_DIV_SW);

            //Map에 담아 응답
            DashboardDto.assetMapInfo res = mapListToAssetMap(hwList,swList);

            return res;
        }
        catch (Exception e){
            throw Response.makeFailResponse
                    (CodeAs.DASHBOARD_ASSET_ERROR_CODE, HttpStatus.INTERNAL_SERVER_ERROR
                    ,CodeAs.DASHBOARD_ASSET_ERROR_MESSAGE , e);
        }
    }

    //Map에 List를 담아 응답해주는 함수
    private DashboardDto.assetMapInfo mapListToAssetMap(List<String> hwList,  List<String> swList){
        return DashboardDto.assetMapInfo.builder()
                .hwList(hwList)
                .swList(swList)
                .build();
    }

    // 코드테이블에서 하드웨어, 소프트웨어 자산 정보 (Asset)를 조회해 자산들의 이름을 응답하는 함수
    private   List<String> getAssetCodeListByAsset(List<Code> assetCodeList , String category){
        List<String> res = new ArrayList<>();

        for(Code code : assetCodeList){
            String codeCtg = code.getCodeCtg();
            if(category.equals(codeCtg)){
                String upperCode = codeRepository.findByCode(codeCtg).getUpperCode();
                if(upperCode == null){
                    throw new CustomException(CodeAs.DASHBOARD_ASSET_CODE_ERROR_CODE, HttpStatus.INTERNAL_SERVER_ERROR
                            ,CodeAs.DASHBOARD_ASSET_CODE_ERROR_MESSAGE , null);
                }

                res.add(Response.getOrDefaultString(code.getCodeName()));
            }

        }
        return res;
    }

    @Override
    @Transactional(readOnly = true)
    public List<DashboardDto.assetInfo> getInOutHistoryService(DashboardDto.rowInfo rowInfo) {
            /*
             * 요청받은 개수 이후부터 현재까지의 반입반출 기록
             *
             * 1. 페이징 함수를 통해 요청받은 rowCount만큼 데이터를 뽑습니다.
             * 2. Entity와 Dto를 매핑 후 응답합니다.
             *
             * */
        try {

            //0부터 요청받은 row까지 페이징 처리
            Page<HistoryEntity> historyList = dashboardHistoryRepository.findAllByOrderByCreateDateDesc
                    (PageRequest.of(0, rowInfo.getRowCount()));
            if(historyList == null){
                throw new CustomException(CodeAs.DASHBOARD_HISTORY_LIST_ERROR_CODE, HttpStatus.INTERNAL_SERVER_ERROR
                        ,CodeAs.DASHBOARD_HISTORY_LIST_ERROR_MESSAGE,null);
            }

            List<DashboardDto.assetInfo> res = mapHistoryEntityToAssetInfo(historyList);
            return res;
        }catch (Exception e){
            throw Response.makeFailResponse(CodeAs.DASHBOARD_IN_OUT_ERROR_CODE, HttpStatus.INTERNAL_SERVER_ERROR
                    ,CodeAs.DASHBOARD_IN_OUT_ERROR_MESSAGE,e);
        }
    }

    // Entitiy <=> DTO 매핑 함수
    private List<DashboardDto.assetInfo> mapHistoryEntityToAssetInfo(Page<HistoryEntity> historyList){
        List<DashboardDto.assetInfo> res = new ArrayList<>();

        for(HistoryEntity history : historyList){
            DashboardDto.assetInfo assetInfo = DashboardDto.assetInfo.builder()
                    .assetNo(Response.getOrDefaultString(history.getAssetNo()))
                    .assetDiv(Response.getOrDefaultString(history.getAssetCategory()))
                    .newUserId(Response.getOrDefaultString(history.getNewUserId()))
                    .newUserName(Response.getOrDefaultString(history.getNewUserName()))
                    .prevUserId(Response.getOrDefaultString(history.getPreviousUserId()))
                    .prevUserName(Response.getOrDefaultString(history.getPreviousUserName()))
                    .assetStatus(Response.getOrDefaultString(history.getAssetStatus()))
                    .assetName(Response.getOrDefaultString(history.getModelName()))
                    // timeStamp 형식에서 String 형식으로 변환
                    .lastModifiedDate(convertLocalDateTimeToString(history.getCreateDate()))
                    .build();

            res.add(assetInfo);
        }
        return res;
    }

    // LocalDateTime -> String 변환
    private String convertLocalDateTimeToString(LocalDateTime date) {
        return date != null ? date.format(DateTimeFormatter.ofPattern(CodeAs.DATE_PATTERN)) : CodeAs.BLANK;
    }

    /*
     * HW의 총개수, 할당, 잉여, 고장, 노후 등 종합 상세 내역
     * */
    @Override
    @Transactional(readOnly = true)
    public List<DashboardDto.hwListInfo> getHwDetailAssetListService() {
        //응답용 배열 선언
        List<DashboardDto.hwListInfo> res = new ArrayList<>();

        try {
            /*
            * 1. 각 종류별 하드웨어 자원을 모두 조회합니다.
            * 2. 하드웨어 자원 리스트를 순회하며,
            *
            * 3-1. 자원 할당 테이블의 HW 자원을 확인하며 할당 여부를 확인합니다.
            * 3-2. 노후화 여부를 확인합니다.
            * 3-3. 고장 여부를 확인합니다.
            * 3-4. 자산 종류 코드와 코드 이름을 확인합니다.
            *
            * 4. 확인한 내용을 Dto로 묶어 List로 응답합니다.
            * */

            //카테고리 종류별 하드웨어 자원 조회
            List<Code> assetDivList = codeRepository.findByCodeCtg(CodeAs.CATEGORY_ASSET_DIV_HW);
            if(assetDivList == null || assetDivList.isEmpty()){
                throw new CustomException(CodeAs.CODE_HW_ASSET_SELECT_FAIL_CODE, HttpStatus.INTERNAL_SERVER_ERROR,
                        CodeAs.CODE_HW_ASSET_SELECT_FAIL_MESSAGE , null);
            }

            for(Code assetDiv : assetDivList){
                List<HardwareEntity> hardwareList = dashboardHardwareRepository.findByHwDivAndDeleteYn(assetDiv, false);
                //해당 자산코드에 해당하는 하드웨어 테이블에 하드웨어가 없으면 반복문 넘기기
//                if(hardwareList.isEmpty()){
//                    continue;
//                }

                int totalCnt = hardwareList.size();
                int allocationAsset = CodeAs.ZERO;
                int oldAsset = CodeAs.ZERO;
                int failureAsset = CodeAs.ZERO;
                String assetCodeName = Response.getOrDefaultString(assetDiv.getCodeName());
                String assetCode = Response.getOrDefaultString(assetDiv.getCode());

                for(HardwareEntity hardware : hardwareList){
                    //자산 로그 테이블과 조인해야함 (자산 로그 테이블 (다대일))
                    //해당 자산 번호와 조인, 날짜별로 내림차순을 해서 반출이 먼저 나오면 할당 O, 반입이 먼저 나오면 할당 X)
                    List<AssignmentEntity> assignmentList = assignmentRepository.findByAssetNoAndDeleteYnOrderByCreateDateDesc
                            (hardware.getHwNo(), CodeAs.NEGATIVE);

                    //해당 하드웨어에 대한 할당 내역이 있고, 할당인 경우
                    if (assignmentList.size() > 0 && StringUtils.hasText(assignmentList.get(0).getUsageName())){
                        allocationAsset+=1;
                    }

                    //노후화된 경우 (4년 이상)
                    // 자산에 대한 제조정보가 없을 경우는 진행하지 않음
                    Timestamp produceDate = Response.getOrDefaultTime(hardware.getProduceDate());
                    if (produceDate != null){
                        boolean oldResult = checkOldAsset(produceDate , CodeAs.OLD_ASSET_YEAR);
                        if (oldResult){
                            oldAsset+=1;
                        }
                    }

                    //결함이 있는 경우
                    if(Response.getOrDefaultBool(hardware.getFailureYn())){ // 임시로 바꿔둠
                        failureAsset+=1;
                    }
                }
                // 할당되지 않은 자산 = 전체 - 할당자산
                int notAllocationAsset = totalCnt-allocationAsset;

                // Data와 DTO 매핑
                DashboardDto.hwListInfo hwListInfo = mapDataToHwListInfo(totalCnt,allocationAsset,notAllocationAsset
                        ,oldAsset,failureAsset,assetCodeName,assetCode);


                res.add(hwListInfo);
            }

            return res;
        }
        catch (Exception e){
            throw  Response.makeFailResponse
            (CodeAs.DASHBOARD_HW_DETAIL_ERROR_CODE, HttpStatus.INTERNAL_SERVER_ERROR
                    ,CodeAs.DASHBOARD_HW_DETAIL_ERROR_MESSAGE,e);
        }
    }
    // 내부 선언 Data <=> DTO 매핑 함수
    private DashboardDto.hwListInfo mapDataToHwListInfo(int totalCnt, int allocationAsset, int notAllocationAsset,
                                                        int oldAsset, int failureAsset, String assetCodeName, String assetCode){

        return DashboardDto.hwListInfo.builder()
                .totalCnt(totalCnt)
                .allocationAsset(allocationAsset)
                .notAllocationAsset(notAllocationAsset)
                .oldAsset(oldAsset)
                .failureAsset(failureAsset)
                .assetCodeName(assetCodeName)
                .assetCode(assetCode)
                .build();
    }

    // n년 이상 차이나면 true, n년 미만 차이가 나면 false를 반환하는 함수
    private boolean checkOldAsset(Timestamp timestamp, int year) {
        //현재 시간
        Instant currentInstant = Instant.now();
        //제조일자
        Instant produceDateInstant = timestamp.toInstant();

        // Instant를 LocalDateTime으로 변환
        LocalDateTime currentDateTime = LocalDateTime.ofInstant(currentInstant, java.time.ZoneId.systemDefault());
        LocalDateTime produceDateTime = LocalDateTime.ofInstant(produceDateInstant, java.time.ZoneId.systemDefault());

        // Period를 사용하여 연도 차이 계산
        Period period = Period.between(produceDateTime.toLocalDate(), currentDateTime.toLocalDate());

        // 차이가 n년 이상이면 true, 그렇지 않으면 false 반환
        return period.getYears() >= year;
    }

    /*
     * SW의 총개수, 할당, 잉여, 기간 만료, 노후 등 종합 상세 내역
     * */
    @Override
    @Transactional(readOnly = true)
    public List<DashboardDto.swListInfo> getSwDetailAssetListService() {
        /*
         * 1. 각 종류별 소프트웨어 자원을 모두 조회합니다.
         * 2. 소프트 자원 리스트를 순회하며,
         *
         * 3-1. 자원 할당 테이블의 SW 자원을 확인하며 할당 여부를 확인합니다.
         * 3-2. 노후화 여부를 확인합니다.
         * 3-3. 기간 만료 여부를 확인합니다.
         * 3-4. 기간 만료 예정 여부를 확인합니다.
         * 3-5. 자산 종류 코드와 코드 이름을 확인합니다.
         *
         * 4. 확인한 내용을 Dto로 묶어 List로 응답합니다.
         * */
        List<DashboardDto.swListInfo> res = new ArrayList<>();

        try{
            //카테고리 종류별 소프트웨어 자원 조회
            List<Code> assetDivList = codeRepository.findByCodeCtg(CodeAs.CATEGORY_ASSET_DIV_SW);
            if(assetDivList.isEmpty()){
                throw new CustomException(CodeAs.CODE_SW_ASSET_SELECT_FAIL_CODE, HttpStatus.INTERNAL_SERVER_ERROR,
                        CodeAs.CODE_SW_ASSET_SELECT_FAIL_MESSAGE , null);
            }

            for(Code assetDiv : assetDivList) {
                List<SoftwareEntity> softwareList = dashboardSoftwareRepository.findBySwDivAndDeleteYn(assetDiv, false);
                //해당 자산코드에 해당하는 소프트웨어 테이블에 소프트웨어가 없으면 반복문 넘기기
//                if (softwareList.isEmpty()) {
//                    continue;
//                }

                int totalCnt = softwareList.size();
                int allocationAsset = CodeAs.ZERO;
                int expirationAsset = CodeAs.ZERO;
                int soonExpirationAsset = CodeAs.ZERO;
                String assetCodeName = Response.getOrDefaultString(assetDiv.getCodeName());
                String assetCode = Response.getOrDefaultString(assetDiv.getCode());

                for(SoftwareEntity software : softwareList){
                    //자산 로그 테이블과 조인해야함 (자산 로그 테이블 (다대일))
                    //해당 자산 번호와 조인, 날짜별로 내림차순을 해서 반출이 먼저 나오면 할당 O, 반입이 먼저 나오면 할당 X)
                    List<AssignmentEntity> assignmentList = assignmentRepository.findByAssetNoAndDeleteYnOrderByCreateDateDesc
                            (software.getSwNo(), CodeAs.NEGATIVE);

                    //해당 소프트웨어에 대한 할당 내역이 있는 경우
                    if (assignmentList.size() > 0){
                        for (AssignmentEntity assignment : assignmentList){
                            // 할당인 경우 (하나만 할당이면 됨)
                            if(StringUtils.hasText(Response.getOrDefaultString(assignment.getUsageName()))){
                                allocationAsset+=1;
                                break;
                            }
                        }
                    }

                    //라이센스 기간이 영구가 아닐 경우
                    if (software.getExpireDate() != null){
                        //만료되었으면
                        boolean expiredResult = isExpired(software.getExpireDate());
                        if(expiredResult){
                            expirationAsset+=1;
                        }
                        //만료되기 3개월 전이면
                        boolean soonExpiredResult = isSoonExpired(software.getExpireDate(), CodeAs.EXPIRED_ASSET_MONTH);
                        if(soonExpiredResult){
                            soonExpirationAsset+=1;
                        }
                    }
                }

                // 할당되지 않은 자산 = 전체 - 할당자산
                int notAllocationAsset = totalCnt-allocationAsset;

                // Data와 DTO 매핑
                DashboardDto.swListInfo swListInfo = mapDataToSwListInfo(totalCnt,allocationAsset,notAllocationAsset,
                        expirationAsset,soonExpirationAsset,assetCodeName,assetCode);

                res.add(swListInfo);
            }

            return res;

        } catch (Exception e){
            throw Response.makeFailResponse(CodeAs.DASHBOARD_SW_DETAIL_ERROR_CODE , HttpStatus.INTERNAL_SERVER_ERROR ,
                    CodeAs.DASHBOARD_SW_DETAIL_ERROR_MESSAGE, e);
        }
    }

    //소프트웨어의 자산 기간 만료 여부 확인 함수
    private boolean isExpired(Timestamp timestamp) {
        // 현재 시간을 얻음
        Instant currentInstant = Instant.now();
        // Timestamp를 Instant로 변환
        Instant targetInstant = timestamp.toInstant();

        return currentInstant.isAfter(targetInstant);
    }

    //소프트웨어의 자산 기간이 곧 만료되는지 여부 확인 함수
    private boolean isSoonExpired(Timestamp timestamp, int month) {
        // 현재 시간
        Instant currentInstant = Instant.now();
        // Timestamp를 LocalDateTime으로 변환
        LocalDateTime targetLocalDateTime = timestamp.toLocalDateTime();
        // 현재 시간 이후의 시간을 계산
        LocalDateTime now = currentInstant.atZone(ZoneId.systemDefault()).toLocalDateTime();
        // n개월 이후의 시간을 계산
        LocalDateTime monthsLater = currentInstant.atZone(ZoneId.systemDefault()).toLocalDateTime().plusMonths(month);
        // 주어진 Timestamp가 현재 이후인 동시에 N개월 이전인지 확인
        return targetLocalDateTime.isAfter(now) && targetLocalDateTime.isBefore(monthsLater);
    }

    // 내부 선언 Data <=> DTO 매핑 함수
    private DashboardDto.swListInfo mapDataToSwListInfo(int totalCnt, int allocationAsset, int notAllocationAsset,
                                                        int expirationAsset, int soonExpirationAsset, String assetCodeName, String assetCode){
        return DashboardDto.swListInfo.builder()
                .totalCnt(totalCnt)
                .allocationAsset(allocationAsset)
                .notAllocationAsset(notAllocationAsset)
                .expirationAsset(expirationAsset)
                .soonExpirationAsset(soonExpirationAsset)
                .assetCodeName(assetCodeName)
                .assetCode(assetCode)
                .build();
    }
}
