package com.client.threads;

import com.client.ClientApplication;
import com.client.configs.ServerConfig;
import com.client.enums.PacketType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;

public class Writer extends Thread {

    private final static Logger logger = LoggerFactory.getLogger(Writer.class.getName());

    private Socket clientSocket;
    private ClientApplication client;


    public Writer(Socket socket, ClientApplication client) {
        this.clientSocket = socket;
        this.client = client;
    }

    public void run() {
        try{
            OutputStream os = clientSocket.getOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(os);
            Scanner scanner = new Scanner(System.in);

            // Connect request
            Map<String, Object> connectMap = new HashMap<>();
            connectMap.put("port", String.valueOf(clientSocket.getLocalPort()));
            connectMap.put("packet", PacketType.CONNECT.getValue());
            Object connectObject = connectMap;
            oos.writeObject(connectObject);

            // Other requests
            while(!Thread.interrupted()) {
                try {
                    String packetType = scanner.nextLine();
                    if (!packetType.isEmpty()) {
                        packetType = packetType.trim().replaceAll(" ","").toLowerCase();
                        PacketType packet = PacketType.findByValue(packetType);

                        Map<String, String> outputMap = new HashMap<>();
                        outputMap.put("port", String.valueOf(clientSocket.getLocalPort()));

                        if (packet != null && packet.equals(PacketType.SUBSCRIBE)) {
                            System.out.print("Enter topic: ");
                            String topic = scanner.nextLine();

                            outputMap.put("packet", PacketType.SUBSCRIBE.getValue());
                            outputMap.put("topic", topic);
                            Object object = outputMap;
                            oos.writeObject(object);
                        }
                        else if (packet != null && packet.equals(PacketType.PUBLISH)) {
                            System.out.print("Enter topic: ");
                            String topic = scanner.nextLine();
                            System.out.print("Enter message: ");
                            String message = scanner.nextLine();

                            outputMap.put("packet", PacketType.PUBLISH.getValue());
                            outputMap.put("topic", topic);
                            outputMap.put("message", message);
                            Object object = outputMap;
                            oos.writeObject(object);
                        }
                        else if (packet != null && packet.equals(PacketType.UNSUBSCRIBE)) {
                            System.out.print("Enter topic: ");
                            String topic = scanner.nextLine();

                            outputMap.put("packet", PacketType.UNSUBSCRIBE.getValue());
                            outputMap.put("topic", topic);
                            Object object = outputMap;
                            oos.writeObject(object);
                        }
                        else {
                            logger.info("*** Incorrect packet or spelling mistake");
                        }
                    }
                } catch (Exception e) {
                    logger.error("Exception caught in Writer thread");
                    e.printStackTrace();
                }
            }
        } catch (UnknownHostException e) {
            logger.error("*** Unknown Host Exception " + ServerConfig.getHost());
            System.exit(1);
        } catch (IOException e) {
            logger.error("*** Couldn't get I/O for the host " + ServerConfig.getHost());
            System.exit(1);
        }
    }
}
