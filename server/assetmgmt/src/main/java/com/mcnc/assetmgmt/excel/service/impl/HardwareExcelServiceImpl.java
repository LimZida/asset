package com.mcnc.assetmgmt.excel.service.impl;

import com.mcnc.assetmgmt.asset.entity.HardwareEntity;
import com.mcnc.assetmgmt.asset.repository.HardwareRepository;
import com.mcnc.assetmgmt.excel.service.HardwareExcelService;
import com.mcnc.assetmgmt.excel.service.common.ExcelCommonComponent;
import com.mcnc.assetmgmt.excel.dto.ExcelDto;
import com.mcnc.assetmgmt.util.common.CodeAs;
import com.mcnc.assetmgmt.util.common.Response;
import com.mcnc.assetmgmt.util.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.sql.Timestamp;
import java.util.Iterator;
/**
 * title : HardwareExcelServiceImpl
 *
 * description : HardwareExcelServiceImpl 구현체 , 하드웨어 엑셀 다운, 조회 기능 로직
 *
 * reference :  올바른 엔티티 수정 방법: https://www.inflearn.com/questions/15944/%EA%B0%95%EC%9D%98-%EC%A4%91%EC%97%90-%EA%B0%92%ED%83%80%EC%9E%85%EC%9D%98-%EC%88%98%EC%A0%95%EC%97%90-%EB%8C%80%ED%95%B4%EC%84%9C-%EB%8B%A4%EB%A4%84%EC%A3%BC%EC%85%A8%EB%8A%94%EB%8D%B0%EC%9A%94-%EB%AC%B8%EB%93%9D-%EC%97%94%ED%8B%B0%ED%8B%B0-%EC%88%98%EC%A0%95%EC%97%90-%EB%8C%80%ED%95%B4%EC%84%9C-%EA%B6%81%EA%B8%88%ED%95%B4%EC%A0%B8-%EC%A7%88%EB%AC%B8%EB%93%9C%EB%A6%BD%EB%8B%88%EB%8B%A4
 *
 *              DTO의 적용 범위 https://tecoble.techcourse.co.kr/post/2021-04-25-dto-layer-scope/
 *              더티체킹 : https://jojoldu.tistory.com/415
 *
 * author : 임현영
 * date : 2023.11.21
 **/
@Service
@RequiredArgsConstructor
@Slf4j
public class HardwareExcelServiceImpl implements HardwareExcelService {
    private final HardwareRepository hardwareRepository;
    private final ExcelCommonComponent excelCommonComponent;

    @Override
    @Transactional
    public ExcelDto.resultInfo readAndInsertHwData(ExcelDto.excelInfo excelInfo) {
        /*
        * 1. dto를 통해 Excel 파일을 받아옵니다.
        * 2. 받아온 Excel 파일 양식대로 row column을 설정하여 데이터를 읽어들입니다.
        * 3. 읽어들인 데이터를 매핑후 HW 테이블에 저장합니다.
        * */
        try (Workbook workbook = new XSSFWorkbook(excelInfo.getExcel().getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);

            Iterator<Row> rowIterator = sheet.iterator();
            // 2번째 상위까지 컬럼은 제외
            if (rowIterator.hasNext()) {
                rowIterator.next();
                rowIterator.next();
            }

            // 엑셀 파일 읽어오며 차례로 Insert
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                // PK 데이터가 없을 경우 대비
                if (excelCommonComponent.isRowDataNotEmpty(row)) {
                    HardwareEntity hardware = mapRowToHardwareEntity(row);

                    HardwareEntity saveResult = hardwareRepository.save(hardware);
                    if (saveResult == null) {
                        throw new CustomException(CodeAs.ASSET_HW_EXCEL_INSERT_ERROR_CODE, HttpStatus.INTERNAL_SERVER_ERROR
                                , CodeAs.ASSET_HW_EXCEL_INSERT_ERROR_MESSAGE, null);
                    }
                }
            }
            ExcelDto.resultInfo res = excelCommonComponent.getResult();
            return res;
        } catch (Exception e) {
            throw Response.makeFailResponse(CodeAs.ASSET_HW_EXCEL_SELECT_ERROR_CODE, HttpStatus.INTERNAL_SERVER_ERROR
                    , CodeAs.ASSET_HW_EXCEL_SELECT_ERROR_MESSAGE, e);
        }
    }

    @Override
    public void downloadHardwareExcelFileService(HttpServletResponse response) {
        try {
            //Workbook wb = new HSSFWorkbook(); // xls
            Workbook wb = new XSSFWorkbook(); // xlsx
            Sheet sheet = wb.createSheet("hardware");
            Row row = null;
            Cell cell = null;
            int rowNum = 0;

            // 가장 상위단 컬럼
            row = sheet.createRow(rowNum++);
            cell = row.createCell(0);
            cell.setCellValue("필독! HW_DIV(자산 종류), HW_CATEGORY(자산 범주), HW_LOCATION(자산 위치), HW_CPU (CPU), HW_MFR (제조사), HW_USAGE (용도 구분)은 '코드 관리'에서 지정한 코드네임으로 입력합니다. 아래 예시를 참고해 자산을 작성해주세요." +System.lineSeparator()+System.lineSeparator()+
                    "1. SSD1, SSD2, HDD1, HDD2, RAM1, RAM2의 경우 엑셀 내 표시 형식을 숫자로 맞춰 진행부탁드립니다. " +System.lineSeparator()+
                    "ex) 512G => 512" +System.lineSeparator()+
                    "만약 메모리가 없다면 공란으로 맞춰 진행 부탁드립니다."+System.lineSeparator()+System.lineSeparator()+
                    "2. PURCHASE_DATE (구매일자), PRODUCE_DATE(제조일자)의 경우"+System.lineSeparator()+
                    "엑셀 내 표시 형식을 텍스트로 맞춰 진행부탁드립니다."+System.lineSeparator()+
                    "ex) 2024-01-23"+System.lineSeparator()+
                    "만약 제조일자 혹은 구매일자가 파악이 불가한 경우 공란으로  기입 부탁드립니다."+System.lineSeparator()+System.lineSeparator()+
                    "3. HW_REMARKS (비고)의 경우"+System.lineSeparator()+
                    "엑셀 내 표시 형식을 텍스트로 맞춰 진행부탁드립니다. 만약 비고가 공란일 경우 공란으로 기입 부탁드립니다."
                    );
            // 그다음단 컬럼
            row = sheet.createRow(rowNum++);
            cell = row.createCell(0);
            cell.setCellValue("HW_NO(자산 일련번호)");
            cell = row.createCell(1);
            cell.setCellValue("HW_DIV(자산 종류)");
            cell = row.createCell(2);
            cell.setCellValue("HW_CATEGORY(자산 범주)");
            cell = row.createCell(3);
            cell.setCellValue("HW_LOCATION(자산 위치)");
            cell = row.createCell(4);
            cell.setCellValue("PRODUCE_DATE(제조일자)");
            cell = row.createCell(5);
            cell.setCellValue("HW_CPU (CPU)");
            cell = row.createCell(6);
            cell.setCellValue("HW_SSD1 (SSD)");
            cell = row.createCell(7);
            cell.setCellValue("HW_SSD2 (SSD)");
            cell = row.createCell(8);
            cell.setCellValue("HW_HDD1 (HDD)");
            cell = row.createCell(9);
            cell.setCellValue("HW_HDD2 (HDD)");
            cell = row.createCell(10);
            cell.setCellValue("HW_RAM1 (RAM)");
            cell = row.createCell(11);
            cell.setCellValue("HW_RAM2 (RAM)");
            cell = row.createCell(12);
            cell.setCellValue("HW_MODEL (모델명)");
            cell = row.createCell(13);
            cell.setCellValue("HW_MFR (제조사)");
            cell = row.createCell(14);
            cell.setCellValue("HW_USAGE (용도 구분)");
            cell = row.createCell(15);
            cell.setCellValue("HW_REMARKS (비고)");
            cell = row.createCell(16);
            cell.setCellValue("PURCHASE_DATE (구매일자)");
            cell = row.createCell(17);
            cell.setCellValue("HW_SN (시리얼넘버)");
            cell = row.createCell(18);
            cell.setCellValue("OLD_YN (노후화 여부)");
            cell = row.createCell(19);
            cell.setCellValue("DELETE_YN (폐기 여부)");
            cell = row.createCell(20);
            cell.setCellValue("FAILURE_YN (결함 여부)");

            // 데이터
            for (int i=0; i<1; i++) {
                row = sheet.createRow(rowNum++);
                cell = row.createCell(0);
                cell.setCellValue("hw-test");
                cell = row.createCell(1);
                cell.setCellValue("AST03");
                cell = row.createCell(2);
                cell.setCellValue("TYPE01");
                cell = row.createCell(3);
                cell.setCellValue("LOC02");
                cell = row.createCell(4);
                cell.setCellValue("2023-11-17");
                cell = row.createCell(5);
                cell.setCellValue("CPU01");
                cell = row.createCell(6);
                cell.setCellValue(256);
                cell = row.createCell(7);
                cell.setCellValue(0);
                cell = row.createCell(8);
                cell.setCellValue(256);
                cell = row.createCell(9);
                cell.setCellValue(256);
                cell = row.createCell(10);
                cell.setCellValue(16);
                cell = row.createCell(11);
                cell.setCellValue(8);
                cell = row.createCell(12);
                cell.setCellValue("1616FDK34-QWER12");
                cell = row.createCell(13);
                cell.setCellValue("COM01");
                cell = row.createCell(14);
                cell.setCellValue("USE01");
                cell = row.createCell(15);
                cell.setCellValue("좌측에 흠집");
                cell = row.createCell(16);
                cell.setCellValue("2023-11-17");
                cell = row.createCell(17);
                cell.setCellValue("SDEE4432");
                cell = row.createCell(18);
                cell.setCellValue("N");
                cell = row.createCell(19);
                cell.setCellValue("N");
                cell = row.createCell(20);
                cell.setCellValue("N");
            }

            // 컨텐츠 타입과 파일명 지정
            response.setContentType("ms-vnd/excel");
//        response.setHeader("Content-Disposition", "attachment;filename=hardware.xls");
            response.setHeader("Content-Disposition", "attachment;filename=hardware.xlsx");

            wb.write(response.getOutputStream());
            wb.close();
        }catch (Exception e) {
            throw Response.makeFailResponse(CodeAs.ASSET_HW_EXCEL_DOWNLOAD_ERROR_CODE, HttpStatus.INTERNAL_SERVER_ERROR
                    ,CodeAs.ASSET_HW_EXCEL_DOWNLOAD_CONVERT_ERROR_MESSAGE , e);
        }
    }

    // HW ENTITY <=> DTO
    private HardwareEntity mapRowToHardwareEntity(Row row) {
        //자산번호 중복체크
        String hwNo = excelCommonComponent.getStringCellValue(row, 0,"hwNo","");
        isDupPk(hwNo);

        String hwDiv = excelCommonComponent.getStringCellValue(row, 1,"hwDiv",hwNo);
        String hwCategory = excelCommonComponent.getStringCellValue(row, 2,"hwCategory", hwNo);
        String hwLocation = excelCommonComponent.getStringCellValue(row, 3, "hwLocation", hwNo);
        String hwCpu = excelCommonComponent.getStringCellValue(row, 5,"hwCpu",hwNo);
        Integer hwSsd1 = excelCommonComponent.getNumericCellValue(row, 6,"hwSSd1",hwNo);
        Integer hwSsd2 = excelCommonComponent.getNumericCellValue(row, 7,"hwSSd2",hwNo);
        Integer hwHdd1 = excelCommonComponent.getNumericCellValue(row, 8,"hwHDd1",hwNo);
        Integer hwHdd2 = excelCommonComponent.getNumericCellValue(row, 9,"hwHDd2",hwNo);
        Integer hwRam1 = excelCommonComponent.getNumericCellValue(row, 10,"hwRam1",hwNo);
        Integer hwRam2 = excelCommonComponent.getNumericCellValue(row, 11,"hwRam2",hwNo);
        String hwModel = excelCommonComponent.getStringCellValue(row, 12,"hwModel",hwNo);
        String hwMfr = excelCommonComponent.getStringCellValue(row, 13,"hwMfr",hwNo);
        String hwUsage = excelCommonComponent.getStringCellValue(row, 14,"hwUsage",hwNo);
        String hwRemarks = excelCommonComponent.getStringCellValue(row, 15,"Remarks",hwNo);

        String purchaseDate = excelCommonComponent.getStringCellValue(row,16,"purchaseDate",hwNo);
        Timestamp datedPurchaseDate = excelCommonComponent.convertDateToTimestamp(purchaseDate);

        String produceDate = excelCommonComponent.getStringCellValue(row,4,"produceDate",hwNo);
        Timestamp datedProductDate = excelCommonComponent.convertDateToTimestamp(produceDate);

        String hwSn = excelCommonComponent.getStringCellValue(row, 17,"hwSn",hwNo);
        String oldYn = excelCommonComponent.getStringCellValue(row, 18,"oldYn",hwNo);
        String deleteYn = excelCommonComponent.getStringCellValue(row, 19,"deleteYn",hwNo);
        String failureYn = excelCommonComponent.getStringCellValue(row, 20,"failureYn",hwNo);

        return HardwareEntity.builder()
                .createId(CodeAs.ADMIN)
                .updateId(CodeAs.ADMIN)
                .deleteYn(deleteYn.equals(CodeAs.POSITIVE)) // 불린
                .oldYn(oldYn.equals(CodeAs.POSITIVE))
                .failureYn(failureYn.equals(CodeAs.POSITIVE)) // 불린
                .hwModel(hwModel)
                .hwSsd1(excelCommonComponent.validateNumber(hwSsd1))
                .hwSsd2(excelCommonComponent.validateNumber(hwSsd2))
                .hwHdd1(excelCommonComponent.validateNumber(hwHdd1))
                .hwHdd2(excelCommonComponent.validateNumber(hwHdd2))
                .hwRam1(excelCommonComponent.validateNumber(hwRam1))
                .hwRam2(excelCommonComponent.validateNumber(hwRam2))
                .purchaseDate(datedPurchaseDate)
                .produceDate(datedProductDate)
                .hwSN(hwSn)
                .hwNo(hwNo)
                .hwRemarks(hwRemarks)
                // code와 조인
                .hwLocation(excelCommonComponent.findCodeByPk(hwLocation))
                .hwCpu(excelCommonComponent.findCodeByPk(hwCpu))
                .hwDiv(excelCommonComponent.findCodeByPk(hwDiv))
                .hwMfr(excelCommonComponent.findCodeByPk(hwMfr))
                .hwUsage(excelCommonComponent.findCodeByPk(hwUsage))
                .hwCategory(excelCommonComponent.findCodeByPk(hwCategory))
                .build();
    }
    // 자산번호 중복체크 함수
    private void isDupPk(String hwNo){
        HardwareEntity dupPk = hardwareRepository.findByHwNo(hwNo);
        if(dupPk != null){
            throw new CustomException(CodeAs.ASSET_PK_DUPLICATE_ERROR_CODE, HttpStatus.INTERNAL_SERVER_ERROR,
                    hwNo+" : "+CodeAs.ASSET_PK_DUPLICATE_ERROR_MESSAGE,null);
        }
    }
}
