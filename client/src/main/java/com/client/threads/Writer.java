package com.client.threads;

import com.client.ClientApplication;
import com.client.configs.ServerConfig;
import com.client.enums.PacketType;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;

public class Writer extends Thread {

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

                String packetType = scanner.nextLine();
                if (!packetType.isEmpty()) {
                    packetType = packetType.trim().replaceAll(" ","");
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
                    if (packet != null && packet.equals(PacketType.PUBLISH)) {
                        List<String> printData = new ArrayList<>();
                        printData.add("Topic");
                        printData.add("Message");
                        List<String> data = new ArrayList<>();
                        for (int i = 0; i < 2; i++) {
                            System.out.print("Enter " + printData.get(i) + ": ");
                            data.add(scanner.nextLine());
                        }
                        outputMap.put("packet", PacketType.PUBLISH.getValue());
                        outputMap.put("topic", data.get(0));
                        outputMap.put("message", data.get(1));
                        Object object = outputMap;
                        oos.writeObject(object);
                    }
                    if (packet != null && packet.equals(PacketType.UNSUBSCRIBE)) {
                        System.out.print("Enter topic: ");
                        String topic = scanner.nextLine();

                        outputMap.put("packet", PacketType.UNSUBSCRIBE.getValue());
                        outputMap.put("topic", topic);
                        Object object = outputMap;
                        oos.writeObject(object);
                    }
                }
            }
        } catch (UnknownHostException e) {
            System.err.println("*** Unknown Host Exception " + ServerConfig.getHost());
            System.exit(1);
        } catch (IOException e) {
            System.err.println("*** Couldn't get I/O for the host " + ServerConfig.getHost());
            System.exit(1);
        }
    }
}
