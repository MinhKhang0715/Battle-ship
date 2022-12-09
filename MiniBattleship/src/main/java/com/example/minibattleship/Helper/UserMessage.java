package com.example.minibattleship.Helper;

import java.io.Serializable;

public class UserMessage implements Serializable {
    private int id;
    private String username;
    private String gameState;
    private String message;
    private boolean isGoFirst;

    public UserMessage(int id, String name, String state, String msg) {
        this.id = id;
        username = name;
        gameState = state;
        message = msg;
    }

    public UserMessage() {
        id = 0;
        username = "";
        gameState = "";
        message = "";
    }

    public String getUsername() { return username; }

    public String getGameState() { return gameState; }

    public String getMessage() { return message; }

    public boolean getIsGoFirst() { return isGoFirst; }

    public int getId() {return id;}

    public UserMessage setId(int id) {
        this.id = id;
        return this;
    }

    public UserMessage setUsername(String username) {
        this.username = username;
        return this;
    }

    public UserMessage setGameState(String gameState) {
        this.gameState = gameState;
        return this;
    }

    public UserMessage setMessage(String message) {
        this.message = message;
        return this;
    }

    public UserMessage setIsGoFirst(boolean isGoFirst) {
        this.isGoFirst = isGoFirst;
        return this;
    }
}
