package com.client.threads;

import com.client.ClientApplication;
import com.client.configs.ServerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;

public class Reader extends Thread {

    private final static Logger logger = LoggerFactory.getLogger(Reader.class.getName());

    private Socket clientSocket;
    private ClientApplication client;

    public Reader(Socket socket, ClientApplication client) {
        this.clientSocket = socket;
        this.client = client;
    }

    public void run() {
        try{
            InputStream is = clientSocket.getInputStream();
            ObjectInputStream ois = new ObjectInputStream(is);
            Object inputObject = null;

            while(!Thread.interrupted()) {
                while ((inputObject = ois.readObject()) != null) {
                    // FROM SERVER
                    Map inputMap = (Map) inputObject;

                    if (inputMap.containsKey("returnCode")){
                        Boolean returnCode = (Boolean) inputMap.get("returnCode");
                        if(returnCode != null){
                            System.out.println("Server: " + inputMap);
                        }
                    }
                    if (inputMap.containsKey("topic") && inputMap.containsKey("message")) {
                        String message = (String) inputMap.get("message");
                        String topic = (String) inputMap.get("topic");
                        if(!topic.isEmpty() && !message.isEmpty()){
                            System.out.println("Message from publisher: " + inputMap);
                            logger.info("Message receive from publisher");
                        }
                    }
                }
            }
        } catch (UnknownHostException e) {
            logger.error("*** Unknown Host Exception " + ServerConfig.getHost());
            System.exit(1);
        } catch (IOException e) {
            logger.error("*** Couldn't get I/O for the host " + ServerConfig.getHost());
            System.exit(1);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
