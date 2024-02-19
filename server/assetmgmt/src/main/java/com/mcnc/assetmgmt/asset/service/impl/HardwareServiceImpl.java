package com.mcnc.assetmgmt.asset.service.impl;

import com.mcnc.assetmgmt.asset.service.HardwareService;
import com.mcnc.assetmgmt.asset.service.common.AssetCommonComponent;
import com.mcnc.assetmgmt.asset.dto.AssetCodeDto;
import com.mcnc.assetmgmt.asset.dto.HardwareDto;
import com.mcnc.assetmgmt.asset.entity.HardwareEntity;
import com.mcnc.assetmgmt.asset.repository.HardwareRepository;
import com.mcnc.assetmgmt.assignment.entity.AssignmentEntity;
import com.mcnc.assetmgmt.assignment.repository.AssignmentRepository;
import com.mcnc.assetmgmt.code.repository.CodeRepository;
import com.mcnc.assetmgmt.util.common.CodeAs;
import com.mcnc.assetmgmt.util.common.Response;
import com.mcnc.assetmgmt.util.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * title : HardwareServiceImpl
 *
 * description :  HardwareService의 구현체
 *
 * reference :  올바른 엔티티 수정 방법: https://www.inflearn.com/questions/15944/%EA%B0%95%EC%9D%98-%EC%A4%91%EC%97%90-%EA%B0%92%ED%83%80%EC%9E%85%EC%9D%98-%EC%88%98%EC%A0%95%EC%97%90-%EB%8C%80%ED%95%B4%EC%84%9C-%EB%8B%A4%EB%A4%84%EC%A3%BC%EC%85%A8%EB%8A%94%EB%8D%B0%EC%9A%94-%EB%AC%B8%EB%93%9D-%EC%97%94%ED%8B%B0%ED%8B%B0-%EC%88%98%EC%A0%95%EC%97%90-%EB%8C%80%ED%95%B4%EC%84%9C-%EA%B6%81%EA%B8%88%ED%95%B4%EC%A0%B8-%EC%A7%88%EB%AC%B8%EB%93%9C%EB%A6%BD%EB%8B%88%EB%8B%A4
 *              DTO의 적용 범위 https://tecoble.techcourse.co.kr/post/2021-04-25-dto-layer-scope/
 *              Spring 핵심 원리 기본편 (8) - @Primary, @Qualifier - https://velog.io/@neity16/Spring-%ED%95%B5%EC%8B%AC-%EC%9B%90%EB%A6%AC-%EA%B8%B0%EB%B3%B8%ED%8E%B8-8-Primary-Qualifier
 *
 * author : 임현영
 *
 * date : 2023.12.11
 **/
@Service
@RequiredArgsConstructor
@Slf4j
public class HardwareServiceImpl extends CodeAs implements HardwareService {
    private final HardwareRepository hardwareRepository;
    private final AssignmentRepository assignmentRepository;
    private final AssetCommonComponent assetCommonComponent;

    /*
    * HW 자산 리스트 조회
    *
    * 1. 사용하고 있는 HW를 조회합니다.
    * 2. 해당 결과를 매핑해 응답합니다.
    *
    * */
    @Override
    @Transactional(readOnly = true)
    public List<HardwareDto.hwInfo> getHwAssetListService() {
        try {
            List<HardwareEntity> hwList = hardwareRepository.findByDeleteYnOrderByCreateDateDesc(false);
            List<HardwareDto.hwInfo> res = new ArrayList<>();

            for (HardwareEntity hw : hwList) {
                res.add(hardwareEntityToHwSelectInfoDto(hw));
            }

            return res;
        } catch (Exception e) {
            throw Response.makeFailResponse(ASSET_SELECT_ERROR_CODE, HttpStatus.BAD_REQUEST, ASSET_SELECT_ERROR_MESSAGE, e);
        }
    }

    // HardwareEntity -> hwSelectInfo DTO 매핑 함수
    private HardwareDto.hwInfo hardwareEntityToHwSelectInfoDto(HardwareEntity hw) {
        return HardwareDto.hwInfo.builder()
                .hwRemarks(hw.getHwRemarks())
                .hwModel(hw.getHwModel())
                .hwSsd1(hw.getHwSsd1())
                .hwSsd2(hw.getHwSsd2())
                .hwHdd1(hw.getHwHdd1())
                .hwHdd2(hw.getHwHdd2())
                .hwRam1(hw.getHwRam1())
                .hwRam2(hw.getHwRam2())
                .produceDate(assetCommonComponent.convertTimestampToString(hw.getProduceDate()))
                .purchaseDate(assetCommonComponent.convertTimestampToString(hw.getPurchaseDate()))
                .failureYn(hw.getFailureYn())
                .oldYn(hw.getOldYn())
                .deleteYn(hw.getDeleteYn())
                .hwSN(hw.getHwSN())
                .hwNo(hw.getHwNo())
                .useTime(getUseTime(hw.getPurchaseDate()))
                .createDate(assetCommonComponent.convertLocalDateTimeToString(hw.getCreateDate()))
                .updateDate(assetCommonComponent.convertLocalDateTimeToString(hw.getUpdateDate()))
                //조인
                .hwDiv(assetCommonComponent.codeEntityToAssetCodeDto(hw.getHwDiv()))
                .hwCategory(assetCommonComponent.codeEntityToAssetCodeDto(hw.getHwCategory()))
                .hwLocation(assetCommonComponent.codeEntityToAssetCodeDto(hw.getHwLocation()))
                .hwMfr(assetCommonComponent.codeEntityToAssetCodeDto(hw.getHwMfr()))
                .hwCpu(assetCommonComponent.codeEntityToAssetCodeDto(hw.getHwCpu()))
                .hwUsage(assetCommonComponent.codeEntityToAssetCodeDto(hw.getHwUsage()))
                .usageInfo(getUsageInfo(hw.getHwNo()))
                .build();
    }

    // 사용시간 시간변환 함수 (Date => x년 x개월)
    private String getUseTime(Timestamp pastTime) {
        if (pastTime == null) return BLANK;

        Date pastDate = getPastDate(pastTime);

        int year = pastDate.getYear();
        int month = pastDate.getMonth() == ZERO && year == ZERO ? NUM_INIT : pastDate.getMonth();

        if (year != 0) {
            if (month != 0) {
                return year + YEAR + month + MONTH;
            } else {
                return year + YEAR;
            }
        } else {
            return month + MONTH;
        }
    }

    // usage 정보 가져오는 함수
    private HardwareDto.usageInfo getUsageInfo(String hwNo) {
        AssignmentEntity assignment = assignmentRepository.findByAssetNoAndDeleteYn(hwNo, NEGATIVE);
        HardwareDto.usageInfo usageInfo;

        if (assignment == null) {
            usageInfo = AssignmentEntityToHardwareDto(
                    assetCommonComponent.getNullCodeInfo(),
                    BLANK,
                    BLANK,
                    assetCommonComponent.getNullCodeInfo());
        } else {
            usageInfo = AssignmentEntityToHardwareDto(
                    assetCommonComponent.codeEntityToAssetCodeDto(assignment.getUsageDept()),
                    assignment.getUsageName(),
                    assignment.getUsageId(),
                    assetCommonComponent.codeEntityToAssetCodeDto(assignment.getAssetStatus()));
        }

        return usageInfo;
    }
    // entity <=> DTO 매핑
    private HardwareDto.usageInfo AssignmentEntityToHardwareDto(AssetCodeDto.codeInfo usageDept ,
                                                                String usageName, String usageId, AssetCodeDto.codeInfo assetStatus){
        return HardwareDto.usageInfo.builder()
                .usageDept(usageDept)
                .usageName(usageName)
                .usageId(usageId)
                .assetStatus(assetStatus).build();
    }

    /*
     * HW 자산 등록
     *
     * 1. 요청으로 받은 HW DTO 정보들을 Entity와 매핑합니다.
     * 2. POST(CREATE)요청이라면 PK 중복체크를 합니다. (중복이면 예외 발생)
     * 3. Entitiy를 save합니다.
     *
     * */
    @Override
    public HardwareDto.resultInfo insertHwAssetService(HardwareDto.hwInsertInfo insertInfo) {
        try {
            if (!duplicationPK(insertInfo.getHwNo())) {
                throw new CustomException(REQUEST_DUPLICATE_PK_CODE, HttpStatus.INTERNAL_SERVER_ERROR, REQUEST_DUPLICATE_PK_MESSAGE, null);
            }

            HardwareEntity hardware = hwInfoDtoToHardwareEntity(insertInfo);
            HardwareEntity insertResult = hardwareRepository.save(hardware);

            if (insertResult == null) {
                return HardwareDto.resultInfo.builder()
                        .result(false)
                        .build();
            } else {
                return HardwareDto.resultInfo.builder()
                        .result(true)
                        .build();
            }
        } catch (Exception e) {
            throw Response.makeFailResponse(ASSET_REGISTER_ERROR_CODE, HttpStatus.BAD_REQUEST, ASSET_REGISTER_ERROR_MESSAGE, e);
        }
    }

    // hwInfo DTO -> HardwareEntity 매핑 함수 (등록 시)
    public HardwareEntity hwInfoDtoToHardwareEntity(HardwareDto.hwInsertInfo insertInfo) {
        return HardwareEntity.builder()
                .hwNo(insertInfo.getHwNo())
                .createId(ADMIN)
                .updateId(ADMIN)
                .deleteYn(false)
                .oldYn(false)
                .failureYn(Response.getOrDefaultBool(insertInfo.getFailureYn()))
                .hwModel(Response.getOrDefaultString(insertInfo.getHwModel()))
                .hwSsd1(assetCommonComponent.validateNumber(insertInfo.getHwSsd1()))
                .hwSsd2(assetCommonComponent.validateNumber(insertInfo.getHwSsd2()))
                .hwHdd1(assetCommonComponent.validateNumber(insertInfo.getHwHdd1()))
                .hwHdd2(assetCommonComponent.validateNumber(insertInfo.getHwHdd2()))
                .hwRam1(assetCommonComponent.validateNumber(insertInfo.getHwRam1()))
                .hwRam2(assetCommonComponent.validateNumber(insertInfo.getHwRam2()))
                .purchaseDate(assetCommonComponent.convertStringToTimestamp(insertInfo.getPurchaseDate()))
                .produceDate(assetCommonComponent.convertStringToTimestamp(insertInfo.getProduceDate()))
                .hwSN(Response.getOrDefaultString(insertInfo.getHwSN()))
                .hwRemarks(Response.getOrDefaultString(insertInfo.getHwRemarks()))
                //code와 조인
                .hwDiv(assetCommonComponent.findCodeByPk(insertInfo.getHwDiv(), false))
                .hwCategory(assetCommonComponent.findCodeByPk(HARDWARE, false))
                .hwLocation(assetCommonComponent.findCodeByPk(insertInfo.getHwLocation(), false))
                .hwCpu(assetCommonComponent.findCodeByPk(insertInfo.getHwCpu(), true))
                .hwMfr(assetCommonComponent.findCodeByPk(insertInfo.getHwMfr(), false))
                .hwUsage(assetCommonComponent.findCodeByPk(insertInfo.getHwUsage(), true))
                .build();
    }

    // HW 자산 등록 시 PK 중복 확인
    private boolean duplicationPK(String hwNo) {
        return hardwareRepository.findByHwNo(hwNo) == null;
    }

    /*
     * HW 자산 수정
     *
     * 1. 요청으로 받은 HW DTO 정보들을 Entity와 매핑합니다.
     * 2. Entitiy를 자동수정합니다. (더티체킹)
     *
     * */
    @Override
    @Transactional
    public HardwareDto.resultInfo updateHwAssetService(HardwareDto.hwInfo hwInfo) {
        try {
            if (duplicationPK(hwInfo.getHwNo())) {
                throw new CustomException(ASSET_MODIFY_ERROR_CODE, HttpStatus.BAD_REQUEST, ASSET_MODIFY_ERROR_MESSAGE, null);
            }

            HardwareEntity hardware = hwInfoDtoToHardwareEntity(hwInfo);
            HardwareEntity updateResult = hardwareRepository.save(hardware);

            if (updateResult == null) {
                return HardwareDto.resultInfo.builder()
                        .result(false)
                        .build();
            } else {
                return HardwareDto.resultInfo.builder()
                        .result(true)
                        .build();
            }
        } catch (Exception e) {
            throw Response.makeFailResponse(ASSET_MODIFY_ERROR_CODE, HttpStatus.BAD_REQUEST, ASSET_MODIFY_ERROR_MESSAGE, e);
        }
    }

    // hwInfo DTO -> HardwareEntity 매핑 함수 (수정 시)
    public HardwareEntity hwInfoDtoToHardwareEntity(HardwareDto.hwInfo hwInfo) {
        return HardwareEntity.builder()
                .createId(ADMIN)
                .updateId(ADMIN)
                .deleteYn(false)
                .oldYn(false)
                .failureYn(Response.getOrDefaultBool(hwInfo.getFailureYn()))
                .hwModel(Response.getOrDefaultString(hwInfo.getHwModel()))
                .hwSsd1(assetCommonComponent.validateNumber(hwInfo.getHwSsd1()))
                .hwSsd2(assetCommonComponent.validateNumber(hwInfo.getHwSsd2()))
                .hwHdd1(assetCommonComponent.validateNumber(hwInfo.getHwHdd1()))
                .hwHdd2(assetCommonComponent.validateNumber(hwInfo.getHwHdd2()))
                .hwRam1(assetCommonComponent.validateNumber(hwInfo.getHwRam1()))
                .hwRam2(assetCommonComponent.validateNumber(hwInfo.getHwRam2()))
                .purchaseDate(assetCommonComponent.convertStringToTimestamp(hwInfo.getPurchaseDate()))
                .produceDate(assetCommonComponent.convertStringToTimestamp(hwInfo.getProduceDate()))
                .hwSN(Response.getOrDefaultString(hwInfo.getHwSN()))
                .hwNo(hwInfo.getHwNo())
                .hwRemarks(Response.getOrDefaultString(hwInfo.getHwRemarks()))
                //code와 조인
                .hwLocation(assetCommonComponent.findCodeByPk(hwInfo.getHwLocation().getCode(), false))
                .hwCpu(assetCommonComponent.findCodeByPk(hwInfo.getHwCpu().getCode(), true))
                .hwDiv(assetCommonComponent.findCodeByPk(hwInfo.getHwDiv().getCode(), false))
                .hwMfr(assetCommonComponent.findCodeByPk(hwInfo.getHwMfr().getCode(), false))
                .hwUsage(assetCommonComponent.findCodeByPk(hwInfo.getHwUsage().getCode(), true))
                .hwCategory(assetCommonComponent.findCodeByPk(HARDWARE, false))
                .build();
    }

    /*
     * HW 자산 삭제
     *
     * 1. 리스트로 받은 HW 자산 일련번호들을 조회합니다.
     * 2. 더티체킹하여 Y로 처리합니다.
     *
     * */
    @Override
    @Transactional
    public HardwareDto.resultInfo deleteHwAssetService(HardwareDto.assetNoListInfo assetNoListInfo) {
        try {
            assetNoListInfo.validate();

            for (String assetNo : assetNoListInfo.getAssetNoList()) {
                deleteHardwareByAssetNo(assetNo);
            }

            return HardwareDto.resultInfo.builder()
                    .result(Boolean.TRUE)
                    .build();
        } catch (Exception e) {
            throw Response.makeFailResponse(SELECT_DATABASE_ERROR_CODE, HttpStatus.INTERNAL_SERVER_ERROR, SELECT_DATABASE_ERROR_MESSAGE, null);
        }
    }

    private void deleteHardwareByAssetNo(String assetNo) {
        HardwareEntity hardware = hardwareRepository.findByHwNo(assetNo);
        if (hardware != null) {
            hardware.deleteHardware(true);
        }
    }

    /*
    * 1. HW 등록에 필요한 코드들을 조회합니다.
    * 2. MAP에 담아 응답합니다.
    * */
    @Override
    @Transactional(readOnly = true)
    public AssetCodeDto.assetHwCodeInfo getHwAssetCodeService() {
        try {
            return AssetCodeDto.assetHwCodeInfo.builder()
                    .assetMfr(assetCommonComponent.findCodeByCtg(CATEGORY_ASSET_MFR)) // 하드웨어 제조회사명
                    .assetDiv(assetCommonComponent.findCodeByCtg(CATEGORY_ASSET_DIV_HW)) // 하드웨어 자산종류(모니터, 등등)
                    .assetLocation(assetCommonComponent.findCodeByCtg(CATEGORY_ASSET_LOCATION)) //위치명
                    .assetUsage(assetCommonComponent.findCodeByCtg(CATEGORY_ASSET_USAGE)) // 사용 용도
                    .assetCtg(assetCommonComponent.findCodeByCtg(CATEGORY_ASSET_CTG)) // 자산 카테고리
                    .assetCpu((assetCommonComponent.findCodeByCtg(CATEGORY_ASSET_CPU))) // 하드웨어 CPU
                    .build();
        } catch (Exception e) {
            throw Response.makeFailResponse(CODE_SELECT_CATEGORY_FAIL_CODE, HttpStatus.INTERNAL_SERVER_ERROR, CODE_SELECT_CATEGORY_FAIL_MESSAGE, e);
        }
    }

    // cpu 코드에 해당 없음 추가
    // 20240121 codeTable에 추가했으므로 삭제됨
    private List<AssetCodeDto.codeInfo> insertCpuNA(List<AssetCodeDto.codeInfo> cpuList) {
        AssetCodeDto.codeInfo codeInfo = AssetCodeDto.codeInfo.builder()
                .code(BLANK)
                .codeName(NA)
                .build();

        List<AssetCodeDto.codeInfo> codeInfoList = new ArrayList<>();
        codeInfoList.add(codeInfo);
        codeInfoList.addAll(cpuList);

        return codeInfoList;
    }

    // HW 자산 노후화(4년) 시 oldYn N -> Y
    @Override
    @Transactional
    public void oldHardware() {
        List<HardwareEntity> hardwareList = hardwareRepository.findByOldYn(false);

        for (HardwareEntity hardware : hardwareList) {
            // 자산에 대한 제조정보가 없을 경우는 진행하지 않음
            if (hardware.getPurchaseDate() != null){
                Date pastDate = getPastDate(hardware.getPurchaseDate());
                if (pastDate.getYear() >= OLD_ASSET_YEAR) {
                    hardware.oldHardware(true);
                }
            }
        }
    }

    // 사용 시간 가져오는 함수
    private Date getPastDate(Timestamp pastTime) {
        Timestamp currentTime = new Timestamp(System.currentTimeMillis());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentTime);
        calendar.add(Calendar.YEAR, -pastTime.getYear());
        calendar.add(Calendar.MONTH, -pastTime.getMonth());

        return calendar.getTime();
    }
}
