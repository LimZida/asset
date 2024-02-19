package com.mcnc.assetmgmt.asset.dto;

import com.mcnc.assetmgmt.asset.entity.HardwareEntity;
import com.mcnc.assetmgmt.util.common.CodeAs;
import com.mcnc.assetmgmt.util.exception.CustomException;
import lombok.*;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;

/**
 * title : HardwareDto
 *
 * description : builder 패턴 사용
 *
 * reference : [Spring Jpa] @Builder 사용법 - https://aamoos.tistory.com/687
 *
 * author : jshong
 *
 * date : 2023-12-11
 **/

public class HardwareDto {
    // HW 자산 등록 요청 dto
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class hwInsertInfo {

        private String hwNo;
        private String hwDiv;
        private String hwLocation;
        private String hwModel;
        private String hwMfr;
        private String hwSN;
        private String hwCpu;
        private Integer hwSsd1;
        private Integer hwSsd2;
        private Integer hwHdd1;
        private Integer hwHdd2;
        private Integer hwRam1;
        private Integer hwRam2;
        private String purchaseDate;
        private String produceDate;
        private String hwUsage;
        private Boolean failureYn;
        private String hwRemarks;

        @Builder
        public hwInsertInfo(String hwNo, String hwDiv, String hwLocation, String hwModel,
                      String hwMfr, String hwSN, String hwCpu, Integer hwSsd1, Integer hwSsd2, Integer hwHdd1,
                            Integer hwHdd2, Integer hwRam1, Integer hwRam2, String purchaseDate, String produceDate,
                      String hwUsage, Boolean failureYn, String hwRemarks) {
            this.hwNo = hwNo;
            this.hwDiv = hwDiv;
            this.hwLocation = hwLocation;
            this.hwModel = hwModel;
            this.hwMfr = hwMfr;
            this.hwSN = hwSN;
            this.hwCpu = hwCpu;
            this.hwSsd1 = hwSsd1;
            this.hwSsd2 = hwSsd2;
            this.hwHdd1 = hwHdd1;
            this.hwHdd2 = hwHdd2;
            this.hwRam1 = hwRam1;
            this.hwRam2 = hwRam2;
            this.purchaseDate = purchaseDate;
            this.produceDate = produceDate;
            this.hwUsage = hwUsage;
            this.failureYn = failureYn;
            this.hwRemarks = hwRemarks;
        }

        /*public void validate() {
            if (hwNo.isEmpty() || hwDiv.isEmpty() || hwLocation.isEmpty() || hwModel.isEmpty()
                    || hwMfr.isEmpty() || hwSN.isEmpty() || hwCpu.isEmpty() || hwSsd1.isEmpty() || hwHdd1.isEmpty()
                    || hwRam1.isEmpty() || purchaseDate.isEmpty() || produceDate.isEmpty()) {
                throw new CustomException(CodeAs.REQUEST_FIELD_ERROR_CODE,
                        HttpStatus.BAD_REQUEST, CodeAs.REQUEST_FIELD_ERROR_MESSAGE, null);
            }

        }*/
    }

    // HW 자산 조회(상세까지 포함) 응답 및 수정 요청 dto
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class hwInfo {

        private AssetCodeDto.codeInfo hwMfr;
        private AssetCodeDto.codeInfo hwCpu;
        private AssetCodeDto.codeInfo hwUsage;
        private AssetCodeDto.codeInfo hwDiv;
        private AssetCodeDto.codeInfo hwCategory;
        private AssetCodeDto.codeInfo hwLocation;
        private HardwareDto.usageInfo usageInfo;
        private String hwNo;
        private String hwModel;
        private String hwSN;
        private Integer hwSsd1;
        private Integer hwSsd2;
        private Integer hwHdd1;
        private Integer hwHdd2;
        private Integer hwRam1;
        private Integer hwRam2;
        private String purchaseDate;
        private String produceDate;
        private Boolean deleteYn;
        private Boolean oldYn;
        private Boolean failureYn;
        private String hwRemarks;
        private String createDate;
        private String updateDate;
        private String useTime;
        @Builder
        public hwInfo(String hwNo, AssetCodeDto.codeInfo hwDiv, AssetCodeDto.codeInfo hwCategory, AssetCodeDto.codeInfo hwLocation,
                            String hwModel, AssetCodeDto.codeInfo hwMfr, String hwSN, AssetCodeDto.codeInfo hwCpu, Integer hwSsd1, Integer hwSsd2, Integer hwHdd1,
                            Integer hwHdd2, Integer hwRam1, Integer hwRam2, String purchaseDate, String produceDate,
                            AssetCodeDto.codeInfo hwUsage, Boolean deleteYn, Boolean oldYn, Boolean failureYn, String hwRemarks,
                            String createDate, String updateDate, String useTime, HardwareDto.usageInfo usageInfo) {
            this.hwNo = hwNo;
            this.hwDiv = hwDiv;
            this.hwCategory = hwCategory;
            this.hwLocation = hwLocation;
            this.hwModel = hwModel;
            this.hwMfr = hwMfr;
            this.hwSN = hwSN;
            this.hwCpu = hwCpu;
            this.hwSsd1 = hwSsd1;
            this.hwSsd2 = hwSsd2;
            this.hwHdd1 = hwHdd1;
            this.hwHdd2 = hwHdd2;
            this.hwRam1 = hwRam1;
            this.hwRam2 = hwRam2;
            this.purchaseDate = purchaseDate;
            this.produceDate = produceDate;
            this.hwUsage = hwUsage;
            this.deleteYn = deleteYn;
            this.oldYn = oldYn;
            this.failureYn = failureYn;
            this.hwRemarks = hwRemarks;
            this.createDate = createDate;
            this.updateDate = updateDate;
            this.useTime = useTime;
            this.usageInfo = usageInfo;
        }

    }

    // HW 자산 사용자 응답 dto
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class usageInfo {
        private AssetCodeDto.codeInfo usageDept;
        private AssetCodeDto.codeInfo assetStatus;
        private String usageName;
        private String usageId;
        @Builder
        public usageInfo(AssetCodeDto.codeInfo usageDept, String usageName, String usageId, AssetCodeDto.codeInfo assetStatus) {
            this.usageDept = usageDept;
            this.usageName = usageName;
            this.usageId = usageId;
            this.assetStatus = assetStatus;
        }
    }

    // 자산 삭제 시 assetNo 리스트 요청 dto
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class assetNoListInfo {
        private List<String> assetNoList;

        @Builder
        private assetNoListInfo(List<String> assetNoList){
            this.assetNoList = assetNoList;
        }

        public void validate() {
            if (assetNoList.isEmpty()) {
                throw new CustomException(CodeAs.REQUEST_FIELD_ERROR_CODE, HttpStatus.BAD_REQUEST, CodeAs.REQUEST_FIELD_ERROR_MESSAGE, null);
            }
        }
    }

    // 결과 T/F dto
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @ToString
    public static class resultInfo {
        private Boolean result;

        @Builder
        private resultInfo(Boolean result){
            this.result = result;
        }
    }
}
