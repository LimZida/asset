package com.mcnc.assetmgmt.excel.service.common;

import com.mcnc.assetmgmt.code.entity.Code;
import com.mcnc.assetmgmt.code.repository.CodeRepository;
import com.mcnc.assetmgmt.excel.dto.ExcelDto;
import com.mcnc.assetmgmt.util.common.CodeAs;
import com.mcnc.assetmgmt.util.common.Response;
import com.mcnc.assetmgmt.util.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * title : ExcelCommonComponent
 *
 * description : Excel 공통 기능
 *
 * reference :  상속보다는 컴포지션(조합)을 사용하라: https://stir.tistory.com/389
 *
 * author : 임현영
 * date : 2024.01.17
 **/
@Component
@RequiredArgsConstructor
public class ExcelCommonComponent {
    private final CodeRepository codeRepository;

    // 데이터가 빈값인지 아닌지 확인하는 함수
    public boolean isRowDataNotEmpty(Row row) {
        // 여기에서 row에 데이터가 있는지 여부를 확인하는 로직 작성
        // 예를 들어, 첫 번째 셀의 데이터 유무로 판단하는 등의 방식으로 체크 가능
        Cell firstCell = row.getCell(0);
        return firstCell != null && firstCell.getCellType() != CellType.BLANK;
    }

    // 엑셀 내 문자열을 받아오는 함수
    public String getStringCellValue(Row row, int cellIndex, String Column, String PK) {
        try {
            Cell cell = row.getCell(cellIndex);
            return cell == null ? Response.getOrDefaultString(null):
                    Response.getOrDefaultString(cell.getStringCellValue());
        }catch (Exception e){
            throw Response.makeFailResponse(CodeAs.ASSET_EXCEL_STR_CHANGE_ERROR_CODE, HttpStatus.INTERNAL_SERVER_ERROR
                    , PK+" 자산번호에 대한 "+Column+" : "+CodeAs.ASSET_EXCEL_STR_CHANGE_ERROR_MESSAGE, e);
        }
    }

    //엑셀 내 숫자를 받아오는 함수
    public int getNumericCellValue(Row row, int cellIndex, String Column, String PK) {
        try {
            Cell cell = row.getCell(cellIndex);
            return cell == null ? 0:(int) Math.floor(cell.getNumericCellValue());

        }catch (Exception e){
            throw Response.makeFailResponse(CodeAs.ASSET_EXCEL_INT_CHANGE_ERROR_CODE, HttpStatus.INTERNAL_SERVER_ERROR
                    , PK+" 자산번호에 대한 "+Column+" : "+CodeAs.ASSET_EXCEL_INT_CHANGE_ERROR_MESSAGE, e);
        }
    }

    // 현재 시간으로 변환하는 함수
    public Timestamp convertDateToTimestamp(String date) {
        try {
            if(date.startsWith(CodeAs.NULL_TEXT)){
                return null;
            }
            // 문자열을 LocalDate로 변환
            LocalDate localDate = LocalDate.parse(date);

            // LocalDate를 Timestamp으로 변환
            Timestamp timestamp = Timestamp.valueOf(localDate.atStartOfDay());

            return timestamp;
        } catch (Exception e) {
            throw Response.makeFailResponse(CodeAs.ASSET_EXCEL_TIME_CONVERT_ERROR_CODE,
                    HttpStatus.INTERNAL_SERVER_ERROR, CodeAs.ASSET_EXCEL_TIME_CONVERT_ERROR_MESSAGE, e);
        }
    }

    //PK를 찾는 함수
    public Code findCodeByPk(String codePk){
        Code code = codeRepository.findByCodeAndActiveYn(codePk ,true);
        if(code == null){
            throw new CustomException(CodeAs.CODE_SELECT_PK_FAIL_CODE, HttpStatus.INTERNAL_SERVER_ERROR,
                    codePk+" : "+CodeAs.CODE_SELECT_PK_FAIL_MESSAGE,null);
        }else{
            return code;
        }
    }

    //DTO 생성 함수
    public ExcelDto.resultInfo getResult(){
        return ExcelDto.resultInfo.builder()
                .result(true)
                .build();
    }

    // 숫자 (HW - SSD, HDD, RAM / SW - 수량) 0보다 작으면 예외
    public Integer validateNumber(Integer num) {
        if (num < 0) {
            throw new CustomException(CodeAs.ASSET_MMR_UNDER_ZERO_ERROR_CODE, HttpStatus.BAD_REQUEST,
                    CodeAs.ASSET_MMR_UNDER_ZERO_ERROR_MESSAGE, null);
        }

        return num;
    }
}
