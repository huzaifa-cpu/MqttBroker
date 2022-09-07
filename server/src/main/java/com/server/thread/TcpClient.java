package com.server.thread;

import com.server.BootStrap;
import com.server.dtos.MessageDto;

import java.io.*;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.Callable;

public class TcpClient implements Callable<String> {

    protected Socket clientSocket;

    public TcpClient(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    public String call() {
        try {
            // output object
            OutputStream os = clientSocket.getOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(os);

            // input object
            InputStream is = clientSocket.getInputStream();
            ObjectInputStream ois = new ObjectInputStream(is);

            // initialize
            Object inputObject = null;

            while(!Thread.interrupted()) {
                while ((inputObject = ois.readObject()) != null) {

                    Map outputMap = new HashMap();
                    Map inputMap = (Map) inputObject;
                    String packet = (String) inputMap.get("packet");
                    String topic = (String) inputMap.get("topic");
                    String message = (String) inputMap.get("message");
                    String port = (String) inputMap.get("port");

                    System.out.println("Client: " + inputMap);

                    if (packet.equals("CONNECT")) {
                        outputMap.put("packet", "CONNACK");
                        outputMap.put("returnCode", true);
                        Object object = outputMap;
                        oos.writeObject(object);
                        System.out.println("Connection established with client port : " +port);
                    }
                    if (packet.equals("SUBSCRIBE")) {
                        if (BootStrap.topicSubscriberRegistry.containsKey(topic)) {
                            Set<ObjectOutputStream> existingClients = BootStrap.topicSubscriberRegistry.get(topic);
                            if (existingClients != null) {
                                existingClients.add(oos);
                                BootStrap.topicSubscriberRegistry.put(topic, existingClients);
                            }
                        } else {
                            Set<ObjectOutputStream> newClients = new HashSet<>();
                            newClients.add(oos);
                            BootStrap.topicSubscriberRegistry.put(topic, newClients);
                        }

                        outputMap.put("packet", "SUBACK");
                        outputMap.put("returnCode", true);
                        Object object = outputMap;
                        oos.writeObject(object);
                        System.out.println("Client subscribed : " + BootStrap.topicSubscriberRegistry);
                    }
                    if (packet.equals("PUBLISH")) {

                        // RESPONSE TO PUBLISHER
                        outputMap.put("packet", "PUBACK");
                        outputMap.put("returnCode", true);
                        Object object = outputMap ;
                        oos.writeObject(object);

                        // BROADCASTING
                        MessageDto messageDto = new MessageDto();
                        messageDto.setTopic(topic);
                        messageDto.setMessage(message);

                        BootStrap.publishMessageQueue.add(messageDto);
                        System.out.println("Message added in publish queue");


                    }
                    if (packet.equals("UNSUBSCRIBE")) {
                        outputMap.put("packet", "UNSUBACK");
                        outputMap.put("returnCode", true);
                        Object object = outputMap;
                        oos.writeObject(object);
                    }


                }
            }
            System.out.print("Client is closing");
        } catch (IOException e) {
            System.out.println("Exception caught when trying to listen on port:" + e.getMessage());
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return clientSocket.getInetAddress().getHostAddress();
    }
}