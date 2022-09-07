package com.server.configs;

public class ServerConfig {

    private static String host="localhost";

    private static  int port = 5555;

    public static String getHost() {
        return host;
    }

    public static void setHost(String host) {
        ServerConfig.host = host;
    }

    public static int getPort() {
        return port;
    }

    public static void setPort(int port) {
        ServerConfig.port = port;
    }
}
