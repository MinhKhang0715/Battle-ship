package com.example.minibattleship.Client;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;

public class Client extends Application {

    Board boardEnemy, boardYou;
    private Stage stage;
    private final static String host = "localhost";
    private final static int port = 1234;

    private BufferedReader inputReader;
    private BufferedWriter outputWriter;
    private static Socket socket;
//    place = prep  -> battle -> end game
    public static String phase = "prep";
    public static TextField recipient;
//    my idea: it's your turn if turn == order. not sure if it works
    public static int turn = 1;

    public static int order = 1;

    public static String rec;

    public BufferedReader getInputReader() {
        return inputReader;
    }

    public BufferedWriter getOutputWriter() {
        return outputWriter;
    }

    //Create board to view on stage
    public Scene createBoard() {
        //root to contain everything
        HBox root = new HBox(100);
        //VBox contains the boards
        VBox container1 = new VBox(50);
        boardEnemy = new Board(true);
        boardYou = new Board(false);
        container1.getChildren().addAll(boardEnemy, boardYou);
        //receiver = recipient = enemy
        Label receiver = new Label("Receiver");
        recipient = new TextField();
        Button done = new Button("Done placing sea men.");
        done.setOnMouseClicked(mouseEvent -> {
            rec  = recipient.getText();
            System.out.println("Recipient: "+rec);
            if(Board.prepCount == 0){
                sendMessage(rec,"prep",boardYou.enemyCoor);
                System.out.println("Sending message...");
                try {
                    String response = inputReader.readLine();
                    System.out.println("Received: " + response);
                    String[] res = response.split(";");
                    boardEnemy.getEnemyPlacement(res[2]);
                    // Kieerm tra luot cua minh
//                        if((phase.equals("battle"))) {
//                            if (order == 2) {
////                             neu la luot cua minh, thi nhan phat ban cua doi thu
//                                in.readLine();
//                            }
//                                sendMessage(rec, "shoot", boardEnemy.shootCoor);
//
//                                in.readLine();
//
//                        }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            } else{
                Alert alertPrep = new Alert(Alert.AlertType.INFORMATION);
                alertPrep.setTitle("Not yet!");
                alertPrep.setHeaderText("Results: Unable to send cuz there you still have ships to deploy");
                alertPrep.setContentText("Deploy all 7 ships before continuing on!!!");
                alertPrep.showAndWait();
            }
        });
        VBox container2 = new VBox(50);
        container2.getChildren().addAll(receiver,recipient,done);
//        placeholder.getChildren().addAll(board3,board4);
        root.getChildren().addAll(container1, container2);
        root.setPrefSize(800, 800);
        return new Scene(root);
    }


    //login gives a username for the Server.user
    public Scene login() {
        VBox root = new VBox(10);
        HBox hbox = new HBox(30);
        Label label = new Label("User? Name it: ");
        TextField username = new TextField();
        hbox.getChildren().addAll(label, username);
        Button login = new Button("Get in??");
        System.out.println("B4 login");
        login.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                try {
                    String txtusername = username.getText();
                    System.out.println("Username which just logged in: " +txtusername);
                    if (!txtusername.equals("")) {
                        outputWriter.write(username.getText());
                        outputWriter.newLine();
                        outputWriter.flush();
                        String Order = inputReader.readLine();
                        order = Integer.parseInt(Order);
                        System.out.println("Your Order : ");
                        System.out.println("U R playa numero: " + order);
                        stage.setScene(createBoard());
                    } else {
                        Alert alertPrep = new Alert(Alert.AlertType.INFORMATION);
                        alertPrep.setTitle("No name?");
                        alertPrep.setHeaderText("Results: Unable to send cuz there ain't nothing to send");
                        alertPrep.setContentText("Write something in the textfield!!!");
                        alertPrep.showAndWait();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        Insets insets = new Insets(10, 10, 10, 10);
        login.setPadding(insets);
        login.setAlignment(Pos.CENTER);
        root.getChildren().addAll(hbox, login);
        return new Scene(root);

    }

    //send a message to a specified user, coor = coordinates
    public void sendMessage(String username, String action, String coor){
        try {
            String message = username + ";" + action + ";" + coor;
            System.out.println("Sending: "+message);
            outputWriter.write(message);
            outputWriter.newLine();
            outputWriter.flush();
            System.out.println("Sent");
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public void sendMessage(String action, String coor){
        try {
            String message = action + ";" + coor;
            System.out.println("Sending: "+message);
            outputWriter.write(action + ";" + coor);
            outputWriter.newLine();
            outputWriter.flush();
            System.out.println("Sent");

        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void receiveMessage(){}

    @Override
    public void start(Stage primaryStage) throws IOException {
        socket = new Socket(host, port);
        outputWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        inputReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        stage = primaryStage;
        stage.setX(100);
        stage.setY(100);
        Scene scene = login();
        primaryStage.setResizable(false);
        primaryStage.setTitle("The epic battles of the battling battleships.");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void stop(){
        try {
            System.out.println("Closing client");
            outputWriter.write("bye");
            outputWriter.newLine();
            outputWriter.flush();
            socket.close();
            inputReader.close();
            outputWriter.close();
        } catch (IOException e){
            System.out.println("Closing failed");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch();
    }
}
