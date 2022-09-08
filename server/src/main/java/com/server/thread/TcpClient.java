package com.server.thread;

import com.server.BootStrap;
import com.server.dtos.Message;
import com.server.dtos.Subscriber;
import com.server.enums.PacketType;

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
            OutputStream outputStream = clientSocket.getOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);

            // input object
            InputStream inputStream = clientSocket.getInputStream();
            ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);

            // initialize
            Object inputObject = null;

            while(!Thread.interrupted()) {
                while ((inputObject = objectInputStream.readObject()) != null) {

                    Map<String, Object> outputMap = new HashMap();
                    Map inputMap = (Map) inputObject;
                    String packetValue = (String) inputMap.get("packet");
                    String topic = (String) inputMap.get("topic");
                    String message = (String) inputMap.get("message");
                    String port = (String) inputMap.get("port");

                    PacketType packet = PacketType.findByValue(packetValue);

                    System.out.println("Client: " + inputMap);

                    if (PacketType.CONNECT.equals(packet)) {
                        outputMap.put("packet", PacketType.CONNACK.getValue());
                        outputMap.put("returnCode", true);
                        Object object = outputMap;
                        objectOutputStream.writeObject(object);
                        System.out.println("Connection established with client port : " +port);
                    }
                    if (PacketType.SUBSCRIBE.equals(packet)) {
                        Subscriber newSubscriber = new Subscriber();
                        newSubscriber.setObjectOutputStream(objectOutputStream);
                        if (BootStrap.topicSubscriberRegistry.containsKey(topic)) {
                            Set<Subscriber> existingSubscribers = BootStrap.topicSubscriberRegistry.get(topic);
                            if (existingSubscribers != null) {
                                existingSubscribers.add(newSubscriber);
                                BootStrap.topicSubscriberRegistry.put(topic, existingSubscribers);
                            }
                        } else {
                            Set<Subscriber> newSubscribers = new HashSet<>();
                            newSubscribers.add(newSubscriber);
                            BootStrap.topicSubscriberRegistry.put(topic, newSubscribers);
                        }

                        outputMap.put("packet", PacketType.SUBACK.getValue());
                        outputMap.put("returnCode", true);
                        Object object = outputMap;
                        objectOutputStream.writeObject(object);
                        System.out.println("Client subscribed : " + BootStrap.topicSubscriberRegistry);
                    }
                    if (PacketType.PUBLISH.equals(packet)) {
                        // RESPONSE TO PUBLISHER
                        outputMap.put("packet", PacketType.PUBACK.getValue());
                        outputMap.put("returnCode", true);
                        Object object = outputMap ;
                        objectOutputStream.writeObject(object);

                        // BROADCASTING
                        Message messageDto = new Message();
                        messageDto.setTopic(topic);
                        messageDto.setMessage(message);

                        BootStrap.publishMessageQueue.add(messageDto);
                        System.out.println("Message added in publish queue");
                    }
                    if (PacketType.UNSUBSCRIBE.equals(packet)) {
                        outputMap.put("packet", PacketType.UNSUBACK.getValue());
                        outputMap.put("returnCode", true);
                        Object object = outputMap;
                        objectOutputStream.writeObject(object);
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