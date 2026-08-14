package com.prajwal.rtdspe.broker;

public class BrokerServer {
    private final Broker broker = new Broker();

    public void run() {
        broker.start();
    }
}
