package com.example.minibattleship.Client.Crypto;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.Serializable;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

public class AES {
    private final String key = "Bar12345Bar12345";
    private final byte[] decodedKey = Base64.getDecoder().decode(key);
    private final Key aesKey = new SecretKeySpec(Arrays.copyOf(decodedKey, 16), "AES");
    private static AES aesInstance;

    public static AES getInstance() {
        if (aesInstance == null)
            aesInstance = new AES();
        return aesInstance;
    }

    private AES() {}

    public Key getAesKey() {
        return aesKey;
    }

    public SealedObject encrypt(Serializable object) {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, aesKey);
            return new SealedObject(object, cipher);
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | IllegalBlockSizeException |
                 IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Serializable decrypt(SealedObject sealedObject) {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, aesKey);
            return (Serializable) sealedObject.getObject(cipher);
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | IllegalBlockSizeException |
                 IOException | ClassNotFoundException | BadPaddingException e) {
            throw new RuntimeException(e);
        }
    }
}
