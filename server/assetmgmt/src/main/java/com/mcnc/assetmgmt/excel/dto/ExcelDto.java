package com.mcnc.assetmgmt.excel.dto;

import com.mcnc.assetmgmt.util.common.CodeAs;
import com.mcnc.assetmgmt.util.exception.CustomException;
import lombok.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;
/**
 * title : ExcelDto
 *
 * description : 엑셀 json request,response 매핑용 dto
 *
 * reference : cannot deserialize from object value 해결법 : https://charactermail.tistory.com/488, https://azurealstn.tistory.com/74
 *             request 받았는데 dto 매핑이 안되는 경우 : https://velog.io/@ssol_916/RequestBody%EB%A1%9C-%EB%B0%9B%EC%95%98%EB%8A%94%EB%8D%B0-null%EC%9D%B8-%EA%B2%BD%EC%9A%B0
 *             기본 생성자의 의미 : https://velog.io/@jakeseo_me/%EA%B0%84%EB%8B%A8%EC%A0%95%EB%A6%AC-%EC%9E%90%EB%B0%94%EC%97%90%EC%84%9C-%EA%B8%B0%EB%B3%B8-%EC%83%9D%EC%84%B1%EC%9E%90%EC%9D%98-%EC%9D%98%EB%AF%B8-feat.-Java-Reflection-Jackson-JPA
 *             빌더 : https://velog.io/@mooh2jj/%EC%98%AC%EB%B0%94%EB%A5%B8-%EC%97%94%ED%8B%B0%ED%8B%B0-Builder-%EC%82%AC%EC%9A%A9%EB%B2%95
 *                   https://pamyferret.tistory.com/67
 *                   https://velog.io/@taegon1998/Spring-%ED%9A%8C%EC%9B%90%EA%B0%80%EC%9E%85-%EA%B5%AC%ED%98%84%ED%95%98%EA%B8%B0DTO-builder
 *             dto 깔끔히 관리하기 : https://velog.io/@p4rksh/Spring-Boot%EC%97%90%EC%84%9C-%EA%B9%94%EB%81%94%ED%95%98%EA%B2%8C-DTO-%EA%B4%80%EB%A6%AC%ED%95%98%EA%B8%B0
 *                                https://velog.io/@aidenshin/DTO%EC%97%90-%EA%B4%80%ED%95%9C-%EA%B3%A0%EC%B0%B0
 *
 * author : 임현영
 * date : 2023.11.21
 **/
public class ExcelDto {
    // 엑셀 파일 요청 dto
    // ex)
    //      "excel": software.xls
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class excelInfo {
        private MultipartFile excel;

        @Builder
        public excelInfo(MultipartFile excel){
            //private 인 경우, 클래스 내부에서만 사용 가능하므로 외부에서 사용할 경우 변수들을 builder로 직접 만들어 사용해야함.
            this.excel = excel;
        }

        public void validate(){
            if(excel.isEmpty()){
                throw new CustomException(CodeAs.REQUEST_FIELD_ERROR_CODE,
                        HttpStatus.BAD_REQUEST,CodeAs.REQUEST_FIELD_ERROR_MESSAGE , null);
            }
        }
    }
    // 엑셀 데이터 삽입 결과 응답 dto
    // ex)
    //      "result":true
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED) //파라미터가 없는 생성자 생성
    @ToString
    public static class resultInfo {
        private Boolean result;

        @Builder
        private resultInfo(Boolean result){
            this.result = result;
        }
    }
}
