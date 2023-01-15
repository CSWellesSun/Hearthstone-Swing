package com.welles;

import java.net.Socket;

public class Session {
    private Socket client1;
    private Socket client2;

    public Session(Socket client1, Socket client2) {
        this.client1 = client1;
        this.client2 = client2;
    }

    public Socket GetClient1() {
        return client1;
    }

    public Socket GetClient2() {
        return client2;
    }
}
