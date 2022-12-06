package com.example.minibattleship.Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Server {
    public static int port = 1234;
    public static int numThread = 2;
    public static int orderNum = 1;
    //    contains username and a socket to send messages to
    public static Map<String, Socket> user = new HashMap<>();
    //    contains order of each player, First come first served
    public static Map<String, Integer> order = new HashMap<>();
    private static ServerSocket server = null;
    private static final List<Future<?>> futures = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        ExecutorService executor = Executors.newFixedThreadPool(numThread);
        try {
            server = new ServerSocket(port);
            System.out.println("Server binding at port " + port);
            System.out.println("Waiting for a sign...");
            while (true) {
                Socket socket = server.accept();
                futures.add(executor.submit(new Worker(socket)));
                boolean isAllDone = true;
//                executor.execute(new Worker(socket));
                for (Future<?> future : futures)
                    isAllDone &= future.isDone();
                if (isAllDone) break;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (server != null) {
                System.out.println("Closing server fr fr.");
                server.close();
            }
        }
    }
}
