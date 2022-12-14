package com.example.minibattleship.Helpers;

import java.io.Serializable;
import java.util.Arrays;

public class Frame implements Serializable {
    private final int id;
    private final byte[] keyInBytes;
    private int timeout;

    public Frame(int id, byte[] bytes) {
        this.id = id;
        this.keyInBytes = bytes;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public byte[] getKeyInBytes() {
        return keyInBytes;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Frame{" +
                "id=" + id +
                ", keyInBytes=" + Arrays.toString(keyInBytes) +
                '}';
    }
}
