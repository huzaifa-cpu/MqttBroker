package com.client.threads;

import com.client.ClientApplication;
import com.client.configs.ServerConfig;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;

public class Reader extends Thread {

    private Socket clientSocket;
    private ClientApplication client;

    private Object inputObject = null;
    private ObjectInputStream ois;

    public Reader(Socket socket, ClientApplication client) {
        this.clientSocket = socket;
        this.client = client;

        try {
            InputStream is = clientSocket.getInputStream();
            ois = new ObjectInputStream(is);

        } catch (IOException ex) {
            System.out.println("Error getting input stream: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void run() {

        try{
            while(!Thread.interrupted()) {
                while ((inputObject = ois.readObject()) != null) {

                    // from server
                    Map inputMap = (Map) inputObject;
                    Boolean returnCode = (Boolean) inputMap.get("returnCode");
                    System.out.println("Server: " + inputMap);
//                    if (returnCode.equals(true) && inputMap.containsKey("message")) {
                    if (inputMap.containsKey("message")) {
                        String message = (String) inputMap.get("message");
                        String topic = (String) inputMap.get("topic");
                        System.out.println("message from client: " + message);
                        System.out.println("topic from client: " + topic);
                    }

                    // to server

                }
            }
        }
        catch (UnknownHostException e) {
            System.err.println("Don't know about host " + ServerConfig.getHost());
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " + ServerConfig.getHost());
            System.exit(1);
        }
        catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }
}
