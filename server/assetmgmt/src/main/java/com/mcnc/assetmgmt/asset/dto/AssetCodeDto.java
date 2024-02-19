package com.mcnc.assetmgmt.asset.dto;

import lombok.*;

import java.util.List;

/**
 * title : AssetCodeDto
 *
 * description :  HW / SW 코드 응답에 필요한 CodeInfo DTO
 *
 * reference :
 *
 * author : 임현영
 *
 * date : 2023.11.29
 **/
public class AssetCodeDto {
    // Hw자산 등록 시 asset code map 응답 dto
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @ToString
    public static class assetHwCodeInfo {
        private List<codeInfo> assetMfr;
        private List<codeInfo> assetDiv;
        private List<codeInfo> assetLocation;
        private List<codeInfo> assetUsage;
        private List<codeInfo> assetCtg;
        private List<codeInfo> assetCpu;


        @Builder
        private assetHwCodeInfo(List<codeInfo> assetMfr, List<codeInfo> assetDiv, List<codeInfo> assetLocation,
                              List<codeInfo> assetUsage, List<codeInfo> assetCtg, List<codeInfo> assetCpu) {
            this.assetMfr = assetMfr;
            this.assetDiv = assetDiv;
            this.assetLocation = assetLocation;
            this.assetUsage = assetUsage;
            this.assetCtg = assetCtg;
            this.assetCpu = assetCpu;
        }
    }

    // Sw자산 등록 시 asset code map 응답 dto
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @ToString
    public static class assetSwCodeInfo {
        private List<codeInfo> assetMfr;
        private List<codeInfo> assetDiv;
        private List<codeInfo> assetLocation;
        private List<codeInfo> assetUsage;
        private List<codeInfo> assetCtg;
        private List<codeInfo> assetOS;
        private List<codeInfo> assetOSVersion;
        private List<codeInfo> assetLicense;


        @Builder
        private assetSwCodeInfo(List<codeInfo> assetMfr, List<codeInfo> assetDiv, List<codeInfo> assetLocation,
                                List<codeInfo> assetUsage, List<codeInfo> assetCtg, List<codeInfo> assetOS,
                                List<codeInfo> assetOSVersion, List<codeInfo> assetLicense){

            this.assetMfr = assetMfr;
            this.assetDiv = assetDiv;
            this.assetLocation = assetLocation;
            this.assetUsage = assetUsage;
            this.assetCtg = assetCtg;
            this.assetOS = assetOS;
            this.assetOSVersion = assetOSVersion;
            this.assetLicense = assetLicense;
        }
    }

    // HW & SW 자산 등록/수정 시 코드 응답 dto
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class codeInfo {
        private String code;
        private String codeName;

        @Builder
        private codeInfo(String code, String codeName) {
            this.code = code;
            this.codeName = codeName;
        }
    }
}
