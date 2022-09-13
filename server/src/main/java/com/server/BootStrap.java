package com.server;

import com.server.configs.ServerConfig;
import com.server.dtos.Message;
import com.server.dtos.Subscriber;
import com.server.thread.Publisher;
import com.server.thread.TcpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.*;

@Component
public class BootStrap {

    private final static Logger logger = LoggerFactory.getLogger(BootStrap.class.getName());

    public static final ConcurrentMap<String, Set<Subscriber>> topicSubscriberRegistry = new ConcurrentHashMap<>();

    public static final LinkedBlockingQueue<Message> publishMessageQueue = new LinkedBlockingQueue<>();

    public static void startMqttServer() {
        logger.info("*** Server Started");
        try {
            ExecutorService publishQueueExecutor = Executors.newFixedThreadPool(10);
            publishQueueExecutor.submit(new Publisher());

            ExecutorService tcpServerExecutor = Executors.newFixedThreadPool(10);
            ServerSocket serverSocket = new ServerSocket(ServerConfig.getPort());

            while (!Thread.currentThread().isInterrupted()) {
                Socket socket = serverSocket.accept();
                tcpServerExecutor.submit(new TcpClient(socket));
            }

            // review the below code
            if(Thread.interrupted()){
                tcpServerExecutor.shutdown();
                publishQueueExecutor.shutdown();
            }
            if(!tcpServerExecutor.isTerminated()){
                tcpServerExecutor.awaitTermination(10000, TimeUnit.MILLISECONDS);
                tcpServerExecutor.shutdownNow();
            }
            if(!publishQueueExecutor.isTerminated()){
                publishQueueExecutor.awaitTermination(10000, TimeUnit.MILLISECONDS);
                publishQueueExecutor.shutdownNow();
            }
        }

        catch (IOException e){
            logger.error("Exception in Bootstrap", e);
            System.exit(-1);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}

