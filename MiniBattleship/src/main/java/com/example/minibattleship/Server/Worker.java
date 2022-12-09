package com.example.minibattleship.Server;

import com.example.minibattleship.Helper.UserMessage;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Worker implements Runnable {
    private static final List<Worker> listOfUsers = new ArrayList<>();
    private final Socket socket;
    private final ObjectInputStream inputReader;
    private final ObjectOutputStream outputWriter;
    private UserMessage userMessage;
    private final boolean isGoFirst;

    public Worker(Socket socket, boolean isGoFirst, int id) {
        try {
            this.socket = socket;
            this.isGoFirst = isGoFirst;
            this.outputWriter = new ObjectOutputStream(socket.getOutputStream());
            outputWriter.writeInt(id);
            outputWriter.flush();
            System.out.println("From constructor isGoFirst: " + isGoFirst);
            listOfUsers.add(this);
            inputReader = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            System.out.println("WORKER: ERROR AT CONSTRUCTOR");
            throw new RuntimeException(e);
        }
    }

//    static void testList() {
//        System.out.println(listOfUsers.size());
//        for (int i = 0; i < listOfUsers.size(); i++) {
//            if (listOfUsers.get(i).userMessage != null) {
//                System.out.println("Info about user " + i);
//                System.out.println("Username: " + listOfUsers.get(i).userMessage.getUsername());
//                System.out.println("Game state: " + listOfUsers.get(i).userMessage.getGameState());
//                System.out.println("Message: " + listOfUsers.get(i).userMessage.getMessage());
//                System.out.println("Is go first: " + listOfUsers.get(i).isGoFirst);
//            }
//        }
//    }

    private void sendMessage(UserMessage message) {
        for (Worker worker : listOfUsers) {
            if (worker.userMessage.getId() != this.userMessage.getId()) {
                try {
                    worker.outputWriter.writeObject(message);
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
                userMessage = (UserMessage) inputReader.readObject();
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
