package com.mcnc.assetmgmt.asset.service.impl;

import com.mcnc.assetmgmt.asset.service.SoftwareService;
import com.mcnc.assetmgmt.asset.service.common.AssetCommonComponent;
import com.mcnc.assetmgmt.asset.dto.AssetCodeDto;
import com.mcnc.assetmgmt.asset.dto.SoftwareDto;
import com.mcnc.assetmgmt.asset.entity.SoftwareEntity;
import com.mcnc.assetmgmt.asset.repository.SoftwareRepository;
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
import java.util.List;

/**
 * title : SoftwareServiceImpl
 *
 * description :  SoftwareService의 구현체
 *
 * reference :  올바른 엔티티 수정 방법: https://www.inflearn.com/questions/15944/%EA%B0%95%EC%9D%98-%EC%A4%91%EC%97%90-%EA%B0%92%ED%83%80%EC%9E%85%EC%9D%98-%EC%88%98%EC%A0%95%EC%97%90-%EB%8C%80%ED%95%B4%EC%84%9C-%EB%8B%A4%EB%A4%84%EC%A3%BC%EC%85%A8%EB%8A%94%EB%8D%B0%EC%9A%94-%EB%AC%B8%EB%93%9D-%EC%97%94%ED%8B%B0%ED%8B%B0-%EC%88%98%EC%A0%95%EC%97%90-%EB%8C%80%ED%95%B4%EC%84%9C-%EA%B6%81%EA%B8%88%ED%95%B4%EC%A0%B8-%EC%A7%88%EB%AC%B8%EB%93%9C%EB%A6%BD%EB%8B%88%EB%8B%A4
 *              DTO의 적용 범위 https://tecoble.techcourse.co.kr/post/2021-04-25-dto-layer-scope/
 *              Spring 핵심 원리 기본편 (8) - @Primary, @Qualifier - https://velog.io/@neity16/Spring-%ED%95%B5%EC%8B%AC-%EC%9B%90%EB%A6%AC-%EA%B8%B0%EB%B3%B8%ED%8E%B8-8-Primary-Qualifier
 *
 * author : 임현영
 *
 * date : 2023.12.13
 **/
@Service
@RequiredArgsConstructor
@Slf4j
public class SoftwareServiceImpl extends CodeAs implements SoftwareService {
    private final SoftwareRepository softwareRepository;
    private final AssignmentRepository assignmentRepository;
    private final AssetCommonComponent assetCommonComponent;


    /*
     * SW 자산 리스트 조회
     *
     * 1. API 로드 시 만료된 SW를 update(expireYn N -> Y) 합니다.
     * 2. 사용하고 있는 SW를 조회합니다.
     * 3. 해당 결과를 매핑해 응답합니다.
     *
     * */
    @Override
    @Transactional(readOnly = true)
    public List<SoftwareDto.swInfo> getSwAssetListService() {
        try{
            List<SoftwareEntity> swList = softwareRepository.findByDeleteYnOrderByCreateDateDesc(false);
            List<SoftwareDto.swInfo> res = new ArrayList<>();

            for (SoftwareEntity sw : swList) {
                res.add(softwareEntityToSwSelectInfoDto(sw));
            }

            return res;
        } catch (Exception e) {
            throw Response.makeFailResponse(ASSET_SELECT_ERROR_CODE, HttpStatus.BAD_REQUEST, ASSET_SELECT_ERROR_MESSAGE, e);
        }
    }

    // SoftwareEntity -> swSelectInfo DTO 매핑 함수
    private SoftwareDto.swInfo softwareEntityToSwSelectInfoDto(SoftwareEntity swAsset) {
        return SoftwareDto.swInfo.builder()
                .swNo(swAsset.getSwNo())
                .swName(swAsset.getSwName())
                .swRemarks(swAsset.getSwRemarks())
                .purchaseDate(assetCommonComponent.convertTimestampToString(swAsset.getPurchaseDate()))
                .expireDate(assetCommonComponent.convertTimestampToString(swAsset.getExpireDate()))
                .expireYn(swAsset.getExpireYn())
                .deleteYn(swAsset.getDeleteYn())
                .swSN(swAsset.getSwSN())
                .swQuantity(swAsset.getSwQuantity())
                .createDate(assetCommonComponent.convertLocalDateTimeToString(swAsset.getCreateDate()))
                .updateDate(assetCommonComponent.convertLocalDateTimeToString(swAsset.getUpdateDate()))
                //조인
                .swDiv(assetCommonComponent.codeEntityToAssetCodeDto(swAsset.getSwDiv()))
                .swCategory(assetCommonComponent.codeEntityToAssetCodeDto(swAsset.getSwCategory()))
                .swLocation(assetCommonComponent.codeEntityToAssetCodeDto(swAsset.getSwLocation()))
                .swMfr(assetCommonComponent.codeEntityToAssetCodeDto(swAsset.getSwMfr()))
                .swOS(assetCommonComponent.codeEntityToAssetCodeDto(swAsset.getSwOS()))
                .swOSVersion(assetCommonComponent.codeEntityToAssetCodeDto(swAsset.getSwOSVersion()))
                .swLicense(assetCommonComponent.codeEntityToAssetCodeDto(swAsset.getSwLicense()))
                .swUsage(assetCommonComponent.codeEntityToAssetCodeDto(swAsset.getSwUsage()))
                // 사용자 정보 리스트
                .usageInfoList(getUsageInfoList(swAsset.getSwNo()))
                .build();
    }

    //사용자 정보 응답 함수
    private List<SoftwareDto.usageInfo> getUsageInfoList(String swNo) {
        List<AssignmentEntity> assignmentList = assignmentRepository.findByAssetNoAndDeleteYnAndAssetStatusOrderByCreateDateDesc(swNo, NEGATIVE,
                assetCommonComponent.findCodeByPk(STATUS02, false));

        List<SoftwareDto.usageInfo> usageList = new ArrayList<>();

        for (AssignmentEntity assignment : assignmentList) {
            if (assignment == null) break;

            SoftwareDto.usageInfo dto = SoftwareDto.usageInfo.builder()
                        .usageDept(assetCommonComponent.codeEntityToAssetCodeDto(assignment.getUsageDept()))
                        .usageName(assignment.getUsageName())
                        .usageId(assignment.getUsageId()).build();

            usageList.add(dto);
        }

        return usageList;
    }

    /*
     * SW 자산 추가
     *
     * 1. 요청으로 받은 SW DTO 정보들을 Entity와 매핑합니다.
     * 2. PK 중복체크를 합니다. (중복이면 예외 발생)
     * 3. Entitiy를 save합니다.
     *
     * */
    @Override
    @Transactional
    public SoftwareDto.resultInfo insertSwAssetService(SoftwareDto.swInsertInfo swInsertInfo) {
        try {
            if (!duplicationPK(swInsertInfo.getSwNo())) { // PK 중복 체크
                throw new CustomException(REQUEST_DUPLICATE_PK_CODE, HttpStatus.INTERNAL_SERVER_ERROR, REQUEST_DUPLICATE_PK_MESSAGE, null);
            }

            SoftwareEntity software = swInfoDtoToSoftwareEntity(swInsertInfo);
            SoftwareEntity insertResult = softwareRepository.save(software);

            if (insertResult == null) {
                return SoftwareDto.resultInfo.builder()
                        .result(false)
                        .build();
            } else {
                return SoftwareDto.resultInfo.builder()
                        .result(true)
                        .build();
            }
        } catch (Exception e) {
            throw Response.makeFailResponse(ASSET_REGISTER_ERROR_CODE, HttpStatus.BAD_REQUEST, ASSET_REGISTER_ERROR_MESSAGE, e);
        }

    }

    // swInfo DTO -> SoftwareEntity 매핑 (등록 시)
    private SoftwareEntity swInfoDtoToSoftwareEntity(SoftwareDto.swInsertInfo swInsertInfo) {
        return SoftwareEntity.builder()
                .createId(ADMIN)
                .updateId(ADMIN)
                .deleteYn(false)
                .expireYn(false)
                .swNo(swInsertInfo.getSwNo())
                .swName(Response.getOrDefaultString(swInsertInfo.getSwName()))
                .swRemarks(Response.getOrDefaultString(swInsertInfo.getSwRemarks()))
                .purchaseDate(assetCommonComponent.convertStringToTimestamp(swInsertInfo.getPurchaseDate()))
                .expireDate(assetCommonComponent.convertStringToTimestamp(swInsertInfo.getExpireDate()))
                .swSN(Response.getOrDefaultString(swInsertInfo.getSwSN()))
                .swQuantity(assetCommonComponent.validateNumber(swInsertInfo.getSwQuantity()))
                //code와 조인
                .swDiv(assetCommonComponent.findCodeByPk(swInsertInfo.getSwDiv(), false))
                .swLocation(assetCommonComponent.findCodeByPk(swInsertInfo.getSwLocation(), false))
                .swMfr(assetCommonComponent.findCodeByPk(swInsertInfo.getSwMfr(), false))
                .swCategory(assetCommonComponent.findCodeByPk(SOFTWARE, false))
                .swOS(assetCommonComponent.findCodeByPk(swInsertInfo.getSwOS(), true))
                .swOSVersion(assetCommonComponent.findCodeByPk(swInsertInfo.getSwOSVersion(), true))
                .swLicense(assetCommonComponent.findCodeByPk(swInsertInfo.getSwLicense(), true))
                .swUsage(assetCommonComponent.findCodeByPk(swInsertInfo.getSwUsage(), true))
                .build();
    }

    // SW 자산 등록 시 PK 중복 확인
    private boolean duplicationPK(String swNo) {
        return softwareRepository.findBySwNo(swNo) == null;
    }

    /*
     * SW 자산 수정
     *
     * 1. 요청으로 받은 SW DTO 정보들을 Entity와 매핑합니다.
     * POST(CREATE)요청이라면 PK 중복체크를 합니다. (중복이면 예외 발생)
     * 2. Entitiy를 save합니다. (PK가 없으면 추가, 있으면 자동 수정)
     *
     * */
    @Override
    @Transactional
    public SoftwareDto.resultInfo updateSwAssetService(SoftwareDto.swInfo swInfo) {
        try {
            if (duplicationPK(swInfo.getSwNo())) { // PK 중복 체크
                throw new CustomException(ASSET_MODIFY_ERROR_CODE, HttpStatus.BAD_REQUEST, ASSET_MODIFY_ERROR_MESSAGE, null);
            }

            SoftwareEntity software = swInfoDtoToSoftwareEntity(swInfo);
            SoftwareEntity updateResult = softwareRepository.save(software);

            if (updateResult == null) {
                return SoftwareDto.resultInfo.builder()
                        .result(false)
                        .build();
            } else {
                return SoftwareDto.resultInfo.builder()
                        .result(true)
                        .build();
            }
        } catch (Exception e) {
            throw Response.makeFailResponse(ASSET_MODIFY_ERROR_CODE, HttpStatus.BAD_REQUEST, ASSET_MODIFY_ERROR_MESSAGE, e);
        }

    }

    // swInfo DTO -> SoftwareEntity 매핑
    private SoftwareEntity swInfoDtoToSoftwareEntity(SoftwareDto.swInfo swInfo) {
        return SoftwareEntity.builder()
                .createId(ADMIN)
                .updateId(ADMIN)
                .deleteYn(false)
                .expireYn(false)
                .swNo(swInfo.getSwNo())
                .swName(Response.getOrDefaultString(swInfo.getSwName()))
                .swRemarks(Response.getOrDefaultString(swInfo.getSwRemarks()))
                .purchaseDate(assetCommonComponent.convertStringToTimestamp(swInfo.getPurchaseDate()))
                .expireDate(assetCommonComponent.convertStringToTimestamp(swInfo.getExpireDate()))
                .swSN(Response.getOrDefaultString(swInfo.getSwSN()))
                .swQuantity(assetCommonComponent.validateNumber(swInfo.getSwQuantity()))
                //code와 조인
                .swDiv(assetCommonComponent.findCodeByPk(swInfo.getSwDiv().getCode(), false))
                .swLocation(assetCommonComponent.findCodeByPk(swInfo.getSwLocation().getCode(), false))
                .swMfr(assetCommonComponent.findCodeByPk(swInfo.getSwMfr().getCode(), false))
                .swCategory(assetCommonComponent.findCodeByPk(SOFTWARE, false))
                .swOS(assetCommonComponent.findCodeByPk(swInfo.getSwOS().getCode(), true))
                .swOSVersion(assetCommonComponent.findCodeByPk(swInfo.getSwOSVersion().getCode(), true))
                .swLicense(assetCommonComponent.findCodeByPk(swInfo.getSwLicense().getCode(), true))
                .swUsage(assetCommonComponent.findCodeByPk(swInfo.getSwUsage().getCode(), true))
                .build();
    }

    /*
     * SW 자산 삭제
     *
     * 1. 리스트로 받은 SW자산 일련번호들을 조회합니다.
     * 2. 더티체킹하여 Y로 처리합니다.
     *
     * */
    @Override
    @Transactional
    public SoftwareDto.resultInfo deleteSwAssetService(SoftwareDto.assetNoListInfo assetNoListInfo) {
        try {
            assetNoListInfo.validate();

            for (String assetNo : assetNoListInfo.getAssetNoList()) {
                deleteSoftwareByAssetNo(assetNo);
            }

            return SoftwareDto.resultInfo.builder()
                    .result(true)
                    .build();
        } catch (Exception e) {
            throw Response.makeFailResponse(ASSET_DELETE_ERROR_CODE, HttpStatus.BAD_REQUEST, ASSET_DELETE_ERROR_MESSAGE, e);
        }
    }

    private void deleteSoftwareByAssetNo(String assetNo) {
        SoftwareEntity software = softwareRepository.findBySwNo(assetNo);
        if (software != null) {
            software.deleteSoftware(true);
        }
    }

    /*
     * SW 자산 등록 / 수정 시 코드 리스트 응답
     *
     * 1. HW 등록에 필요한 코드들을 조회합니다.
     * 2. MAP에 담아 응답합니다.
     * */
    @Override
    @Transactional(readOnly = true)
    public AssetCodeDto.assetSwCodeInfo getSwAssetCodeService() {
        try {
             return AssetCodeDto.assetSwCodeInfo.builder()
                    .assetMfr(assetCommonComponent.findCodeByCtg(CATEGORY_ASSET_MFR)) // SW 제조사명
                    .assetDiv(assetCommonComponent.findCodeByCtg(CATEGORY_ASSET_DIV_SW)) // SW 종류(자산명)
                    .assetLocation(assetCommonComponent.findCodeByCtg(CATEGORY_ASSET_LOCATION)) // SW 증빙 위치
                    .assetUsage(assetCommonComponent.findCodeByCtg(CATEGORY_ASSET_USAGE)) // SW 사용 용도
                    .assetCtg(assetCommonComponent.findCodeByCtg(CATEGORY_ASSET_CTG)) // 자산 카테고리
                    .assetOS(assetCommonComponent.findCodeByCtg(CATEGORY_ASSET_OS)) // SW 프로그램 / 운영체제
                    .assetOSVersion(assetCommonComponent.findCodeByCtg(CATEGORY_ASSET_OS_VERSION)) // SW 프로그램 / 운영체제 버전
                    .assetLicense(assetCommonComponent.findCodeByCtg(CATEGORY_ASSET_LICENSE)) // SW 라이센스 유형
                    .build();
        } catch (Exception e) {
            throw Response.makeFailResponse(CODE_SELECT_CATEGORY_FAIL_CODE, HttpStatus.INTERNAL_SERVER_ERROR, CODE_SELECT_CATEGORY_FAIL_MESSAGE, e);
        }
    }

    // 자산 만료시 expireYn N -> Y
    @Override
    @Transactional
    public void expireSoftware() {
        List<SoftwareEntity> softwareList = softwareRepository.findByExpireYn(false);
        Timestamp currentTime = new Timestamp(System.currentTimeMillis());

        for (SoftwareEntity sw : softwareList) {
            Timestamp expireDate = sw.getExpireDate();
            if (sw.getExpireDate() != null && expireDate.before(currentTime)) {
                sw.expireSoftware(true);
            }
        }
    }

    // SW 라이선스 영구일 경우 expireDate 들어왔을 때 null 처리
    private Timestamp validateLicenseAndExpireDate(String licenseCode, String expireDate) {
        Timestamp timestamp = assetCommonComponent.convertStringToTimestamp(expireDate);

        return licenseCode.equals(PERMANENT_CODE) && timestamp != null ? null : timestamp;
    }
}
