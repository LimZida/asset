package com.mcnc.assetmgmt.assignment.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mcnc.assetmgmt.asset.entity.HardwareEntity;
import com.mcnc.assetmgmt.asset.entity.SoftwareEntity;
import com.mcnc.assetmgmt.asset.repository.HardwareRepository;
import com.mcnc.assetmgmt.asset.repository.SoftwareRepository;
import com.mcnc.assetmgmt.assignment.dto.AssignmentDto;
import com.mcnc.assetmgmt.assignment.entity.AssignmentEntity;
import com.mcnc.assetmgmt.assignment.repository.AssignmentRepository;
import com.mcnc.assetmgmt.code.dto.CodeDto;
import com.mcnc.assetmgmt.code.entity.Code;
import com.mcnc.assetmgmt.code.repository.CodeRepository;
import com.mcnc.assetmgmt.history.entity.HistoryEntity;
import com.mcnc.assetmgmt.history.repository.HistoryRepository;
import com.mcnc.assetmgmt.user.dto.UserDto;
import com.mcnc.assetmgmt.user.entity.User;
import com.mcnc.assetmgmt.user.repository.UserRepository;
import com.mcnc.assetmgmt.user.service.UserService;
import com.mcnc.assetmgmt.util.common.CodeAs;
import com.mcnc.assetmgmt.util.common.Response;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.util.*;

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
public interface AssignmentService {
    Boolean saveAssetHistory(ObjectNode assignAssetObj);
    Map getAssignHistoryList(String assetNo);
}