package com.mcnc.assetmgmt.excel.service;

import com.mcnc.assetmgmt.excel.dto.ExcelDto;

import javax.servlet.http.HttpServletResponse;

/**
 * title : SoftwareExcelService
 *
 * description : SoftwareExcelRepository,ExcelDto 매핑용 SoftwareExcelService 인터페이스
 *
 * reference : Optional https://mangkyu.tistory.com/70
 *             메소드의 반환 값이 절대 null이 아니라면 Optional을 사용하지 않는 것이 좋다.
 *             즉, Optional은 메소드의 결과가 null이 될 수 있으며, null에 의해 오류가 발생할 가능성이 매우 높을 때 반환값으로만 사용되어야 한다.
 *
 *
 *
 * author : 임현영
 * date : 2023.11.21
 **/
public interface SoftwareExcelService {
    ExcelDto.resultInfo readAndInsertSwData(ExcelDto.excelInfo excelInfo);
    void downloadSoftwareExcelFileService(HttpServletResponse response);
}
