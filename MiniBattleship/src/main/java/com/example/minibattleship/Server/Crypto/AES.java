package com.example.minibattleship.Server.Crypto;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.Serializable;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

public class AES {
    private final Key aesKey;
    private static AES aesInstance;

    public static AES getInstance(byte[] keyInBytes) {
        if (aesInstance == null)
            aesInstance = new AES(keyInBytes);
        return aesInstance;
    }

    private AES(byte[] keyInBytes) {
        aesKey = new SecretKeySpec(keyInBytes, 0, keyInBytes.length, "AES");
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
