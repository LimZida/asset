package com.mcnc.assetmgmt.excel.service.impl;

import com.mcnc.assetmgmt.asset.entity.SoftwareEntity;
import com.mcnc.assetmgmt.asset.repository.SoftwareRepository;
import com.mcnc.assetmgmt.excel.service.SoftwareExcelService;
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
 * title : SoftwareExcelServiceImpl
 *
 * description : SoftwareExcelServiceImpl 구현체, 소프트웨어 엑셀 다운, 조회 기능 로직
 *
 * reference :  올바른 엔티티 수정 방법: https://www.inflearn.com/questions/15944/%EA%B0%95%EC%9D%98-%EC%A4%91%EC%97%90-%EA%B0%92%ED%83%80%EC%9E%85%EC%9D%98-%EC%88%98%EC%A0%95%EC%97%90-%EB%8C%80%ED%95%B4%EC%84%9C-%EB%8B%A4%EB%A4%84%EC%A3%BC%EC%85%A8%EB%8A%94%EB%8D%B0%EC%9A%94-%EB%AC%B8%EB%93%9D-%EC%97%94%ED%8B%B0%ED%8B%B0-%EC%88%98%EC%A0%95%EC%97%90-%EB%8C%80%ED%95%B4%EC%84%9C-%EA%B6%81%EA%B8%88%ED%95%B4%EC%A0%B8-%EC%A7%88%EB%AC%B8%EB%93%9C%EB%A6%BD%EB%8B%88%EB%8B%A4
 *
 *              DTO의 적용 범위 https://tecoble.techcourse.co.kr/post/2021-04-25-dto-layer-scope/
 *              더티체킹 : https://jojoldu.tistory.com/415
 *
 *
 * author : 임현영
 * date : 2023.11.21
 **/
@Service
@Slf4j
@RequiredArgsConstructor
public class SoftwareExcelServiceImpl implements SoftwareExcelService {
    private final SoftwareRepository softwareRepository;
    private final ExcelCommonComponent excelCommonComponent;

    @Override
    @Transactional
    public ExcelDto.resultInfo readAndInsertSwData(ExcelDto.excelInfo excelInfo) {
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
                    SoftwareEntity software = mapRowToSoftwareEntity(row);

                    SoftwareEntity saveResult = softwareRepository.save(software);
                    if (saveResult == null) {
                        throw new CustomException(CodeAs.ASSET_SW_EXCEL_INSERT_ERROR_CODE, HttpStatus.INTERNAL_SERVER_ERROR
                                , CodeAs.ASSET_SW_EXCEL_INSERT_ERROR_MESSAGE, null);
                    }
                }
            }
            ExcelDto.resultInfo res = excelCommonComponent.getResult();
            return res;
        } catch (Exception e) {
            throw Response.makeFailResponse(CodeAs.ASSET_SW_EXCEL_SELECT_ERROR_CODE, HttpStatus.INTERNAL_SERVER_ERROR
                    , CodeAs.ASSET_SW_EXCEL_SELECT_ERROR_MESSAGE, e);
        }
    }

    @Override
    public void downloadSoftwareExcelFileService(HttpServletResponse response) {
        try {

            //Workbook wb = new HSSFWorkbook(); // xls
            Workbook wb = new XSSFWorkbook(); // xlsx
            Sheet sheet = wb.createSheet("software");
            Row row = null;
            Cell cell = null;
            int rowNum = 0;

            // 가장 상위단 컬럼
            row = sheet.createRow(rowNum++);
            cell = row.createCell(0);
            cell.setCellValue("필독! SW_DIV(자산 종류), SW_CATEGORY(자산 범주), SW_LOCATION(자산 위치), SW_MFR(자산 제조회사), SW_USAGE(용도 구분), SW_LICENSE(라이선스 기간), SW_OS(운영체제), SW_OS_VERSION(운영체제 버전)은 '코드 관리'에서 지정한 코드네임으로 입력합니다. 아래 예시를 참고해 자산을 작성해주세요."+System.lineSeparator()+System.lineSeparator()+
                    "1. SW_QUANTITY(수량)의 경우 엑셀 내 표시 형식을 숫자로 맞춰 진행부탁드립니다. " +System.lineSeparator()+
                    "ex) 2개 => 2" +System.lineSeparator()+
                    "만약 수량이 없다면 공란으로 맞춰 진행 부탁드립니다."+System.lineSeparator()+System.lineSeparator()+
                    "2. EXPIRE_DATE(만료일자),PURCHASE_DATE (구매일자)의 경우"+System.lineSeparator()+
                    "엑셀 내 표시 형식을 텍스트로 맞춰 진행부탁드립니다."+System.lineSeparator()+
                    "ex) 2024-01-23"+System.lineSeparator()+
                    "만약 만료일자가 영구거나, 구매일자가 파악이 불가한 경우 공란으로 맞춰 진행 부탁드립니다."+System.lineSeparator()+System.lineSeparator()+
                    "3. SW_REMARKS (비고)의 경우"+System.lineSeparator()+
                    "엑셀 내 표시 형식을 텍스트로 맞춰 진행부탁드립니다. 만약 비고가 공란일 경우 공란으로 기입 부탁드립니다."
            );
            // 2번째 상위단 컬럼
            row = sheet.createRow(rowNum++);
            cell = row.createCell(0);
            cell.setCellValue("SW_NO(자산 일련번호)");
            cell = row.createCell(1);
            cell.setCellValue("SW_DIV(자산 종류)");
            cell = row.createCell(2);
            cell.setCellValue("SW_CATEGORY(자산 범주)");
            cell = row.createCell(3);
            cell.setCellValue("SW_LOCATION(자산 위치)");
            cell = row.createCell(4);
            cell.setCellValue("SW_NAME(자산명)");
            cell = row.createCell(5);
            cell.setCellValue("SW_MFR(자산 제조회사)");
            cell = row.createCell(6);
            cell.setCellValue("SW_USAGE(용도 구분)");
            cell = row.createCell(7);
            cell.setCellValue("SW_REMARKS(비고)");
            cell = row.createCell(8);
            cell.setCellValue("PURCHASE_DATE(구매일자)");
            cell = row.createCell(9);
            cell.setCellValue("EXPIRE_DATE(만료일자)");
            cell = row.createCell(10);
            cell.setCellValue("SW_SN(시리얼넘버)");
            cell = row.createCell(11);
            cell.setCellValue("EXPIRE_YN(만료여부)");
            cell = row.createCell(12);
            cell.setCellValue("DELETE_YN(폐기여부)");
            cell = row.createCell(13);
            cell.setCellValue("SW_LICENSE(라이선스 기간)");
            cell = row.createCell(14);
            cell.setCellValue("SW_QUANTITY(수량)");
            cell = row.createCell(15);
            cell.setCellValue("SW_OS(운영체제)");
            cell = row.createCell(16);
            cell.setCellValue("SW_OS_VERSION(운영체제 버전)");

            // 데이터
            for (int i=0; i<1; i++) {
                row = sheet.createRow(rowNum++);
                cell = row.createCell(0);
                cell.setCellValue("sw-test");
                cell = row.createCell(1);
                cell.setCellValue("AST07");
                cell = row.createCell(2);
                cell.setCellValue("TYPE02");
                cell = row.createCell(3);
                cell.setCellValue("LOC01");
                cell = row.createCell(4);
                cell.setCellValue("이클립스 엔터프라이즈");
                cell = row.createCell(5);
                cell.setCellValue("COM41");
                cell = row.createCell(6);
                cell.setCellValue("USE01");
                cell = row.createCell(7);
                cell.setCellValue("정품인증");
                cell = row.createCell(8);
                cell.setCellValue("2023-11-17");
                cell = row.createCell(9);
                cell.setCellValue("2023-11-17");
                cell = row.createCell(10);
                cell.setCellValue("1RRT34-WKR5567");
                cell = row.createCell(11);
                cell.setCellValue("N");
                cell = row.createCell(12);
                cell.setCellValue("N");
                cell = row.createCell(13);
                cell.setCellValue("LIC02");
                cell = row.createCell(14);
                cell.setCellValue(1);
                cell = row.createCell(15);
                cell.setCellValue("OS08");
                cell = row.createCell(16);
                cell.setCellValue("OSV01");
            }

            // 컨텐츠 타입과 파일명 지정
            response.setContentType("ms-vnd/excel");
//        response.setHeader("Content-Disposition", "attachment;filename=software.xls");
            response.setHeader("Content-Disposition", "attachment;filename=software.xlsx");

            wb.write(response.getOutputStream());
            wb.close();
        }catch (Exception e) {
            throw Response.makeFailResponse(CodeAs.ASSET_SW_EXCEL_DOWNLOAD_ERROR_CODE, HttpStatus.INTERNAL_SERVER_ERROR
                    ,CodeAs.ASSET_SW_EXCEL_DOWNLOAD_CONVERT_ERROR_MESSAGE , e);
        }
    }

    // SW ENTITY <=> DTO
    private SoftwareEntity mapRowToSoftwareEntity(Row row) {
        // SW 정보
        String swNo = excelCommonComponent.getStringCellValue(row, 0,"swNo","");
        // SW 자산번호 중복체크
        isDupPk(swNo);

        String swDiv = excelCommonComponent.getStringCellValue(row, 1,"swDiv",swNo);
        String swCategory = excelCommonComponent.getStringCellValue(row, 2,"swCategory",swNo);
        String swLocation = excelCommonComponent.getStringCellValue(row, 3,"swLocation",swNo);
        String swName = excelCommonComponent.getStringCellValue(row, 4,"swName",swNo);
        String swMfr = excelCommonComponent.getStringCellValue(row, 5,"swMfr",swNo);
        String swUsage = excelCommonComponent.getStringCellValue(row, 6,"swUsage",swNo);
        String swRemarks = excelCommonComponent.getStringCellValue(row, 7,"swRemarks",swNo);

        // 구매 일자는 날짜 형식
        String purchaseDate = excelCommonComponent.getStringCellValue(row,8,"purchaseDate",swNo);
        Timestamp datedPurchaseDate = excelCommonComponent.convertDateToTimestamp(purchaseDate);

        // 만료 일자는 날짜 형식
        String expireDate = excelCommonComponent.getStringCellValue(row,9,"expireDate",swNo);
        Timestamp datedExpiredDate = excelCommonComponent.convertDateToTimestamp(expireDate);

        String swSn = excelCommonComponent.getStringCellValue(row, 10,"swSn",swNo);
        String expireYn = excelCommonComponent.getStringCellValue(row, 11,"expireYn",swNo);
        String deleteYn = excelCommonComponent.getStringCellValue(row, 12,"deleteYn",swNo);
        String swLicense = excelCommonComponent.getStringCellValue(row, 13,"swLicense",swNo);

        // 수량은 숫자형식
        int swQuantity = excelCommonComponent.getNumericCellValue(row,14,"swQuantity",swNo);

        String swOs = excelCommonComponent.getStringCellValue(row, 15,"swOs",swNo);
        String swOsVersion = excelCommonComponent.getStringCellValue(row, 16,"swOsVersion",swNo);

        return SoftwareEntity.builder()
                .createId(CodeAs.ADMIN)
                .updateId(CodeAs.ADMIN)
                .deleteYn(deleteYn.equals(CodeAs.POSITIVE)) // 불린
                .expireYn(expireYn.equals(CodeAs.POSITIVE)) // 불린
                .swNo(swNo)
                .swName(swName)
                .swRemarks(swRemarks)
                .purchaseDate(datedPurchaseDate)
                .expireDate(datedExpiredDate)
                .swSN(swSn)
                .swQuantity(excelCommonComponent.validateNumber(swQuantity))
                // code와 조인
                .swDiv(excelCommonComponent.findCodeByPk(swDiv))
                .swLocation(excelCommonComponent.findCodeByPk(swLocation))
                .swMfr(excelCommonComponent.findCodeByPk(swMfr))
                .swOS(excelCommonComponent.findCodeByPk(swOs))
                .swOSVersion(excelCommonComponent.findCodeByPk(swOsVersion))
                .swLicense(excelCommonComponent.findCodeByPk(swLicense))
                .swUsage(excelCommonComponent.findCodeByPk(swUsage))
                .swCategory(excelCommonComponent.findCodeByPk(swCategory))
                .build();
    }

    // 자산번호 중복체크 함수
    private void isDupPk(String swNo){
        SoftwareEntity dupPk = softwareRepository.findBySwNo(swNo);
        if(dupPk != null){
            throw new CustomException(CodeAs.ASSET_PK_DUPLICATE_ERROR_CODE, HttpStatus.INTERNAL_SERVER_ERROR,
                    swNo+" : "+CodeAs.ASSET_PK_DUPLICATE_ERROR_MESSAGE,null);
        }
    }
}