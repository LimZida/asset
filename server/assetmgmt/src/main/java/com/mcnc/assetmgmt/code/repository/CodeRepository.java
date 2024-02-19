package com.mcnc.assetmgmt.code.repository;

import com.mcnc.assetmgmt.code.entity.Code;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * title : CodeRepository
 *
 * description : 코드관리 기능에 사용하는 JPA쿼리
 *
 * reference : 쿼리 직접 사용시 https://sundries-in-myidea.tistory.com/91
 *             JPA 메서드 명령규칙 https://zara49.tistory.com/130
 *                              https://ozofweird.tistory.com/entry/%EC%82%BD%EC%A7%88-%ED%94%BC%ED%95%98%EA%B8%B0-JpaRepository-%EA%B7%9C%EC%B9%99%EC%97%90-%EB%A7%9E%EB%8A%94-%EB%A9%94%EC%84%9C%EB%93%9C
 *
 *             JPA like, containing 쿼리 https://velog.io/@jehpark/Spring-Data-JPA-%EC%BF%BC%EB%A6%AC-like-containing%EC%9D%98-%EC%B0%A8%EC%9D%B4%EC%A0%90
 * author : 임현영
 * date : 2023.11.14
 **/
@Repository
public interface CodeRepository extends JpaRepository<Code,Long> {
    Code save(Code code);
    List<Code> findByCodeType(String codeType);
    List<Code> findByCodeTypeAndActiveYn(String codeType, boolean activeYn);
    List<Code> findByCodeCtg(String codeCtg);
    List<Code> findByCodeCtgAndActiveYn(String codeCtg, boolean activeYn);
    List<Code> findByUpperCode(String upperCode);
    Code findByCode(String code);
    Code findByCodeAndActiveYn(String code, boolean activeYn);
    List<Code> findByCodeTypeAndCodeNameContaining(String codeType, String codeName);
    List<Code> findByCodeTypeAndCodeNameContainingAndCodeCtg(String codeType, String codeName, String codeCtg);
    List<Code> findByCodeTypeAndCodeNameContainingAndCodeIsNotAndCodeCtg(String codeType, String codeName, String code, String codeCtg);
    List<Code> findByCodeTypeAndUpperCodeAndCodeNameContainingAndCodeIsNot(String codeType,String upperCode ,String codeName, String code);
    List<Code> findByUpperCodeAndCodeNameContaining(String upperCode, String codeName);
}
