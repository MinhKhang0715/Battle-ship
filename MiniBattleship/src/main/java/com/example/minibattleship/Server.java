package com.example.minibattleship;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    public static int port = 1234;
    public static int numThread = 2;
    private static ServerSocket server = null;
    public static int orderNum = 1;

//    contains username and a socket to send messages to
    public static Map<String, Socket> user = new HashMap<String,Socket>();
//    contains order of each player, First come first serve
    public static Map<String, Integer> order = new HashMap<String,Integer>();

    public static void main(String[] args) throws IOException {
        ExecutorService executor = Executors.newFixedThreadPool(numThread);
        try{
            server = new ServerSocket(port);
            System.out.println("Server binding at port " + port);

            System.out.println("Waiting for a sign...");
            while(true){
                Socket socket = server.accept();
                executor.execute(new Worker(socket));
            }


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        finally {
            if(server!=null){
                System.out.println("Closing server fr fr.");
                server.close();
            }
        }
    }
}
