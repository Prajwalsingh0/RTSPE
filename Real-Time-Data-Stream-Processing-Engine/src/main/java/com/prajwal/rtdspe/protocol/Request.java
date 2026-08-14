package com.prajwal.rtdspe.protocol;

public class Request {
    private final MessageType type;

    public Request(MessageType type) {
        this.type = type;
    }

    public MessageType getType() {
        return type;
    }
}
