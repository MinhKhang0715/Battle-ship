package com.example.minibattleship.Server.Crypto;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.*;

public class RSA {
    private final KeyPair keyPair;
    private static RSA instance;

    public static RSA getInstance() {
        if (instance == null)
            instance = new RSA();
        return instance;
    }

    private RSA() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            keyPair = keyPairGenerator.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public PrivateKey getPrivateKey() {
        return keyPair.getPrivate();
    }

    public PublicKey getPublicKey() {
        return keyPair.getPublic();
    }

    public byte[] decrypt(byte[] input) {
        try {
            Cipher decryptCipher = Cipher.getInstance("RSA");
            decryptCipher.init(Cipher.DECRYPT_MODE, this.getPrivateKey());
            return decryptCipher.doFinal(input);
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | IllegalBlockSizeException |
                 BadPaddingException e) {
            throw new RuntimeException(e);
        }
    }
}
