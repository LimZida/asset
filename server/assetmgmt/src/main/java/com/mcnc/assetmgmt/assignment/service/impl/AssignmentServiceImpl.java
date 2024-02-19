package com.mcnc.assetmgmt.assignment.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mcnc.assetmgmt.asset.entity.HardwareEntity;
import com.mcnc.assetmgmt.asset.entity.SoftwareEntity;
import com.mcnc.assetmgmt.asset.repository.HardwareRepository;
import com.mcnc.assetmgmt.asset.repository.SoftwareRepository;
import com.mcnc.assetmgmt.asset.service.common.AssetCommonComponent;
import com.mcnc.assetmgmt.assignment.dto.AssignmentDto;
import com.mcnc.assetmgmt.assignment.entity.AssignmentEntity;
import com.mcnc.assetmgmt.assignment.repository.AssignmentRepository;
import com.mcnc.assetmgmt.assignment.service.AssignmentService;
import com.mcnc.assetmgmt.code.dto.CodeDto;
import com.mcnc.assetmgmt.code.entity.Code;
import com.mcnc.assetmgmt.code.repository.CodeRepository;
import com.mcnc.assetmgmt.history.dto.HistoryDto;
import com.mcnc.assetmgmt.history.entity.HistoryEntity;
import com.mcnc.assetmgmt.history.repository.HistoryRepository;
import com.mcnc.assetmgmt.user.dto.UserDto;
import com.mcnc.assetmgmt.user.entity.User;
import com.mcnc.assetmgmt.user.repository.UserRepository;
import com.mcnc.assetmgmt.user.service.UserService;
import com.mcnc.assetmgmt.util.common.CodeAs;
import com.mcnc.assetmgmt.util.common.Response;
import com.mcnc.assetmgmt.util.exception.CustomException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.mcnc.assetmgmt.util.common.CodeAs.*;

/**
 * title : AssignmentService
 *
 * description : 자원할당 / 개별 자원 히스토리 / 유저 정보

 * reference :
 *
 * author : 임채성
 *
 * date : 23.11.24
 **/
@Service
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class AssignmentServiceImpl implements AssignmentService {
    private final UserService userService;
    private final AssignmentRepository assignmentRepository;
    private final HistoryRepository historyRepository;
    private final CodeRepository codeRepository;
    private final UserRepository userRepository;
    private final HardwareRepository hardwareRepository;
    private final SoftwareRepository softwareRepository;
    private final AssetCommonComponent assetCommonComponent;
    @Transactional
    public Boolean saveAssetHistory(ObjectNode assignAssetObj) {
        try {
            Date today = new Date();
            Locale currentLocale = new Locale(CodeAs.KOREAN, CodeAs.KOREA);
            String pattern = CodeAs.TIMESTAMP_PATTERN; //hhmmss로 시간,분,초만 뽑기도 가능
            SimpleDateFormat formatter = new SimpleDateFormat(pattern, currentLocale);
            long createDate = Long.parseLong(formatter.format(today));

            ObjectMapper objMapper = new ObjectMapper();
            Object asset = objMapper.treeToValue(assignAssetObj.get("assetAssignList"), AssignmentDto.class);
            AssignmentDto assignmentDto = (AssignmentDto) asset;

            // HW일 경우 자산번호가 이미 존재하면 Update 없으면 Insert
            if(assignmentDto.getAssetHwSwFlag().equals(CodeAs.HW)){
                // assetNo(자산번호) 확인 후 없으면 insert 있으면 update를 위한 변수 있으면 ture 없으면 false
                boolean existAssetNo = assignmentRepository.existsByAssetNo(assignmentDto.getAssetNo());
                // 자산번호로 할당 정보 찾은 후 저장
                AssignmentEntity findByAssetNoAssetId = assignmentRepository.findByAssetNo(assignmentDto.getAssetNo());
                // 자산번호로 하드웨어 정보 찾은 후 저장
                HardwareEntity findByHwNo =  hardwareRepository.findByHwNo(assignmentDto.getAssetNo());

                // 자산번호가 이미 존재 (이미 할당한 내역이 있는 경우) update
                if (existAssetNo == true) {
                    // newUserId가 있을 경우 user 테이블에서 정보 찾아온 후 저장
                    if(!assignmentDto.getNewUserId().equals(CodeAs.BLANK)) {
                        setNewUserInfo(assignmentDto);
                    }else {
                        assignmentDto.setNewUserDeptCode(CodeAs.BLANK);
                    }
                    assignmentDto.setAssetCategory(findByHwNo.getHwDiv().getCode());
                    assignmentDto.setModelName(findByHwNo.getHwModel());

                    setUpdateAssignmentDto(findByAssetNoAssetId, assignmentDto);

                    // 자원 할당 저장
                    assignmentRepository.save(assignmentEntity(assignmentDto));
                    // 히스토리 저장
                    // prevUserId가 있을 경우 user 테이블에서 정보 찾아온 후 저장
                    if(!assignmentDto.getPrevUserId().equals(CodeAs.BLANK)) {
                        User user = userRepository.findByUserId(assignmentDto.getPrevUserId());
                        assignmentDto.setPrevUserName(user.getUserName());
                        assignmentDto.setPrevUserDeptName(user.getCode().getCodeName());
                    }
                    setInsertHistory(assignmentDto, createDate);

                    Code codecheck = findCodeByPk(assignmentDto.getAssetCategory());
                    assignmentDto.setAssetCategory(codecheck.getCodeName());

                    historyRepository.save(historyEntity(assignmentDto));
                    return true;
                }
                // 자산번호가 존재 하지 않으므로 insert
                setInsertAssignmentDto(assignmentDto);

                assignmentDto.setAssetCategory(findByHwNo.getHwDiv().getCode());
                assignmentDto.setModelName(findByHwNo.getHwModel());
                // 자원 할당 저장
                assignmentRepository.save(assignmentEntity(assignmentDto));
                // 히스토리 저장
                setInsertHistory(assignmentDto, createDate);

                Code codecheck = findCodeByPk(assignmentDto.getAssetCategory());
                assignmentDto.setAssetCategory(codecheck.getCodeName());

                historyRepository.save(historyEntity(assignmentDto));
                return true;
            }
            // SW일 경우 이미 자산번호가 존재해도 Insert 가능
            else {
                SoftwareEntity findBySwNo =  softwareRepository.findBySwNo(assignmentDto.getAssetNo());
                // update
                if (assignmentDto.getPrevUserId() != CodeAs.BLANK) {
                    List<AssignmentEntity> newFindByAssetNoAndUsageId =
                            assignmentRepository.findByAssetNoAndUsageIdOrderByCreateDateDesc(
                                    assignmentDto.getAssetNo(), assignmentDto.getNewUserId());
                    // 이미 sw에 저장된 id일 경우
                    if(newFindByAssetNoAndUsageId.size() == ZERO || BLANK.equals(assignmentDto.getNewUserId())) {
                        AssignmentEntity findByAssetNoAndUsageId =
                                assignmentRepository.findByAssetNoAndUsageId(
                                        assignmentDto.getAssetNo(), assignmentDto.getPrevUserId());
                        // newUserId가 있을 경우 user 테이블에서 정보 찾아온 후 저장
                        if(!assignmentDto.getNewUserId().equals(CodeAs.BLANK)) {
                            setNewUserInfo(assignmentDto);
                        }else{
                            assignmentDto.setNewUserDeptCode(CodeAs.BLANK);
                        }

                        assignmentDto.setAssetCategory(findBySwNo.getSwDiv().getCode());
                        assignmentDto.setModelName(findBySwNo.getSwName());

                        setUpdateAssignmentDto(findByAssetNoAndUsageId, assignmentDto);

                        // 자원 할당 저장
                        assignmentRepository.save(assignmentEntity(assignmentDto));
                        // 히스토리 저장
                        User user = userRepository.findByUserId(assignmentDto.getPrevUserId());
                        assignmentDto.setPrevUserName(user.getUserName());
                        assignmentDto.setPrevUserDeptName(user.getCode().getCodeName());

                        setInsertHistory(assignmentDto, createDate);

                        Code codecheck = findCodeByPk(assignmentDto.getAssetCategory());
                        assignmentDto.setAssetCategory(codecheck.getCodeName());

                        historyRepository.save(historyEntity(assignmentDto));
                        return true;
                    }else{
                        throw Response.makeFailResponse(CodeAs.ASSET_ALLOCATE_SW_VALIDATE_FAIL_CODE, HttpStatus.BAD_REQUEST,
                                CodeAs.ASSET_ALLOCATE_SW_VALIDATE_FAIL_MESSAGE, null);
                    }
                }
                // 현재 할당된 소프트웨어 개수가 수량이랑 많거나 같으면 더이상 등록불가
                if(assignmentRepository.countByAssetNoAndAssetStatus(assignmentDto.getAssetNo(), findCodeByPk(STATUS02))
                        >= findBySwNo.getSwQuantity()){
                    throw Response.makeFailResponse(CodeAs.ASSET_ALLOCATE_QUANTITY_FAIL_CODE, HttpStatus.BAD_REQUEST,
                            CodeAs.ASSET_ALLOCATE_QUANTITY_FAIL_MESSAGE, null);
                }

                AssignmentEntity findByAssetNoAndUsageId =
                        assignmentRepository.findByAssetNoAndUsageId(
                                assignmentDto.getAssetNo(), assignmentDto.getNewUserId());

                if(findByAssetNoAndUsageId == null){

                    setInsertAssignmentDto(assignmentDto);

                    assignmentDto.setAssetCategory(findBySwNo.getSwDiv().getCode());
                    assignmentDto.setModelName(findBySwNo.getSwName());

                    // 자원 할당 저장
                    assignmentRepository.save(assignmentEntity(assignmentDto));
                    // 히스토리 저장
                    setInsertHistory(assignmentDto, createDate);

                    Code codecheck = findCodeByPk(assignmentDto.getAssetCategory());
                    assignmentDto.setAssetCategory(codecheck.getCodeName());

                    historyRepository.save(historyEntity(assignmentDto));
                    return true;
                } else {
                    throw Response.makeFailResponse(CodeAs.ASSET_ALLOCATE_SW_VALIDATE_FAIL_CODE, HttpStatus.BAD_REQUEST,
                            CodeAs.ASSET_ALLOCATE_SW_VALIDATE_FAIL_MESSAGE, null);
                }
            }
        }catch (Exception e) {
            // 에러 코드 확인 후 수정
            throw Response.makeFailResponse(CodeAs.ASSET_ALLOCATE_FAIL_CODE, HttpStatus.BAD_REQUEST,
                    CodeAs.ASSET_ALLOCATE_FAIL_MESSAGE, e);
        }
    }

    // 할당 자원 히스토리
    @Transactional
    public Map getAssignHistoryList(String assetNo) {
        try{
            Map<Object, Object> map= new HashMap<>();

            List<HistoryEntity> findByAssetHistory = historyRepository.findByAssetNoOrderByCreateDateDesc(assetNo);
            // 유저정보
            List<UserDto.userListInfo> userList = userService.getActiveEmployeeService();
            List<CodeDto.deptInfo> deptList = userService.activeDeptCodeService();

            List<HistoryDto.historyInfo> res = new ArrayList<>();

            for(HistoryEntity history : findByAssetHistory) {
                HistoryDto.historyInfo historyInfo = HistoryDto.historyInfo.builder()
                        .assetNo(Response.getOrDefaultString(history.getAssetNo()))
                        .assetCategory(Response.getOrDefaultString(history.getAssetCategory()))
                        .newUserName(Response.getOrDefaultString(history.getNewUserName()))
                        .newUserDept(Response.getOrDefaultString(history.getNewUserDept()))
                        .newUserId(Response.getOrDefaultString(history.getNewUserId()))
                        .previousUserId(Response.getOrDefaultString(history.getPreviousUserId()))
                        .previousUserName(Response.getOrDefaultString(history.getPreviousUserName()))
                        .previousUserDept(Response.getOrDefaultString(history.getPreviousUserDept()))
                        .assetStatus(Response.getOrDefaultString(history.getAssetStatus()))
                        .modelName(Response.getOrDefaultString(history.getModelName()))
                        .assetFlag(Response.getOrDefaultString(history.getAssetFlag()))
                        // timeStamp 형식에서 String 형식으로 변환
                        .createDate(assetCommonComponent.convertLocalDateTimeToString(history.getCreateDate()))
                        .build();

                res.add(historyInfo);
            }

            map.put(CodeAs.ASSIGN_HISTORY, res);
            map.put(CodeAs.USER_LIST, userList);
            map.put(CodeAs.DEPT_LIST, deptList);

            return map;
        }catch (Exception e) {
            // 에러 코드 확인 후 수정
            throw  Response.makeFailResponse(CodeAs.ASSET_ALLOCATE_HISTORY_FAIL_CODE, HttpStatus.BAD_REQUEST,
                    CodeAs.ASSET_ALLOCATE_HISTORY_FAIL_MESSAGE, e);
        }
    }

    private AssignmentEntity assignmentEntity(AssignmentDto dto) throws Exception {
        AssignmentEntity build = AssignmentEntity.builder()
                .id(dto.getId())
                .assetNo(dto.getAssetNo())
                .assetStatus(findCodeByPk(dto.getAssetStatus()))
                .usageDept(!dto.getNewUserDeptCode().isEmpty() ? findCodeByPk(dto.getNewUserDeptCode()) :
                        null)
                .usageName(dto.getNewUserName())
                .usageId(dto.getNewUserId())
                .createId(dto.getCreateId())
                .updateId(dto.getUpdateId())
                .deleteYn(dto.getDeleteYn())
                .build();
        return build;
    }

    private HistoryEntity historyEntity(AssignmentDto dto) {
        HistoryEntity build = HistoryEntity.builder()
                .id(dto.getId())
                .assetNo(dto.getAssetNo())
                .previousUserId(dto.getPrevUserId())
                .newUserId(dto.getNewUserId())
                .previousUserDept(dto.getPrevUserDeptName())
                .previousUserName(dto.getPrevUserName())
                .newUserDept(dto.getNewUserDeptName())
                .assetCategory(dto.getAssetCategory())
                .newUserName(dto.getNewUserName())
                .modelName(dto.getModelName())
                .assetStatus(dto.getAssetStatus())
                .assetFlag(dto.getAssetHwSwFlag())
                .build();
        return build;
    }

    private Code findCodeByPk(String codePk) throws Exception {
        Code code = codeRepository.findByCode(codePk);
        if(code == null){
            throw new CustomException(CodeAs.CODE_SELECT_PK_FAIL_CODE, HttpStatus.INTERNAL_SERVER_ERROR,
                    CodeAs.CODE_SELECT_PK_FAIL_MESSAGE,null);
        }else{
            return code;
        }
    }
    // 처음 자원 할당할 때 사용
    private AssignmentDto setInsertAssignmentDto(AssignmentDto assignmentDto){
        User user = userRepository.findByUserId(assignmentDto.getNewUserId());
        assignmentDto.setNewUserName(user.getUserName());
        assignmentDto.setNewUserDeptCode(user.getCode().getCode());
        assignmentDto.setNewUserDeptName(user.getCode().getCodeName());
        assignmentDto.setDeleteYn(CodeAs.NEGATIVE);
        assignmentDto.setCreateId(CodeAs.ADMIN);
        return assignmentDto;
    }
    // 히스토리 Insert시 사용 (상태 코드 처리)
    private AssignmentDto setInsertHistory(AssignmentDto assignmentDto , Long createDate){
        assignmentDto.setId(null);
        if(assignmentDto.getAssetStatus().equals(CodeAs.STATUS01)){
            assignmentDto.setAssetStatus(CodeAs.IN);
        }else if(assignmentDto.getAssetStatus().equals(STATUS02)){
            assignmentDto.setAssetStatus(CodeAs.OUT);
        }
        return assignmentDto;
    }
    // 할당 Update시 사용
    private AssignmentDto setUpdateAssignmentDto(AssignmentEntity assignmentEntity, AssignmentDto assignmentDto){
        assignmentDto.setId(assignmentEntity.getId());
        assignmentDto.setCreateId(CodeAs.ADMIN);
        assignmentDto.setDeleteYn(CodeAs.NEGATIVE);
        assignmentDto.setUpdateId(CodeAs.ADMIN);
        return assignmentDto;
    }
    // Update시 New User 정보 추가
    private AssignmentDto setNewUserInfo(AssignmentDto assignmentDto){
        User user = userRepository.findByUserId(assignmentDto.getNewUserId());
        assignmentDto.setNewUserName(user.getUserName());
        assignmentDto.setNewUserDeptCode(user.getCode().getCode());
        assignmentDto.setNewUserDeptName(user.getCode().getCodeName());
        return assignmentDto;
    }
    // Long에 대한 시간을 String으로 변환하는 함수
    // ex) 20231123132610 => 2023-11-23
    private String mapLongToStringTime(Long timestamp){
        if(timestamp == null){
            return CodeAs.NULL_TEXT;
        }
        LocalDateTime dateTime = LocalDateTime.parse(String.valueOf(timestamp),
                DateTimeFormatter.ofPattern(CodeAs.TIMESTAMP_PATTERN));

        // LocalDateTime을 원하는 형식의 문자열로 변환
        String formattedDate = dateTime.format(DateTimeFormatter.ofPattern(CodeAs.DATE_PATTERN));

        return formattedDate;
    }
}