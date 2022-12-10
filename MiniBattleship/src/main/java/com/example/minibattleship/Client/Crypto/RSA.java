package com.example.minibattleship.Client.Crypto;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

public class RSA {
    private PublicKey publicKey;

    public RSA(byte[] keyInBytes) {
        try {
            EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(keyInBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            publicKey = keyFactory.generatePublic(publicKeySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
    }

    public byte[] encrypt(byte[] input) {
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            return cipher.doFinal(input);
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | IllegalBlockSizeException |
                 BadPaddingException e) {
            throw new RuntimeException(e);
        }
    }
}
