package com.client;

import com.client.configs.ServerConfig;
import com.client.threads.Reader;
import com.client.threads.Writer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

@SpringBootApplication
public class ClientApplication {

	private final static Logger logger = LoggerFactory.getLogger(ClientApplication.class.getName());

	public void execute() {
		try {
			Socket socket = new Socket(ServerConfig.getHost(), ServerConfig.getPort());
			logger.info("*** Connected to Server");

			new Writer(socket, this).start();
			new Reader(socket, this).start();

		} catch (UnknownHostException ex) {
			logger.error("*** Server not found: " + ex.getMessage());
		} catch (IOException ex) {
			logger.info("*** I/O Error: " + ex.getMessage());
		}

	}

	// Main method
	public static void main(String[] args) {
		ClientApplication client = new ClientApplication();
		client.execute();
	}
}
