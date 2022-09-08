package com.client;

import com.client.configs.ServerConfig;
import com.client.threads.Reader;
import com.client.threads.Writer;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

@SpringBootApplication
public class ClientApplication {

	private String hostname;
	private int port;

	public ClientApplication(String hostname, int port) {
		this.hostname = hostname;
		this.port = port;
	}

	public void execute() {
		try {
			Socket socket = new Socket(hostname, port);

			System.out.println("Connected to the chat server");

			new Reader(socket, this).start();
			new Writer(socket, this).start();

		} catch (UnknownHostException ex) {
			System.out.println("Server not found: " + ex.getMessage());
		} catch (IOException ex) {
			System.out.println("I/O Error: " + ex.getMessage());
		}

	}

	public static void main(String[] args) {
		ClientApplication client = new ClientApplication(ServerConfig.getHost(), ServerConfig.getPort());
		client.execute();
	}
}
