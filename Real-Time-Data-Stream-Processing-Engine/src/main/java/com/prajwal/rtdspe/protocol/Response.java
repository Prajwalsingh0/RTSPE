package com.prajwal.rtdspe.protocol;

public class Response {
    private final boolean ok;

    public Response(boolean ok) {
        this.ok = ok;
    }

    public boolean isOk() {
        return ok;
    }
}
