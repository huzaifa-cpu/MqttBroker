package com.server.dtos;

import java.io.ObjectOutputStream;

public class Subscriber {

    private String portNo;
    private ObjectOutputStream objectOutputStream;

    public ObjectOutputStream getObjectOutputStream() {
        return objectOutputStream;
    }

    public void setObjectOutputStream(ObjectOutputStream objectOutputStream) {
        this.objectOutputStream = objectOutputStream;
    }

    public String getPortNo() {
        return portNo;
    }

    public void setPortNo(String portNo) {
        this.portNo = portNo;
    }
}
