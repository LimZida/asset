package com.mcnc.assetmgmt.history.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mcnc.assetmgmt.code.entity.Code;
import com.mcnc.assetmgmt.code.repository.CodeRepository;
import com.mcnc.assetmgmt.history.dto.HistoryDto;
import com.mcnc.assetmgmt.history.entity.HistoryEntity;
import com.mcnc.assetmgmt.history.repository.HistoryRepository;
import com.mcnc.assetmgmt.util.common.CodeAs;
import com.mcnc.assetmgmt.util.common.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * title : HistoryService
 *
 * description : 전체 히스토리 / 히스토리 검색 기능
 *
 * reference :
 *
 * author : 임채성
 *
 * date : 2023.11.24
 **/
public interface HistoryService {
    Map getHistoryList(ObjectNode assignAssetHistoryObj);
    Map getAssetNameAndCodeService();
}
