package com.server;

import com.server.enums.PacketType;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ServerApplication {

	public static void main(String[] args) {
		BootStrap.startMqttServer();
	}
}