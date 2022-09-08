package com.server.dtos;

import java.io.ObjectOutputStream;

public class Subscriber {

    private ObjectOutputStream objectOutputStream;

    public ObjectOutputStream getObjectOutputStream() {
        return objectOutputStream;
    }

    public void setObjectOutputStream(ObjectOutputStream objectOutputStream) {
        this.objectOutputStream = objectOutputStream;
    }
}
