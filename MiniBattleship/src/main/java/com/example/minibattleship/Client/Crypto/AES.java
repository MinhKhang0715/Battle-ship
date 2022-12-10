package com.example.minibattleship.Client.Crypto;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
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

    private AES() {
    }

    public Key getAesKey() {
        return aesKey;
    }
}
