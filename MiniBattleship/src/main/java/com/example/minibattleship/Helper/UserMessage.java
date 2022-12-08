package com.example.minibattleship.Helper;

import java.io.Serializable;

public class UserMessage implements Serializable {
    private String username;
    private String gameState;
    private String message;
    private boolean isGoFirst;

    public UserMessage(String name, String state, String msg) {
        username = name;
        gameState = state;
        message = msg;
    }

    public UserMessage() {
        username = "";
        gameState = "";
        message = "";
    }

    public String getUsername() { return username; }

    public String getGameState() { return gameState; }

    public String getMessage() { return message; }

    public boolean getIsGoFirst() { return isGoFirst; }

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

    public void setUsernameNoReturn(String username) { this.username = username; }

    public void setGameStateNoreturn(String gameState) { this.gameState = gameState; }

    public void setMessageNoReturn(String message) { this.message = message; }
}
