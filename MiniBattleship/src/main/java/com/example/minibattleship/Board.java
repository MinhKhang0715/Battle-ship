package com.example.minibattleship;

import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.io.IOException;

public class Board extends Parent {

    private VBox rows = new VBox();
//    is this board an enemy's board, yours if false
    private boolean enemy = false;
    //remaining ships, game over when falls to 0
    private int remaining = 7;
//to store x,y coordinates
    private int a, b;
//    for placing ships
    public static int prepCount = 7;
//    length of the board
    private int size = 6;

    public String enemyCoor = "";
    public String shootCoor = "";
//each board has 1 VBox containing 10 HBox(es), each HBox has a Cell
    public Board(boolean enemy) {
        this.enemy = enemy;
        //your board
        if (!enemy) {
            for (int y = 0; y < size; y++) {
                HBox row = new HBox();
                for (int x = 0; x < size; x++) {
                    Cell c = new Cell(x, y, true);
                    c.setOnMouseClicked(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent mouseEvent) {
//                            phase for placing ships
                            if (Client.phase.equals("prep") && prepCount > 0) {
                                a = c.x;
                                b = c.y;
                                if (place(a, b)) {
                                    prepCount--;
                                    enemyCoor += Integer.toString(a) + Integer.toString(b) + ",";
                                    if (prepCount == 0) {

                                        enemyCoor = enemyCoor.substring(0, enemyCoor.length() - 1);
                                        Client.phase = "battle";
                                        System.out.println("Changing phase: " + Client.phase);
                                        System.out.println("Sent Coordinates: " + enemyCoor);

                                    }
                                } else {
                                    Alert alertPrep = new Alert(Alert.AlertType.INFORMATION);
                                    alertPrep.setTitle("Can't place C man.");
                                    alertPrep.setHeaderText("Results: Unable to deploy due to space being occuPIED.");
                                    alertPrep.setContentText("Please select another spot dumbass!!!");
                                    alertPrep.showAndWait();
                                }
//                                System.out.println(a);
//                                System.out.println(b);
//                                System.out.println(coor);


//                                phase for shooting
                            } else if (Client.phase.equals("battle")) {
                                Alert alertPrep = new Alert(Alert.AlertType.INFORMATION);
                                alertPrep.setTitle("Can't place C man.");
                                alertPrep.setHeaderText("Results: Unable to deploy due to all ships already deployed.");
                                alertPrep.setContentText("Please do NOT touch this grid any longer.");
                                alertPrep.showAndWait();
                            }
                        }
                    });
                    row.getChildren().add(c);
                }

                rows.getChildren().add(row);
            }
            getChildren().add(rows);
            //enemy's board
        } else {
            for (int y = 0; y < size; y++) {
                HBox row = new HBox();
                for (int x = 0; x < size; x++) {
                    Cell c = new Cell(x, y);
                    row.getChildren().add(c);
                    c.setOnMouseClicked(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent mouseEvent) {
                            if (Client.phase.equals("prep")) {

                                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setTitle("Can't shoot C man.");
                                alert.setHeaderText("Results: Unable to massacre your opponent due to not being your turn.");
                                alert.setContentText("AKA Place all your ships first dumbass!!!");
                                alert.showAndWait();
                            } else if (Client.phase.equals("battle")) {
                                a = c.x;
                                b = c.y;
//                                **************Shooting phase here???*****************
//                                if (Client.turn == Client.order) {
//                                    if (shoot(a, b) == 1) {
//                                        shootCoor += Integer.toString(a) + Integer.toString(b) + ",";
//                                    } else if (shoot(a, b) == 2) {
//                                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
//                                        alert.setTitle("Can't shoot C man.");
//                                        alert.setHeaderText("Results: Unable to massacre your opponent cause yew shot hear all ready.");
//                                        alert.setContentText("AKA Shoot somewhere else!!!");
//                                        alert.showAndWait();
//                                    } else if (shoot(a, b) == 0) {
//                                        shootCoor += Integer.toString(a) + Integer.toString(b) + ",";
//                                        shootCoor = shootCoor.substring(0, shootCoor.length() - 1);
//                                        System.out.println("Sent Coordinates for Shot: " + shootCoor);
//                                        Client.sendMessage(Client.rec, "shoot", shootCoor);
//                                        if (Client.turn == 1) {
//                                            Client.turn++;
//                                        } else {
//                                            Client.turn--;
//                                        }
//
//                                    }
//                                    try {
//                                        String response = Client.in.readLine();
//                                        String[] res = response.split(";");
//                                        getShot(res[2]);
//                                    } catch (IOException e) {
//                                        throw new RuntimeException(e);
//                                    }
//                                    if (remaining == 0) {
//                                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
//                                        alert.setTitle("Game Ovah");
//                                        alert.setHeaderText("Results: You Win?");
//                                        alert.setContentText("KAKAAKAKAKKAKAAK");
//                                        alert.showAndWait();
//                                    }
//
//                                }
//                                else {
//                                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
//                                    alert.setTitle("Can't shoot C man.");
//                                    alert.setHeaderText("Results: Unable to massacre your opponent due to not being your turn.");
//                                    alert.setContentText("Wait for your opponent to pew pew!");
//                                    alert.showAndWait();
//                                }

//
                            }

                        }
                    });
                }

                rows.getChildren().add(row);
            }
            getChildren().add(rows);
        }


    }

//place your ships
    public boolean place(int x, int y) {
        if (valid(x, y)) {
            Cell cell = getCell(x, y);
            if (!cell.containShip) {
                cell.setFill(Color.YELLOW);
                cell.containShip = true;
                return true;
            } else {
                return false;
            }

        } else {
            return false;
        }

    }

//place enemy ships (will not color the cell brown in the final build), the brown color is to test if it's working properly
    public boolean placeEnemy(int x, int y) {
        if (valid(x, y)) {
            Cell cell = getCell(x, y);
            if (!cell.containShip) {
                cell.setFill(Color.BROWN);
                cell.containShip = true;
                return true;
            } else {
                return false;
            }

        } else {
            return false;
        }

    }


//    place all enemy ship using a message sent from the server, example of a message: 34,12,44,56,13,25,66             **
    public void getEnemyPlacement(String massage) {
        String[] element = massage.split(",");
        for (int i = 0; i < element.length; i++) {
            placeEnemy(Integer.parseInt(String.valueOf(element[i].charAt(0))), Integer.parseInt(String.valueOf(element[i].charAt(1))));
        }
    }

    //Get the fucking shot to print on board???
    public void getShot(String massage) {
        String[] element = massage.split(",");
        for (int i = 0; i < element.length; i++) {
            placeShot(Integer.parseInt(String.valueOf(element[i].charAt(0))), Integer.parseInt(String.valueOf(element[i].charAt(1))));

        }

    }
//not done yet, doesn't work
    public void placeShot(int x, int y) {
        if (valid(x, y)) {
            Cell cell = getCell(x, y);
            if (!cell.containShip) {
                cell.setFill(Color.RED);
                return;
            } else {
                cell.setFill(Color.GREEN);
                return;
            }

        } else {
            return;
        }

    }

//    public int shoot(int x, int y) {
//        Cell cell = getCell(x, y);
//        if (cell.wasShot) {
////            System.out.println("X: " + cell.x+"   "+ "Y: " + cell.y);
//
//            return 2;
//        }
//        cell.setFill(Color.RED);
//        cell.wasShot = true;
//        if (cell.containShip) {
//            cell.setFill(Color.GREEN);
//            remaining--;
//            return 1;
//
//        }
//        return 0;
//    }

//check to see if a certain point/cell is valid to work with
    public boolean valid(int x, int y) {
        return x >= 0 && x < size && y >= 0 && y < size;
    }


//    get a certain Cell(each cell already contains its own x and y coordinates)
    public Cell getCell(int x, int y) {
        return (Cell) ((HBox) rows.getChildren().get(y)).getChildren().get(x);
    }


}
