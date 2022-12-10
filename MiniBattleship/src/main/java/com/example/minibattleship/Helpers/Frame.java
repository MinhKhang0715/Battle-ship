package com.example.minibattleship.Helpers;

import java.io.Serializable;
import java.util.Arrays;

public class Frame implements Serializable {
    private int id;
    private byte[] keyInBytes;

    public Frame(int id, byte[] bytes) {
        this.id = id;
        this.keyInBytes = bytes;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setKeyInBytes(byte[] keyInBytes) {
        this.keyInBytes = keyInBytes;
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
