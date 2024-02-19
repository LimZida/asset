package com.mcnc.assetmgmt.asset.service.common;

import com.mcnc.assetmgmt.asset.dto.AssetCodeDto;
import com.mcnc.assetmgmt.code.entity.Code;
import com.mcnc.assetmgmt.code.repository.CodeRepository;
import com.mcnc.assetmgmt.util.common.CodeAs;
import com.mcnc.assetmgmt.util.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * title : AssetCommonComponent
 *
 * description :  HW / SW 서비스 로직에 필요한 공통함수 모아놓은 component
 *
 * reference :  Spring 핵심 원리 기본편 (8) - @Primary, @Qualifier - https://velog.io/@neity16/Spring-%ED%95%B5%EC%8B%AC-%EC%9B%90%EB%A6%AC-%EA%B8%B0%EB%B3%B8%ED%8E%B8-8-Primary-Qualifier
 *              [카카오 면접] @Service,@Controller,@Component 차이 - https://baek-kim-dev.site/64
 *
 * author : 홍지수
 *
 * date : 2023.12.11
 **/
@Component
@RequiredArgsConstructor
public class AssetCommonComponent extends CodeAs {
    private final CodeRepository codeRepository;

    // LocalDateTime -> String 변환
    public String convertLocalDateTimeToString(LocalDateTime date) {
        return date != null ? date.format(DateTimeFormatter.ofPattern(DATE_PATTERN)) : BLANK;
    }

    // Timestamp -> String 변환
    public String convertTimestampToString(Timestamp date) {
        return date != null ? convertLocalDateTimeToString(date.toLocalDateTime()) : BLANK;
    }

    // String -> Timestamp 변환
    public Timestamp convertStringToTimestamp(String date) throws IllegalArgumentException {
        return date.isBlank() ? null : Timestamp.valueOf(date + STARTOFDAY);
    }

    // CodeEntity -> codeInfo DTO 매핑 함수
    public AssetCodeDto.codeInfo codeEntityToAssetCodeDto(Code code) {
        if (code == null) return getNullCodeInfo();

        return AssetCodeDto.codeInfo.builder()
                .code(code.getCode())
                .codeName(code.getCodeName())
                .build();
    }

    // code DTO 빈값일 경우 null처리
    public AssetCodeDto.codeInfo getNullCodeInfo() {
        return AssetCodeDto.codeInfo.builder()
                .code(CodeAs.BLANK)
                .codeName(CodeAs.BLANK)
                .build();
    }

    // Code table에서 매개변수에 해당하는 PK를 찾는 함수
    public Code findCodeByPk(String codePk, boolean nullable) {
        Code code = codeRepository.findByCode(codePk);

        if (nullable && code == null) {
            return null;
        }

        if (code == null) {
            throw new CustomException(CodeAs.CODE_SELECT_PK_FAIL_CODE, HttpStatus.INTERNAL_SERVER_ERROR, CodeAs.CODE_SELECT_PK_FAIL_MESSAGE, null);
        } else {
            return code;
        }
    }

    // Code 테이블 조회 후 List 응답 함수
    public List<AssetCodeDto.codeInfo> findCodeByCtg(String category) {
        List<AssetCodeDto.codeInfo> resList = new ArrayList<>();
        List<Code> codeList = codeRepository.findByCodeCtgAndActiveYn(category , true);

        if (codeList.isEmpty()) {
            throw new CustomException(CODE_SELECT_CATEGORY_FAIL_CODE, HttpStatus.INTERNAL_SERVER_ERROR, CODE_SELECT_CATEGORY_FAIL_MESSAGE, null);
        } else {
            for (Code code : codeList) {
                resList.add(codeEntityToCodeInfoDto(code));
            }
            return resList;
        }
    }

    // CodeEntity -> codeInfo DTO 매핑 함수
    private AssetCodeDto.codeInfo codeEntityToCodeInfoDto(Code code) {
        return AssetCodeDto.codeInfo.builder()
                .code(code.getCode())
                .codeName(code.getCodeName())
                .build();
    }

    // 숫자 (HW - SSD, HDD, RAM / SW - 수량) 0보다 작으면 예외
    public Integer validateNumber(Integer num) {
        if (num < 0) {
            throw new CustomException(ASSET_MMR_UNDER_ZERO_ERROR_CODE, HttpStatus.BAD_REQUEST,
                    ASSET_MMR_UNDER_ZERO_ERROR_MESSAGE, null);
        }

        return num;
    }
}
