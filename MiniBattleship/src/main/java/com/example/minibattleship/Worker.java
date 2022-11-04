package com.example.minibattleship;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Worker implements Runnable {
    private Socket socket;

    public Worker(Socket socket) {
        this.socket = socket;

    }


    public void run() {
        System.out.println("Client " + socket.toString() + "accepted");
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            String input = "";
//            get username and add to the hashmaps
            input = in.readLine();
//            if (input.equals("bye")) {
//                System.out.println("Closing server socket " + socket.toString());
//                in.close();
//                out.close();
//                socket.close();
//            }
            Server.user.put(input, socket);
            Server.order.put(input, Server.orderNum);
            String orderNumber = String.valueOf(Server.order.get(input));
            System.out.println("Sending Order #: " +orderNumber);
//            give players their order
            out.write(orderNumber);
            out.newLine();
            out.flush();
            Server.orderNum++;
            System.out.println("Order, Grand: " + Server.order);

            System.out.println("Socket, I hope: " + Server.user);
            System.out.println("Particular socket, I hope: " + Server.user.get(input));
            System.out.println("Server received: " + input + " from " + socket.toString());
//            out.write("Username: " + input);
//            out.newLine();
//            out.flush();
            while (true) {
                input = in.readLine();
                if (input.equals("bye")) {
                    System.out.println("Closing server socket " + socket.toString());
                    in.close();
                    out.close();
                    socket.close();
                    break;
                }
//                message = username;action;coordinates or "all";action;coordinates(this is for sending to everyone).
                String[] message = input.split(";");
                if (message.length == 2) {
                    writeToUser("all",message[0], message[1]);
                } else if (message.length == 3) {
                    writeToUser(message[0], message[1], message[2]);
                } else {
                    for (String ele : message) {
                        System.out.println("Message: " + ele);
                    }
                    System.out.println("Something weird happened.");
                }

            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    write to someone or everyone
    private void writeToUser(String username, String action, String message) throws IOException {
        if(username.equals("all")){
            BufferedWriter writer = null;

            try {
                for (Map.Entry<String, Socket> entry : Server.user.entrySet()) {
                    Socket temp = entry.getValue();
                    writer = new BufferedWriter(new OutputStreamWriter(temp.getOutputStream()));
                    writer.write(action + ";" + message);
                    writer.newLine();
                    writer.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            Socket temp = Server.user.get(username);
            BufferedWriter writer = null;
            try {
                writer = new BufferedWriter(new OutputStreamWriter(temp.getOutputStream()));
                writer.write(username + ";" + action + ";" + message);
                writer.newLine();
                writer.flush();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}


