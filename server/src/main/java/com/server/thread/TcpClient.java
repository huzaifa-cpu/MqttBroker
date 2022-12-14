package com.server.thread;

import com.server.BootStrap;
import com.server.dtos.Message;
import com.server.dtos.Subscriber;
import com.server.enums.PacketType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.Callable;

public class TcpClient implements Callable<String> {

    private final static Logger logger = LoggerFactory.getLogger(TcpClient.class.getName());

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
                    try {
                        Map inputMap = (Map) inputObject;
                        String packetValue = (String) inputMap.get("packet");
                        if(packetValue != null && !packetValue.isEmpty()){
                            packetValue = packetValue.trim();
                            PacketType packet = PacketType.findByValue(packetValue);
                            String topic = (String) inputMap.get("topic");
                            String message = (String) inputMap.get("message");
                            String port = (String) inputMap.get("port");

                            System.out.println("Client: " + inputMap);
                            Map<String, Object> outputMap = new HashMap();

                            if(packet != null && packet.equals(PacketType.CONNECT)) {
                                outputMap.put("packet", PacketType.CONNACK.getValue());
                                outputMap.put("returnCode", true);
                                Object object = outputMap;
                                objectOutputStream.writeObject(object);
                                logger.info("*** Connection established with client on port : " +port);
                            }
                            if(packet != null && packet.equals(PacketType.SUBSCRIBE)) {
                                Subscriber newSubscriber = new Subscriber();
                                newSubscriber.setObjectOutputStream(objectOutputStream);
                                newSubscriber.setPortNo(port);
                                if (BootStrap.topicSubscriberRegistry.containsKey(topic)) {
                                    Set<Subscriber> existingSubscribers = BootStrap.topicSubscriberRegistry.get(topic);
                                    if (existingSubscribers != null) {
                                        existingSubscribers.add(newSubscriber);
                                        BootStrap.topicSubscriberRegistry.put(topic, existingSubscribers);
                                    } // else case is never reached
                                } else {
                                    Set<Subscriber> newSubscribers = new HashSet<>();
                                    newSubscribers.add(newSubscriber);
                                    BootStrap.topicSubscriberRegistry.put(topic, newSubscribers);
                                }

                                outputMap.put("packet", PacketType.SUBACK.getValue());
                                outputMap.put("returnCode", true);
                                Object object = outputMap;
                                objectOutputStream.writeObject(object);
                                logger.info("*** Client subscribed : " + BootStrap.topicSubscriberRegistry);
                            }
                            if(packet != null && packet.equals(PacketType.PUBLISH)) {
                                if(BootStrap.topicSubscriberRegistry.containsKey(topic)){
                                    Set<Subscriber> subscribers = BootStrap.topicSubscriberRegistry.get(topic);
                                    if(!subscribers.isEmpty()){
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

                                        logger.info("*** Message added in publish queue");
                                    } else{
                                        // A message is always discarded whose topic exists but have no subscribers
                                        // Example : whatsapp group
                                        logger.info("*** Topic exists but have no subscribers");
                                        outputMap.put("message", "Topic exists but have no subscribers");
                                        outputMap.put("returnCode", false);
                                        Object object = outputMap;
                                        objectOutputStream.writeObject(object);
                                    }
                                } else{
                                    // A message is always discarded whose topic does not exist
                                    logger.info("*** Topic does not exists");
                                    outputMap.put("message", "Topic does not exists");
                                    outputMap.put("returnCode", false);
                                    Object object = outputMap;
                                    objectOutputStream.writeObject(object);
                                }
                            }
                            if(packet != null && packet.equals(PacketType.UNSUBSCRIBE)) {
                                if(BootStrap.topicSubscriberRegistry.containsKey(topic)){
                                    Set<Subscriber> subscribers = BootStrap.topicSubscriberRegistry.get(topic);
                                    if(!subscribers.isEmpty()){
                                        Iterator<Subscriber> itr = subscribers.iterator();
                                        while(itr.hasNext()){
                                            Subscriber subscriber = itr.next();
                                            if(port.equals(subscriber.getPortNo())){
                                                itr.remove();
                                            }
                                        }
                                        BootStrap.topicSubscriberRegistry.put(topic, subscribers);
                                        // response to unsubscriber
                                        outputMap.put("packet", PacketType.UNSUBACK.getValue());
                                        outputMap.put("returnCode", true);
                                        Object object = outputMap;
                                        objectOutputStream.writeObject(object);
                                    } else{
                                        logger.info("*** Topic has no subscribers");
                                        outputMap.put("message", "Topic has no subscribers");
                                        outputMap.put("returnCode", false);
                                        Object object = outputMap;
                                        objectOutputStream.writeObject(object);
                                    }

                                } else{
                                    logger.info("*** Topic does not exists");
                                    outputMap.put("message", "Topic does not exists");
                                    outputMap.put("returnCode", false);
                                    Object object = outputMap;
                                    objectOutputStream.writeObject(object);
                                }
                            }
                        }
                    } catch (Exception e){
                        logger.error("Exception caught in TCP client");
                        e.printStackTrace();
                    }
                }
                Thread.currentThread().interrupt();
            }
            logger.info("*** Client is closing");

        } catch (IOException e) {
            logger.error("*** Exception when trying to listen on port:" + e.getMessage());
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return clientSocket.getInetAddress().getHostAddress();
    }
}