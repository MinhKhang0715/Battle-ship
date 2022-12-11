package com.example.minibattleship.Helpers;

import java.io.Serializable;

public class UserMessage implements Serializable {
    private int id;
    private String username;
    private String gameState;
    private String message;
    private boolean isGoFirst;
    private MessageType messageType;
    private boolean isAbandonGame = false;

    public UserMessage(int id, String name, String state, String msg, MessageType messageType) {
        this.id = id;
        this.username = name;
        this.gameState = state;
        this.message = msg;
        this.messageType = messageType;
    }

    public UserMessage() {
        id = 0;
        username = "";
        gameState = "";
        message = "";
    }

    public boolean isAbandonGame() {
        return isAbandonGame;
    }

    public UserMessage setAbandonGame(boolean abandonGame) {
        isAbandonGame = abandonGame;
        return this;
    }

    public MessageType getMessageType() {
        return messageType;
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

    public UserMessage setMessageType(MessageType type) {
        this.messageType = type;
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
