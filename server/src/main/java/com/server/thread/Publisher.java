package com.server.thread;

import com.server.BootStrap;
import com.server.dtos.MessageDto;

import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

public class Publisher implements Callable<String> {

    public String call() throws Exception {

        try {
            System.out.println("Publisher thread started");

            while (!Thread.currentThread().isInterrupted()) {
                MessageDto messageDto = BootStrap.publishMessageQueue.take();
                String topic = messageDto.getTopic();

                if (BootStrap.topicSubscriberRegistry.containsKey(topic)) {
                    Set<ObjectOutputStream> clients = BootStrap.topicSubscriberRegistry.get(topic);
                    System.out.println("Number of subscribers : " + clients.size());
                    for (ObjectOutputStream client : clients) {
                        Map messageMap = new HashMap();
                        messageMap.put("topic", messageDto.getTopic());
                        messageMap.put("message", messageDto.getMessage());
                        Object messageObject = messageMap;
                        try {
                            client.writeObject(messageObject);
                            client.flush();
                            System.out.println("Message sent to client " + client);
                        }catch (Exception ex){
                           ex.printStackTrace();
                        }

                    }
                }
            }
        }
        catch (Exception e){}
        return null;
    }
}
