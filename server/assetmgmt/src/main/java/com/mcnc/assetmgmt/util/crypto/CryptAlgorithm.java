package com.mcnc.assetmgmt.util.crypto;
/**
 * title : CryptAlgorithm
 *
 * description : 암호화 인터페이스
 *
 * reference :
 *
 * author : 임현영
 * date : 2024.01.22
 **/
public interface CryptAlgorithm {
    String encrypt(String text) throws Exception;
    String decrypt(String cipherText) throws Exception;
}
