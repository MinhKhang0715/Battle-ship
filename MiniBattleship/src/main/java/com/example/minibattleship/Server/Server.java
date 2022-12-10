package com.example.minibattleship.Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private final ServerSocket serverSocket;
    private boolean isGoFirst = true;
    private int id;

    public Server() {
        postIP();
        int port = 1234;
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server binding at port " + port);
        } catch (IOException e) {
            System.out.println("SERVER: CANNOT CREATE SERVER SOCKET");
            throw new RuntimeException(e);
        }
    }

    private void postIP() {
        try (Socket socket = new Socket("google.com", 80)) {
            String localIP = socket.getLocalAddress().toString().subString(1);
            String apiURL = "https://retoolapi.dev/aEVGGM/data/1";
            String jsonData = "{\"ip\":\"" + localIP + "\"}";
            System.out.println(jsonData);
            Jsoup.connect(apiURL)
                .ignoreContentType(true)
                .ignoreHttpError(true)
                .header("Content-Type", "application/json")
                .requestBody(jsonData)
                .method(Connection.Method.PUT).execute();
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
        }

    }

    public void startServer() {
        int numberOfThreads = 4;
        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
        try {
            while (!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
                id++;
                executor.execute(new Worker(socket, isGoFirst, id));
                isGoFirst = !isGoFirst;
            }
        } catch (IOException e) {
            closeServer();
            throw new RuntimeException(e);
        }
    }

    private void closeServer() {
        try {
            if (serverSocket != null)
                serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Server().startServer();
    }
}
