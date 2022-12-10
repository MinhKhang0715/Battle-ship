package com.example.minibattleship.Client;

import com.example.minibattleship.Helpers.UserMessage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class TCPConnection {
    private static TCPConnection instance;
    private final Socket socket;
    private final ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;

    private TCPConnection(Socket socket) {
        try {
            this.socket = socket;
            outputStream = new ObjectOutputStream(this.socket.getOutputStream());
        } catch (IOException e) {
            System.out.println("At TCPConnection constructor");
            throw new RuntimeException(e);
        }
    }

    public static TCPConnection getInstance(Socket socket) {
        if (instance == null)
            instance = new TCPConnection(socket);
        return instance;
    }

    public boolean isClose() {
        return socket.isClosed();
    }

    public void sendMessage(Object messageObject) {
        try {
            outputStream.writeObject(messageObject);
            outputStream.flush();
        } catch (IOException e) {
            System.out.println("ERROR AT TCPConnection.sendMessage");
            throw new RuntimeException(e);
        }
    }

    public Object readMessage() {
        try {
            if (inputStream == null)
                inputStream = new ObjectInputStream(this.socket.getInputStream());
            return inputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public int readInt() {
        try {
            if (inputStream == null)
                inputStream = new ObjectInputStream(this.socket.getInputStream());
            return inputStream.readInt();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
