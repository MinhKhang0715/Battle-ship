package com.example.minibattleship.Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerTest {
    private final ServerSocket serverSocket;
    private boolean isGoFirst = true;

    public ServerTest() {
        int port = 1234;
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server binding at port " + port);
        } catch (IOException e) {
            System.out.println("SERVER: CANNOT CREATE SERVER SOCKET");
            throw new RuntimeException(e);
        }
    }

    public void startServer() {
        int numberOfThreads = 2;
        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
        try {
            while (!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
                executor.execute(new WorkerTest(socket, isGoFirst));
                isGoFirst = !isGoFirst;
            }
        } catch (IOException e) {
            closeServer();
            throw new RuntimeException(e);
        }
    }

    public void closeServer() {
        try {
            if (serverSocket != null)
                serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        ServerTest server = new ServerTest();
        server.startServer();
    }
}
