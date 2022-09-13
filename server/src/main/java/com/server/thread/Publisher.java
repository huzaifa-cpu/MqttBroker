package com.server.thread;

import com.server.BootStrap;
import com.server.dtos.Message;
import com.server.dtos.Subscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

public class Publisher implements Callable<String> {

    private final static Logger logger = LoggerFactory.getLogger(Publisher.class.getName());

    public String call() throws Exception {

        try {
            logger.info("*** Publisher thread started");
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Message message = BootStrap.publishMessageQueue.take();  // take() : A blocking method
                    String topic = message.getTopic();
                    Set<Subscriber> subscribers = BootStrap.topicSubscriberRegistry.get(topic);
                    logger.info("*** Number of subscribers : " + subscribers.size());
                    for (Subscriber subscriber : subscribers) {
                        Map<String, Object> messageMap = new HashMap();
                        messageMap.put("topic", message.getTopic());
                        messageMap.put("message", message.getMessage());
                        Object messageObject = messageMap;
                        subscriber.getObjectOutputStream().writeObject(messageObject);
                        subscriber.getObjectOutputStream().flush();
                        logger.info("*** Message sent to client on port : " + subscriber.getPortNo());
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
