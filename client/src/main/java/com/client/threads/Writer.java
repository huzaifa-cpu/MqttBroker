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

    private OutputStream os;
    private ObjectOutputStream oos;
    BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
    String fromUser;

    public Writer(Socket socket, ClientApplication client) {
        this.clientSocket = socket;
        this.client = client;

        try {
            os = clientSocket.getOutputStream();
            oos = new ObjectOutputStream(os);
        } catch (IOException ex) {
            System.out.println("Error getting output stream: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void run() {

        try{
            // CONNECT request
            Map<String, Object> connectMap = new HashMap<>();
            connectMap.put("port", String.valueOf(clientSocket.getLocalPort()));
            connectMap.put("packet", "CONNECT");
            Object connectObject = connectMap;
            oos.writeObject(connectObject);

            while(!Thread.interrupted()) {

                fromUser = stdIn.readLine();
                if (fromUser != null) {
                        Map<String, String> map = new HashMap<>();
                        map.put("port", String.valueOf(clientSocket.getLocalPort()));
                        connectMap.put("port", String.valueOf(clientSocket.getLocalPort()));

                        if (fromUser.equalsIgnoreCase(PacketType.SUBSCRIBE.getValue())) {
                            System.out.print("Enter topic : ");
                            Scanner sc = new Scanner(System.in);
                            String topic = sc.nextLine();

                            map.put("packet", PacketType.SUBSCRIBE.getValue());
                            map.put("topic", topic);
                            Object object = (Object) map;
                            oos.writeObject(object);
                        }
                    if (fromUser.equalsIgnoreCase(PacketType.PUBLISH.getValue())) {
                        Scanner sc = new Scanner(System.in);
                            List<String> printData = new ArrayList<>();
                            printData.add("Topic");
                            printData.add("Message");
                            List<String> data = new ArrayList<>();
                            for (int i = 0; i < 2; i++) {
                                System.out.print("Enter " + printData.get(i) + " : ");
                                data.add(sc.nextLine());
                            }
                            map.put("packet", PacketType.PUBLISH.getValue());
                            map.put("topic", data.get(0));
                            map.put("message", data.get(1));
                            Object object = (Object) map;
                            oos.writeObject(object);
                        }
                        else if (fromUser.equals("UNSUBSCRIBE")) {
                            map.put("packet", "UNSUBSCRIBE");
                            map.put("topic", "test");
                            map.put("message", "U-msg");
                            Object object = (Object) map;
                            oos.writeObject(object);
                        }
                    }

                }
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + ServerConfig.getHost());
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " + ServerConfig.getHost());
            System.exit(1);
        }
    }
}
