package com.server.thread;

import com.server.BootStrap;
import com.server.dtos.Message;
import com.server.dtos.Subscriber;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

public class Publisher implements Callable<String> {

    public String call() throws Exception {

        try {
            System.out.println("*** Publisher thread started");
            while (!Thread.currentThread().isInterrupted()) {
                Message message = BootStrap.publishMessageQueue.take();  // take() : A blocking method
                String topic = message.getTopic();
                Set<Subscriber> subscribers = BootStrap.topicSubscriberRegistry.get(topic);
                System.out.println("*** Number of subscribers : " + subscribers.size());
                for (Subscriber subscriber : subscribers) {
                    Map<String, Object> messageMap = new HashMap();
                    messageMap.put("topic", message.getTopic());
                    messageMap.put("message", message.getMessage());
                    Object messageObject = messageMap;
                    try {
                        subscriber.getObjectOutputStream().writeObject(messageObject);
                        subscriber.getObjectOutputStream().flush();
                        System.out.println("*** Message sent to client on port : " + subscriber.getPortNo());
                    } catch (Exception ex){
                        ex.printStackTrace();
                    }
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
