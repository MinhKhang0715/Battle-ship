package com.example.minibattleship.Server;

import com.example.minibattleship.Helper.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class WorkerTest implements Runnable {
    private static final List<WorkerTest> listOfUsers = new ArrayList<>();
    private final Socket socket;
    private final ObjectInputStream inputReader;
    private final ObjectOutputStream outputWriter;
    private User user;
    private final boolean isGoFirst;

    public WorkerTest(Socket socket, boolean isGoFirst) {
        try {
            this.socket = socket;
            this.isGoFirst = isGoFirst;
            this.outputWriter = new ObjectOutputStream(socket.getOutputStream());
            System.out.println("From constructor isGoFirst: " + isGoFirst);
            listOfUsers.add(this);
            inputReader = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            System.out.println("WORKER: ERROR AT CONSTRUCTOR");
            throw new RuntimeException(e);
        }
    }

    static void testList() {
        System.out.println(listOfUsers.size());
        for (int i = 0; i < listOfUsers.size(); i++) {
            if (listOfUsers.get(i).user != null) {
                System.out.println("Info about user " + i);
                System.out.println("Username: " + listOfUsers.get(i).user.getUsername());
                System.out.println("Game state: " + listOfUsers.get(i).user.getGameState());
                System.out.println("Message: " + listOfUsers.get(i).user.getMessage());
                System.out.println("Is go first: " + listOfUsers.get(i).isGoFirst);
            }
        }
    }

    private void sendMessage(User message) {
        for (WorkerTest worker : listOfUsers) {
            if (!worker.user.getUsername().equals(this.user.getUsername())) {
                try {
                    worker.outputWriter.writeObject(message);
                    worker.outputWriter.flush();
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
                user = (User) inputReader.readObject();
                switch (user.getGameState()) {
                    case "PlacingShip" -> {
                        System.out.println("Placing ships state");
                        testList();
                        if (listOfUsers.size() != 1)
                            sendMessage(user.setIsGoFirst(this.isGoFirst));
                    }
                    case "Battling" -> {
                        System.out.println("Battling state");
                        testList();
                        sendMessage(user);
                    }
//                    case "Finished" -> {}
                }
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
