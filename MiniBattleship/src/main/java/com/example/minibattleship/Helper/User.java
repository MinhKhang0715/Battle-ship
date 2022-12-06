package com.example.minibattleship.Helper;

import java.io.Serializable;

public class User implements Serializable {
    private String username;
    private String gameState;
    private String message;
    private boolean isGoFirst;

    public User(String name, String state, String msg) {
        username = name;
        gameState = state;
        message = msg;
    }

    public User() {
        username = "";
        gameState = "";
        message = "";
    }

    public String getUsername() { return username; }

    public String getGameState() { return gameState; }

    public String getMessage() { return message; }

    public boolean getIsGoFirst() { return isGoFirst; }

    public User setUsername(String username) {
        this.username = username;
        return this;
    }

    public User setGameState(String gameState) {
        this.gameState = gameState;
        return this;
    }

    public User setMessage(String message) {
        this.message = message;
        return this;
    }

    public User setIsGoFirst(boolean isGoFirst) {
        this.isGoFirst = isGoFirst;
        return this;
    }

    public void setUsernameNoReturn(String username) { this.username = username; }

    public void setGameStateNoreturn(String gameState) { this.gameState = gameState; }

    public void setMessageNoReturn(String message) { this.message = message; }
}
