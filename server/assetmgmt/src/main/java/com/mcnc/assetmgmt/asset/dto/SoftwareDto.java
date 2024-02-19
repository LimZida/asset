package com.mcnc.assetmgmt.asset.dto;

import com.mcnc.assetmgmt.util.common.CodeAs;
import com.mcnc.assetmgmt.util.exception.CustomException;
import lombok.*;
import org.springframework.http.HttpStatus;

import java.util.List;

/**
 * title : SoftwareDto
 *
 * description : builder 패턴 사용
 *
 * reference : [Spring Jpa] @Builder 사용법 - https://aamoos.tistory.com/687
 *
 * author : jshong
 *
 * date : 2023-12-06
 **/

public class SoftwareDto {
    // SW 자산 등록 요청 dto
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class swInsertInfo {
        private String swNo;
        private String swDiv;
        private String swLocation;
        private String swName;
        private String swMfr;
        private String swRemarks;
        private String purchaseDate;
        private String expireDate;
        private String swSN;
        private String swOS;
        private String swOSVersion;
        private String swLicense;
        private Integer swQuantity;
        private String swUsage;

        @Builder
        public swInsertInfo(String swNo, String swDiv, String swLocation, String swName, String swMfr, String swRemarks,
                      String purchaseDate, String expireDate, String swSN, String swOS,
                      String swOSVersion, String swLicense, Integer swQuantity, String swUsage) {
            this.swNo = swNo;
            this.swDiv = swDiv;
            this.swLocation = swLocation;
            this.swName = swName;
            this.swMfr = swMfr;
            this.swRemarks = swRemarks;
            this.purchaseDate = purchaseDate;
            this.expireDate = expireDate;
            this.swSN = swSN;
            this.swOS = swOS;
            this.swOSVersion = swOSVersion;
            this.swLicense = swLicense;
            this.swQuantity = swQuantity;
            this.swUsage = swUsage;
        }

        public void validate() {
            if (swNo.isEmpty() || swDiv.isEmpty() || swLocation.isEmpty() || swName.isEmpty() || swMfr.isEmpty()
                    || purchaseDate.isEmpty() || swSN.isEmpty() || swOS.isEmpty()
                    || swOSVersion.isEmpty() || swLicense.isEmpty() || swQuantity < 0) {
                throw new CustomException(CodeAs.REQUEST_FIELD_ERROR_CODE,
                        HttpStatus.BAD_REQUEST, CodeAs.REQUEST_FIELD_ERROR_MESSAGE, null);
            }
        }
    }

    // SW 자산 조회(상세까지 포함) 응답 및 수정 요청 dto
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class swInfo {
        private List<SoftwareDto.usageInfo> usageInfoList;
        private AssetCodeDto.codeInfo swOS;
        private AssetCodeDto.codeInfo swDiv;
        private AssetCodeDto.codeInfo swLocation;
        private AssetCodeDto.codeInfo swMfr;
        private AssetCodeDto.codeInfo swOSVersion;
        private AssetCodeDto.codeInfo swLicense;
        private AssetCodeDto.codeInfo swUsage;
        private AssetCodeDto.codeInfo swCategory;
        private String swNo;
        private String swName;
        private String swRemarks;
        private String purchaseDate;
        private String expireDate;
        private Boolean expireYn;
        private Boolean deleteYn;
        private String swSN;
        private Integer swQuantity;
        private String createDate;
        private String updateDate;

        @Builder
        public swInfo(String swNo, AssetCodeDto.codeInfo swDiv, AssetCodeDto.codeInfo swLocation, String swName, AssetCodeDto.codeInfo swMfr, String swRemarks,
                            String purchaseDate, String expireDate, Boolean expireYn, Boolean deleteYn, String swSN, AssetCodeDto.codeInfo swOS,
                            AssetCodeDto.codeInfo swOSVersion, AssetCodeDto.codeInfo swLicense, Integer swQuantity, String createDate,
                            String updateDate, AssetCodeDto.codeInfo swUsage, AssetCodeDto.codeInfo swCategory, List<SoftwareDto.usageInfo> usageInfoList) {
            this.swNo = swNo;
            this.swDiv = swDiv;
            this.swLocation = swLocation;
            this.swName = swName;
            this.swMfr = swMfr;
            this.swRemarks = swRemarks;
            this.purchaseDate = purchaseDate;
            this.expireDate = expireDate;
            this.expireYn = expireYn;
            this.deleteYn = deleteYn;
            this.swSN = swSN;
            this.swOS = swOS;
            this.swOSVersion = swOSVersion;
            this.swLicense = swLicense;
            this.swQuantity = swQuantity;
            this.createDate = createDate;
            this.updateDate = updateDate;
            this.swUsage = swUsage;
            this.swCategory = swCategory;
            this.usageInfoList = usageInfoList;
        }
    }

    // SW 자산 사용자 응답 dto
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class usageInfo {
        private AssetCodeDto.codeInfo usageDept;
        private String usageName;
        private String usageId;

        @Builder
        public usageInfo(AssetCodeDto.codeInfo usageDept, String usageName, String usageId) {
            this.usageDept = usageDept;
            this.usageName = usageName;
            this.usageId = usageId;
        }
    }

    // 자산 삭제 시 assetNo 리스트 요청 dto
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class assetNoListInfo {
        private List<String> assetNoList;

        @Builder
        private assetNoListInfo(List<String> assetNoList) {
            this.assetNoList = assetNoList;
        }

        public void validate(){
            if (assetNoList.isEmpty()) {
                throw new CustomException(CodeAs.REQUEST_FIELD_ERROR_CODE, HttpStatus.BAD_REQUEST ,CodeAs.REQUEST_FIELD_ERROR_MESSAGE, null);
            }
        }
    }

    // 결과 T/F dto
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class resultInfo {
        private Boolean result;

        @Builder
        private resultInfo(Boolean result) {
            this.result = result;
        }
    }
}
