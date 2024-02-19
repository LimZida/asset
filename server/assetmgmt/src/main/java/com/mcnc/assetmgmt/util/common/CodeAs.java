package com.mcnc.assetmgmt.util.common;

/**
 * title : CodeAs
 *
 * description : 에러가 나거나 로그에 사용하기 위한 전역 코드 정의
 *               1. 기능 코드
 *               2. 공통 에러 코드
 *               3. 대시보드 에러 코드
 *               4. 자산 CRUD 코드
 *               5. 할당 자산 코드
 *               6. 히스토리 코드
 *               7. 임직원 인증 및 관리 코드
 *               8. 코드관리 코드
 *
 * reference : static의 사용 범위 - https://velog.io/@ldevlog/17.-static%EB%A9%94%EC%84%9C%EB%93%9C%EC%9D%98-%EA%B5%AC%ED%98%84%EA%B3%BC-%ED%99%9C%EC%9A%A9-%EB%B3%80%EC%88%98%EC%9D%98-%EC%9C%A0%ED%9A%A8-%EB%B2%94%EC%9C%84
 *
 * author : 임현영
 * date : 2023.11.08
 **/
public class CodeAs {
    /**
    * 기능코드
    */
    public static final String JWT_ROLE                                    = "ROLE_USER";
    public static final String ENCRYPT_AES                                 = "AES";
    public static final String CHARSET                                     = "UTF-8";
    public static final String JWT_HEADER                                  = "Bearer ";
    public static final String CATEGORY                                    = "CTG";
    public static final String DEPARTMENT                                  = "DPT";
    public static final String ASSET                                       = "AST";
    public static final String ALL                                         = "전체";
    public static final String ALL_CODE                                    = "ALL";
    public static final String ADMIN                                       = "ADMIN";
    public static final String NA                                          = "해당 없음";
    public static final String BLANK                                       = "";
    public static final String SPACING                                     = " ";
    public static final String NULL_TEXT                                   = "-";
    public static final int    NULL_DEPTH                                  = -1;
    public static final int    NUM_INIT                                    = 1;
    public static final int    OLD_ASSET_YEAR                              = 4;
    public static final int    EXPIRED_ASSET_MONTH                         = 3;
    public static final int    ZERO                                        = 0;
    public static final String NEGATIVE                                    = "N";
    public static final String POSITIVE                                    = "Y";
    public static final String PK_FIRST                                    = "01";
    public static final String STATUS01                                    = "STS01";
    public static final String IN                                          = "반입";
    public static final String STATUS02                                    = "STS02";
    public static final String OUT                                         = "반출";
    public static final String HARDWARE                                    = "TYPE01";
    public static final String HW                                          = "HW";
    public static final String ALL_HARDWARE                                = "하드웨어 전체";
    public static final String ALL_SOFTWARE                                = "소프트웨어 전체";
    public static final String SOFTWARE                                    = "TYPE02";
    public static final String SW                                          = "SW";
    public static final String ASSIGN_HISTORY                              = "assignHistory";
    public static final String ASSIGN_HISTORY_LIST                         = "assetHistoryList";
    public static final String ASSIGN_HISTORY_TOTAL_CNT                    = "assignHistoryTotalCnt";
    public static final String USER_LIST                                   = "userList";
    public static final String DEPT_LIST                                   = "deptList";
    public static final String ASSET_SW_MAP                                = "swList";
    public static final String ASSET_HW_MAP                                = "hwList";
    public static final String CATEGORY_ASSET_MFR                          = "CTG010202";
    public static final String CATEGORY_ASSET_DIV_HW                       = "CTG010201";
    public static final String CATEGORY_ASSET_DIV_SW                       = "CTG010301";
    public static final String CATEGORY_ASSET_LOCATION                     = "CTG010102";
    public static final String CATEGORY_ASSET_CPU                          = "CTG010203";
    public static final String CATEGORY_ASSET_USAGE                        = "CTG010105";
    public static final String CATEGORY_ASSET_CTG                          = "CTG010104";
    public static final String CATEGORY_ASSET_OS                           = "CTG010303";
    public static final String CATEGORY_ASSET_OS_VERSION                   = "CTG010304";
    public static final String CATEGORY_ASSET_LICENSE                      = "CTG010305";
    public static final String HEADER_AUTH_INFO                            = "Auth-Info";
    public static final String TOKEN_ERROR                                 = "tokenError";
    public static final String TR_ID                                       = "transactionId";
    public static final String EXECUTE_METHOD                              = "executeMethod";
    public static final String REQ_TYPE                                    = "reqType";
    public static final String REQUEST                                     = "request";
    public static final String REQ_BODY                                    = "body";
    public static final String REQ_PARAMETER                               = "parameter";
    public static final String LDAP_RESULT_ID                              = "sAMAccountName";
    public static final String LDAP_RESULT_NAME                            = "name";
    public static final String DUPLICATE                                   = "dup";
    public static final String NON_DUPLICATE                               = "notDup";
    public static final String AUTHORIZATION                               = "Authorization";
    public static final String USER_ID                                     = "userId";
    public static final String LOGIN                                       = "login";
    public static final String VALIDATION                                  = "validation";
    public static final String CODE                                        = "code";
    public static final String ERROR_CODE                                  = "Errorcode";
    public static final String CODE_NAME                                   = "codeName";
    public static final String CODE_LIST_INFO                              = "infoList";
    public static final String CODE_TYPE                                   = "codeType";
    public static final String YEAR                                        = "년";
    public static final String MONTH                                       = "개월";
    public static final String TIMESTAMP_PATTERN                           = "yyyyMMddHHmmss";
    public static final String KOREAN                                      = "KOREAN";
    public static final String KOREA                                       = "KOREA";
    public static final String DATE_PATTERN                                = "yyyy-MM-dd";
    public static final String STARTOFDAY                                  = " 00:00:00"; // 날짜 형변환용
    public static final String STRING_FORMAT                               = "%02d";
    public static final String TEMP                                        = "코드 이름을 수정해 사용해주세요";
    public static final String PERMANENT_CODE                              = "LIC01"; // 라이선스 영구 코드
    public static final String UPDATE                                      = "U";
    public static final String INSERT                                      = "I";


    /**
     * 공통 에러 코드
     */
    public static final String REQUEST_NULL_ERROR_CODE 					    = "REQ01";
    public static final String REQUEST_NULL_ERROR_MESSAGE 				    = "요청 변수 혹은 할당 변수가 NULL입니다.";

    public static final String REQUEST_WRONG_ERROR_CODE 					    = "REQ02";
    public static final String REQUEST_WRONG_ERROR_MESSAGE 				    = "잘못된 요청 값입니다.";

    public static final String REQUEST_AUTHORIZE_ERROR_CODE 				= "REQ03";
    public static final String REQUEST_AUTHORIZE_ERROR_MESSAGE 				= "You do not have access permission.";

    public static final String REQUEST_FIELD_ERROR_CODE 					= "REQ04";
    public static final String REQUEST_FIELD_ERROR_MESSAGE 				    = "필수 요청 필드가 누락되었습니다.";

    public static final String REQUEST_DUPLICATE_PK_CODE                    = "REQ05";
    public static final String REQUEST_DUPLICATE_PK_MESSAGE                 = "이미 존재하는 PK 값입니다.";

    public static final String REQUEST_PARAMETER_NULL_CODE                  = "REQ06";
    public static final String REQUEST_PARAMETER_NULL_MESSAGE               = "요청 파라미터가 NULL입니다.";

    public static final String REQUEST_PARAMETER_EMPTY_CODE                 = "REQ07";
    public static final String REQUEST_PARAMETER_EMPTY_MESSAGE              = "요청 매개변수가 없습니다.";

    public static final String REQUEST_HEADER_EMPTY_CODE                    = "REQ08";
    public static final String REQUEST_HEADER_EMPTY_MESSAGE                 = "헤더에 userId가 null입니다.";

    public static final String REQUEST_HEADER_AUTH_EMPTY_CODE               = "REQ09";
    public static final String REQUEST_HEADER_AUTH_EMPTY_MESSAGE            = "헤더에 authorization이 null입니다.";

    public static final String REQUEST_HEADER_ID_AUTH_EMPTY_CODE            = "REQ10";
    public static final String REQUEST_HEADER_ID_AUTH_EMPTY_MESSAGE         = "헤더에 userId와 authorization이 null입니다.";

    public static final String UNKNOWN_ERROR_CODE 					        = "UNKNOWN";
    public static final String UNKNOWN_ERROR_MESSAGE 				        = "알 수 없는 에러가 발생하였습니다.";

    public static final String MAPPING_ERROR_CODE 					        = "MAP01";
    public static final String MAPPING_ERROR_MESSAGE 				        = "ENTITY <=> DTO 변환 중 에러가 발생하였습니다.";

    public static final String SELECT_DATABASE_ERROR_CODE 					= "DATA01";
    public static final String SELECT_DATABASE_ERROR_MESSAGE 				= "데이터 조회 오류입니다.";

    public static final String UPDATE_DATABASE_ERROR_CODE 					= "DATA02";
    public static final String UPDATE_DATABASE_ERROR_MESSAGE 				= "데이터 갱신 오류입니다.";

    public static final String INSERT_DATABASE_ERROR_CODE 					= "DATA03";
    public static final String INSERT_DATABASE_ERROR_MESSAGE 				= "데이터 삽입 오류입니다.";

    public static final String DELETE_DATABASE_ERROR_CODE 					= "DATA04";
    public static final String DELETE_DATABASE_ERROR_MESSAGE 				= "데이터 삭제 오류입니다.";

    public static final String TIMESTAMP_NULL_ERROR_CODE 					= "TIME01";
    public static final String TIMESTAMP_NULL_ERROR_MESSAGE 				= "TIMESTAMP가 NULL입니다.";

    /**
     * 대시보드 에러 코드
     */
    public static final String DASHBOARD_ASSET_ERROR_CODE					= "DASH01";
    public static final String DASHBOARD_ASSET_ERROR_MESSAGE				= "대시보드 자산 코드 응답 중 에러가 발생하였습니다.";

    public static final String DASHBOARD_IN_OUT_ERROR_CODE					= "DASH02";
    public static final String DASHBOARD_IN_OUT_ERROR_MESSAGE				= "대시보드 반입반출 기록 응답 중 에러가 발생하였습니다.";

    public static final String DASHBOARD_DETAIL_ERROR_CODE					= "DASH03";
    public static final String DASHBOARD_DETAIL_ERROR_MESSAGE      			= "대시보드 상세 내역 처리 중 에러가 발생하였습니다.";

    public static final String DASHBOARD_ASSET_LIST_ERROR_CODE				= "DASH04";
    public static final String DASHBOARD_ASSET_LIST_ERROR_MESSAGE      		= "대시보드 자산 리스트 조회 중 에러가 발생하였습니다.";

    public static final String DASHBOARD_ASSET_CODE_ERROR_CODE				= "DASH05";
    public static final String DASHBOARD_ASSET_CODE_ERROR_MESSAGE      		= "대시보드 자산 코드 PK 조회 중 에러가 발생하였습니다.";

    public static final String DASHBOARD_HISTORY_LIST_ERROR_CODE			= "DASH06";
    public static final String DASHBOARD_HISTORY_LIST_ERROR_MESSAGE      	= "대시보드 히스토리 내역 조회 중 에러가 발생하였습니다.";

    public static final String DASHBOARD_SW_DETAIL_ERROR_CODE				= "DASH07";
    public static final String DASHBOARD_SW_DETAIL_ERROR_MESSAGE      		= "SW 대시보드 상세 내역 처리 중 에러가 발생하였습니다.";

    public static final String DASHBOARD_HW_DETAIL_ERROR_CODE				= "DASH08";
    public static final String DASHBOARD_HW_DETAIL_ERROR_MESSAGE      		= "HW 대시보드 상세 내역 처리 중 에러가 발생하였습니다.";
    /**
     * 자산 CRUD 에러 코드
     */
    public static final String ASSET_SELECT_ERROR_CODE			            = "ASSET01";
    public static final String ASSET_SELECT_ERROR_MESSAGE   			    = "자산 조회 처리 중 에러가 발생했습니다.";

    public static final String ASSET_REGISTER_ERROR_CODE					= "ASSET02";
    public static final String ASSET_REGISTER_ERROR_MESSAGE					= "자산 등록 처리 중 에러가 발생했습니다.";

    public static final String ASSET_CODE_ERROR_CODE					    = "ASSET03";
    public static final String ASSET_CODE_ERROR_MESSAGE 					= "자산 등록/수정 시 필요한 코드 응답 중 에러가 발생했습니다.";

    public static final String ASSET_MODIFY_ERROR_CODE					    = "ASSET04";
    public static final String ASSET_MODIFY_ERROR_MESSAGE					= "자산 수정 처리 중 에러가 발생했습니다.";

    public static final String ASSET_DELETE_ERROR_CODE					    = "ASSET05";
    public static final String ASSET_DELETE_ERROR_MESSAGE					= "자산 삭제 처리 중 에러가 발생했습니다.";

    public static final String ASSET_HW_EXCEL_SELECT_ERROR_CODE			    = "ASSET06";
    public static final String ASSET_HW_EXCEL_SELECT_ERROR_MESSAGE			= "HW 자산 엑셀 데이터 조회 중 에러가 발생했습니다.";

    public static final String ASSET_SW_EXCEL_SELECT_ERROR_CODE			    = "ASSET07";
    public static final String ASSET_SW_EXCEL_SELECT_ERROR_MESSAGE			= "SW 자산 엑셀 데이터 조회 중 에러가 발생했습니다.";

    public static final String ASSET_HW_EXCEL_ERROR_CODE			        = "ASSET08";
    public static final String ASSET_HW_EXCEL_ERROR_MESSAGE			        = "HW 자산 기능 실행 중 에러가 발생했습니다.";

    public static final String ASSET_SW_EXCEL_ERROR_CODE			        = "ASSET09";
    public static final String ASSET_SW_EXCEL_ERROR_MESSAGE			        = "SW 자산 기능 실행 중 에러가 발생했습니다.";

    public static final String ASSET_EXCEL_TIME_CONVERT_ERROR_CODE			= "ASSET10";
    public static final String ASSET_EXCEL_TIME_CONVERT_ERROR_MESSAGE		= "엑셀 내 데이터 시간 변환 중 에러가 발생했습니다.";

    public static final String ASSET_SW_EXCEL_DOWNLOAD_ERROR_CODE			= "ASSET11";
    public static final String ASSET_SW_EXCEL_DOWNLOAD_CONVERT_ERROR_MESSAGE= "SW 엑셀 다운로드 도중 에러가 발생했습니다.";

    public static final String ASSET_HW_EXCEL_DOWNLOAD_ERROR_CODE			= "ASSET12";
    public static final String ASSET_HW_EXCEL_DOWNLOAD_CONVERT_ERROR_MESSAGE= "HW 엑셀 다운로드 도중 에러가 발생했습니다.";

    public static final String ASSET_HW_EXCEL_INSERT_ERROR_CODE			    = "ASSET13";
    public static final String ASSET_HW_EXCEL_INSERT_ERROR_MESSAGE          = "HW 엑셀을 통한 자산 삽입 도중 에러가 발생했습니다.";

    public static final String ASSET_SW_EXCEL_INSERT_ERROR_CODE			    = "ASSET14";
    public static final String ASSET_SW_EXCEL_INSERT_ERROR_MESSAGE          = "SW 엑셀을 통한 자산 삽입 도중 에러가 발생했습니다.";

    public static final String ASSET_PK_DUPLICATE_ERROR_CODE			    = "ASSET15";
    public static final String ASSET_PK_DUPLICATE_ERROR_MESSAGE             = "해당 자산 번호가 이미 존재합니다.";

    public static final String ASSET_MMR_UNDER_ZERO_ERROR_CODE			    = "ASSET16";
    public static final String ASSET_MMR_UNDER_ZERO_ERROR_MESSAGE           = "해당 자산의 메모리 혹은 수량이 0 미만입니다.";

    public static final String ASSET_EXCEL_STR_CHANGE_ERROR_CODE		    = "ASSET17";
    public static final String ASSET_EXCEL_STR_CHANGE_ERROR_MESSAGE         = "엑셀 내 데이터를 문자열로 변환 중 에러가 발생했습니다.";

    public static final String ASSET_EXCEL_INT_CHANGE_ERROR_CODE		    = "ASSET18";
    public static final String ASSET_EXCEL_INT_CHANGE_ERROR_MESSAGE         = "엑셀 내 데이터를 숫자로 변환 중 에러가 발생했습니다.";

    /**
     * 할당 자산 에러 코드
     */
    public static final String ASSET_ALLOCATE_FAIL_CODE					    = "ALLOCATION01";
    public static final String ASSET_ALLOCATE_FAIL_MESSAGE  				= "자산을 할당하는 중 에러가 발생했습니다.";
    public static final String ASSET_ALLOCATE_HISTORY_FAIL_CODE				= "ALLOCATION02";
    public static final String ASSET_ALLOCATE_HISTORY_FAIL_MESSAGE		    = "할당하려는 자산의 히스토리 조회 중 에러가 발생하였습니다.";
    public static final String ASSET_ALLOCATE_QUANTITY_FAIL_CODE  			= "ALLOCATION03";
    public static final String ASSET_ALLOCATE_QUANTITY_FAIL_MESSAGE  		= "자산을 할당하는 중 수량 에러가 발생했습니다.";
    public static final String ASSET_ALLOCATE_SW_VALIDATE_FAIL_CODE  		= "ALLOCATION04";
    public static final String ASSET_ALLOCATE_SW_VALIDATE_FAIL_MESSAGE  	= "SW 자산에 이미 할당된 아이디 입니다.";
    public static final String ASSET_ALLOCATE_LIST_FAIL_CODE  		        = "ALLOCATION05";
    public static final String ASSET_ALLOCATE_LIST_FAIL_MESSAGE  	        = "할당 리스트 조회 중 오류가 발생했습니다.";

    /**
     * HISTORY 에러 코드
     */
    public static final String HISTORY_VIEW_FAIL_CODE   					= "HISTORY01";
    public static final String HISTORY_VIEW_FAIL_MESSAGE	    			= "히스토리를 응답 중 에러가 발생하였습니다.";
    public static final String HISTORY_SELECT_FAIL_CODE                     = "HISTORY02";
    public static final String HISTORY_SELECT_FAIL_MESSAGE	    			= "히스토리를 조회 중 에러가 발생하였습니다.";
    public static final String HISTORY_ASSET_ERROR_CODE					    = "HISTORY03";
    public static final String HISTORY_ASSET_ERROR_MESSAGE				    = "히스토리 자산 코드 응답 중 에러가 발생하였습니다.";
    /**
     * 임직원 인증 및 관리 에러 코드
     */
    public static final String VALIDATE_ACCESS_JWT_ERROR_CODE 				= "JWT01";
    public static final String VALIDATE_ACCESS_JWT_ERROR_MESSAGE 			= "access JWT 인증에 실패하였습니다.";

    public static final String VALIDATE_REFRESH_JWT_ERROR_CODE 				= "JWT02";
    public static final String VALIDATE_REFRESH_JWT_ERROR_MESSAGE 			= "refresh JWT 인증에 실패하였습니다.";

    public static final String SELECT_REFRESH_JWT_ERROR_CODE 				= "JWT03";
    public static final String SELECT_REFRESH_JWT_ERROR_MESSAGE 			= "refresh JWT 조회에 실패하였습니다.";

    public static final String EXPIRED_JWT_ERROR_CODE 				        = "JWT04";
    public static final String EXPIRED_JWT_ERROR_MESSAGE 			        = "JWT 기간이 만료되었습니다.";

    public static final String APPROVAL_JWT_CODE 				            = "JWT05";
    public static final String APPROVAL_JWT_MESSAGE 			            = "JWT 정상 인증되었습니다.";

    public static final String NON_VALIDATION_JWT_CODE 				        = "JWT06";
    public static final String NON_VALIDATION_JWT_MESSAGE 			        = "유효하지 않은 JWT입니다.";

    public static final String NON_ACCESS_JWT_CODE 				            = "JWT07";
    public static final String NON_ACCESS_JWT_MESSAGE 			            = "JWT토큰 접근 권한 인증에 실패했습니다.";

    public static final String JWT_GENERATE_FAIL_CODE 				        = "JWT08";
    public static final String JWT_GENERATE_FAIL_MESSAGE 			        = "JWT토큰 발급에 실패했습니다.";

    public static final String JWT_UPSERT_FAIL_CODE 				        = "JWT09";
    public static final String JWT_UPSERT_FAIL_MESSAGE 			            = "회원의 JWT토큰 저장 및 갱신에 실패했습니다.";

    public static final String EXPIRED_ACCESS_JWT_ERROR_CODE 				= "JWT10";
    public static final String EXPIRED_ACCESS_JWT_ERROR_MESSAGE 			= "ACCESS JWT 기간이 만료되었습니다.";

    public static final String EXPIRED_REFRESH_JWT_ERROR_CODE 				= "JWT11";
    public static final String EXPIRED_REFRESH_JWT_ERROR_MESSAGE 			= "REFRESH JWT expired! Please login again.";

    public static final String REISSUE_ACCESS_JWT_ERROR_CODE 				= "JWT12";
    public static final String REISSUE_ACCESS_JWT_ERROR_MESSAGE 			= "ACCESS JWT reissued!";

    public static final String EXTRACT_ACCESS_JWT_ERROR_CODE 				= "JWT13";
    public static final String EXTRACT_ACCESS_JWT_ERROR_MESSAGE 			= "헤더에서 토큰 추출 중 양식에 맞지 않아 추출에 실패했습니다.";

    public static final String SELECT_REFRESH_QUERY_JWT_ERROR_CODE 			= "JWT14";
    public static final String SELECT_REFRESH_QUERY_JWT_ERROR_MESSAGE 		= "특정 회원의 refresh 토큰 쿼리 조회에 실패했습니다.";

    public static final String WEIRD_ACCESS_JWT_ERROR_CODE 				    = "JWT15";
    public static final String WEIRD_ACCESS_JWT_ERROR_MESSAGE 			    = "You do not have access permission. Maybe ACCESS JWT is not invalid.";

    public static final String CHANGE_REFRESH_QUERY_JWT_ERROR_CODE 			= "JWT16";
    public static final String CHANGE_REFRESH_QUERY_JWT_ERROR_MESSAGE 		= "특정 회원의 refresh 토큰 값 수정에 실패했습니다.";

    public static final String USER_LDAP_LOGIN_FAIL_CODE                    = "AUTH01";
    public static final String USER_LDAP_LOGIN_FAIL_MESSAGE                 = "아이디 혹은 비밀번호가 잘못되었습니다.";

    public static final String USER_LOGOUT_FAIL_CODE						= "AUTH02";
    public static final String USER_LOGOUT_FAIL_MESSAGE				        = "로그아웃 중 에러가 발생했습니다.";

    public static final String USER_LOGIN_DUPLICATE_CODE					= "AUTH03";
    public static final String USER_LOGIN_DUPLICATE_MESSAGE				    = "Duplicate Login! Log out!";

    public static final String USER_INFO_FAIL_CODE		    			    = "AUTH04";
    public static final String USER_INFO_FAIL_MESSAGE   				    = "임직원 정보 조회 중 에러가 발생했습니다.";

    public static final String USER_INFO_UPSERT_FAIL_CODE					= "AUTH05";
    public static final String USER_INFO_UPSERT_FAIL_MESSAGE				= "임직원 정보 추가 및 수정 중 에러가 발생하였습니다.";

    public static final String USER_INFO_DELETE_FAIL_CODE					= "AUTH06";
    public static final String USER_INFO_DELETE_FAIL_MESSAGE				= "임직원 비활성화/활성화 중 에러가 발생하였습니다.";

    public static final String USER_INFO_INSERT_FAIL_CODE					= "AUTH07";
    public static final String USER_INFO_INSERT_FAIL_MESSAGE				= "임직원 정보 추가 중 에러가 발생하였습니다.";

    public static final String USER_INFO_CODE_FAIL_CODE 					= "AUTH08";
    public static final String USER_INFO_CODE_FAIL_MESSAGE  				= "임직원 정보 추가 시 내려주는 코드 응답 중 에러가 발생하였습니다.";

    public static final String USER_ID_VALIDATE_FAIL_CODE 					= "AUTH09";
    public static final String USER_ID_VALIDATE_FAIL_MESSAGE  				= "임직원 ID 중복체크 도중 에러가 발생하였습니다.";

    public static final String USER_DELETED_CODE 					        = "AUTH10";
    public static final String USER_DELETED_MESSAGE  				        = "비활성화된 임직원입니다.";

    public static final String USER_LOGIN_LOGIC_FAIL_CODE                   = "AUTH11";
    public static final String USER_LOGIN_LOGIC_FAIL_MESSAGE                = "로그인 로직 처리 중 에러가 발생했습니다.";

    public static final String USER_LDAP_UNKNOWN_FAIL_CODE                  = "AUTH12";
    public static final String USER_LDAP_UNKNOWN_FAIL_MESSAGE               = "LDAP 서버 요청 중 알 수 없는 에러가 발생했습니다.";

    public static final String USER_LIST_INFO_FAIL_CODE		    			= "AUTH13";
    public static final String USER_LIST_INFO_FAIL_MESSAGE   				= "모든 임직원 정보 리스트 조회 중 에러가 발생했습니다.";

    public static final String USER_LIST_DPT_FAIL_CODE		    			= "AUTH14";
    public static final String USER_LIST_DPT_FAIL_MESSAGE   				= "특정 부서에 해당하는 임직원 정보 리스트 조회 중 에러가 발생했습니다.";

    public static final String USER_INFO_UPDATE_FAIL_CODE					= "AUTH15";
    public static final String USER_INFO_UPDATE_FAIL_MESSAGE				= "임직원 정보 수정 중 에러가 발생하였습니다.";

    public static final String USER_LOGOUT_UPDATE_FAIL_CODE					= "AUTH16";
    public static final String USER_LOGOUT_UPDATE_FAIL_MESSAGE				= "다른 환경에서 로그인하여 로그아웃에 실패하였습니다.";

    public static final String USER_LOGIN_FAIL_CODE					        = "AUTH17";
    public static final String USER_LOGIN_FAIL_MESSAGE				        = "관리자가 아니므로 접근이 불가능합니다.";

    public static final String LDAP_VALIDATION_USER_FAIL_CODE				= "AUTH18";
    public static final String LDAP_VALIDATION_USER_FAIL_MESSAGE			= "LDAP 사용자 검증 중 에러가 발생했습니다.";

    public static final String LOGIN_SELECT_USER_FAIL_CODE				    = "AUTH19";
    public static final String LOGIN_SELECT_USER_FAIL_MESSAGE			    = "LDAP에는 등록되었으나 자산관리 서비스에 등록되지 않는 임직원입니다.";

    /**
     * 코드 관리 에러 코드
     */
    public static final String CODE_CATEGORY_LIST_FAIL_CODE					= "CODE01";
    public static final String CODE_CATEGORY_LIST_FAIL_MESSAGE				= "모든 카테고리 응답 도중 에러가 발생하였습니다.";

    public static final String CODE_CATEGORY_ADD_FAIL_CODE					= "CODE02";
    public static final String CODE_CATEGORY_ADD_FAIL_MESSAGE				= "카테고리 추가 중 에러가 발생하였습니다.";

    public static final String CODE_INFO_MODIFY_FAIL_CODE					= "CODE03";
    public static final String CODE_INFO_MODIFY_FAIL_MESSAGE				= "코드 정보 수정 도중 에러가 발생했습니다";

    public static final String CODE_INSERT_CATEGORY_FAIL_CODE				= "CODE04";
    public static final String CODE_INSERT_CATEGORY_FAIL_MESSAGE			= "특정 카테고리 내 코드 추가 도중 에러가 발생했습니다.";

    public static final String CODE_DELETE_CATEGORY_FAIL_CODE				= "CODE05";
    public static final String CODE_DELETE_CATEGORY_FAIL_MESSAGE			= "특정 카테고리 내 코드 삭제 도중 에러가 발생했습니다.";

    public static final String CODE_SELECT_CATEGORY_FAIL_CODE				= "CODE06";
    public static final String CODE_SELECT_CATEGORY_FAIL_MESSAGE			= "특정 카테고리 내 코드 리스트 조회 도중 에러가 발생했습니다.";

    public static final String CODE_VALIDATE_FAIL_CODE				        = "CODE07";
    public static final String CODE_VALIDATE_FAIL_MESSAGE			        = "코드 중복체크 도중 에러가 발생했습니다.";

    public static final String CODE_SELECT_DPT_FAIL_CODE				    = "CODE08";
    public static final String CODE_SELECT_DPT_FAIL_MESSAGE			        = "부서 코드 조회 도중 에러가 발생했습니다.";

    public static final String CODE_SELECT_PK_FAIL_CODE				        = "CODE09";
    public static final String CODE_SELECT_PK_FAIL_MESSAGE			        = "해당 코드는 비활성화 상태이거나 없으므로 등록이 불가능합니다. 코드 관리에서 확인해주세요";

    public static final String CODE_SELECT_ALL_DPT_FAIL_CODE				= "CODE10";
    public static final String CODE_SELECT_ALL_DPT_FAIL_MESSAGE			    = "모든 부서 코드 조회 도중 에러가 발생했습니다.";

    public static final String CODE_CATEGORY_SELECT_LIST_FAIL_CODE			= "CODE11";
    public static final String CODE_CATEGORY_SELECT_LIST_FAIL_MESSAGE		= "카테고리 리스트 조회 도중 에러가 발생하였습니다.";

    public static final String CODE_UPPER_SELECT_LIST_FAIL_CODE			    = "CODE12";
    public static final String CODE_UPPER_SELECT_LIST_FAIL_MESSAGE		    = "동일한 상위 코드를 가지는 코드 리스트 조회 도중 에러가 발생하였습니다.";

    public static final String CODE_SELECT_TYPE_NAME_FAIL_CODE				= "CODE13";
    public static final String CODE_SELECT_TYPE_NAME_FAIL_MESSAGE			= "특정 타입이나 이름을 가진 코드 리스트 조회 도중 에러가 발생했습니다.";

    public static final String CODE_CATEGORY_UPDATE_FAIL_CODE	    		= "CODE14";
    public static final String CODE_CATEGORY_UPDATE_FAIL_MESSAGE	    	= "카테고리 수정 도중 에러가 발생했습니다.";

    public static final String CODE_CATEGORY_DELETE_FAIL_CODE	    		= "CODE15";
    public static final String CODE_CATEGORY_DELETE_FAIL_MESSAGE	    	= "카테고리 삭제 도중 에러가 발생했습니다.";

    public static final String CODE_INFO_ACTIVE_FAIL_CODE	    		    = "CODE16";
    public static final String CODE_INFO_ACTIVE_FAIL_MESSAGE	    	    = "코드 활성화/비활성화 도중 에러가 발생했습니다.";

    public static final String CODE_HW_ASSET_SELECT_FAIL_CODE	    		= "CODE17";
    public static final String CODE_HW_ASSET_SELECT_FAIL_MESSAGE	    	= "HW ASSET 코드 조회 도중 에러가 발생했습니다.";

    public static final String CODE_SW_ASSET_SELECT_FAIL_CODE	    		= "CODE18";
    public static final String CODE_SW_ASSET_SELECT_FAIL_MESSAGE	    	= "SW ASSET 코드 조회 도중 에러가 발생했습니다.";

    public static final String CODE_UPDATE_FAIL_CODE	    		        = "CODE19";
    public static final String CODE_UPDATE_FAIL_MESSAGE	    	            = "코드 이름과 비고 수정 중 바뀐 내용이 없습니다.";

    public static final String CODE_UPDATE_DUP_CODE	    		            = "CODE20";
    public static final String CODE_UPDATE_DUP_MESSAGE	    	            = "이미 해당 이름이 존재합니다.";
    
}
