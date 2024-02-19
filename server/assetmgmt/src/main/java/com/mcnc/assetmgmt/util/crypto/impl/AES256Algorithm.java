package com.mcnc.assetmgmt.util.crypto.impl;

import com.mcnc.assetmgmt.util.common.CodeAs;
import com.mcnc.assetmgmt.util.crypto.CryptAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
/**
 * title : AES256Algorithm
 *
 * description : 암호화 CryptAlgorithm 구현체 , AES256 방식
 *
 * reference : 참고 코드 => https://bamdule.tistory.com/234
 *             AES 온라인 툴 => https://www.devglan.com/online-tools/aes-encryption-decryption
 *
 * author : 임현영
 * date : 2024.01.22
 **/
@Component
public class AES256Algorithm implements CryptAlgorithm {
    private final String alg;
    private final String key;

    public AES256Algorithm(@Value("${aes.alg}") String alg,
                           @Value("${aes.key}") String key){
        this.alg = alg;
        this.key = key;
    }

    @Override
    public String encrypt(String text) throws Exception {
        Cipher cipher = initializeCipher(Cipher.ENCRYPT_MODE);

        byte[] encrypted = cipher.doFinal(text.getBytes(CodeAs.CHARSET));
        return Base64.getEncoder().encodeToString(encrypted);
    }

    @Override
    public String decrypt(String cipherText) throws Exception {
        Cipher cipher = initializeCipher(Cipher.DECRYPT_MODE);

        byte[] decodedBytes = Base64.getDecoder().decode(cipherText);
        byte[] decrypted = cipher.doFinal(decodedBytes);

        return new String(decrypted, CodeAs.CHARSET);
    }

    private Cipher initializeCipher(int mode) throws Exception {
        String iv = key.substring(0, 16);
        Cipher cipher = Cipher.getInstance(alg);

        SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), CodeAs.ENCRYPT_AES);
        IvParameterSpec ivParamSpec = new IvParameterSpec(iv.getBytes());
        cipher.init(mode, keySpec, ivParamSpec);

        return cipher;
    }
}
