package com.example.minibattleship.Server;

import com.example.minibattleship.Helpers.Frame;
import com.example.minibattleship.Helpers.UserMessage;
import com.example.minibattleship.Server.Crypto.AES;
import com.example.minibattleship.Server.Crypto.RSA;

import javax.crypto.SealedObject;
import java.io.*;
import java.net.Socket;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Worker implements Runnable {
    private static final List<Worker> listOfUsers = new ArrayList<>();
    private final Socket socket;
    private final ObjectInputStream inputReader;
    private final ObjectOutputStream outputWriter;
    private UserMessage userMessage;
    private final boolean isGoFirst;
    private final AES aesCrypto;

    public Worker(Socket socket, boolean isGoFirst, int id) {
        RSA rsaCrypto = RSA.getInstance();
        PublicKey publicKey = rsaCrypto.getPublicKey();
        Frame frame = new Frame(id, publicKey.getEncoded());
        try {
            this.socket = socket;
            this.isGoFirst = isGoFirst;
            this.outputWriter = new ObjectOutputStream(socket.getOutputStream());
            outputWriter.writeObject(frame);
            outputWriter.flush();
            listOfUsers.add(this);
            inputReader = new ObjectInputStream(socket.getInputStream());
            Frame fromClient = (Frame) inputReader.readObject();
            byte[] aesKey = rsaCrypto.decrypt(fromClient.getKeyInBytes());
            aesCrypto = AES.getInstance(aesKey);
            System.out.println(Arrays.toString(aesKey));
        } catch (IOException e) {
            System.out.println("WORKER: ERROR AT CONSTRUCTOR");
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendMessage(UserMessage message) {
        for (Worker worker : listOfUsers) {
            if (worker.userMessage.getId() != this.userMessage.getId()) {
                try {
                    worker.outputWriter.writeObject(aesCrypto.encrypt(message));
                    worker.outputWriter.flush();
                    System.out.println("Sent " + message.getUsername() + " with msg: " + message.getMessage());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    @Override
    public void run() {
        System.out.println("Client " + socket + " accepted");
        while (socket.isConnected()) {
            try {
                SealedObject fromClient = (SealedObject) inputReader.readObject();
                userMessage = (UserMessage) aesCrypto.decrypt(fromClient);
                switch (userMessage.getGameState()) {
                    case "PlacingShip" -> {
                        System.out.println("Placing ships state");
//                        testList();
                        if (listOfUsers.size() != 1)
                            sendMessage(userMessage.setIsGoFirst(this.isGoFirst));
                    }
                    case "Battling" -> {
                        System.out.println("Battling state");
                        System.out.println("Receive from " + userMessage.getUsername() + " with msg: " + userMessage.getMessage());
//                        testList();
                        sendMessage(userMessage);
                    }
//                    case "Finished" -> {}
                }
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
