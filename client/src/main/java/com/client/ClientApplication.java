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

	public void execute() {
		try {
			Socket socket = new Socket(ServerConfig.getHost(), ServerConfig.getPort());
			System.out.println("*** Connected to Server ***");

			new Reader(socket, this).start();
			new Writer(socket, this).start();

		} catch (UnknownHostException ex) {
			System.out.println("*** Server not found: " + ex.getMessage() + " ***");
		} catch (IOException ex) {
			System.out.println("*** I/O Error: " + ex.getMessage() + " ***");
		}

	}

	// Main method
	public static void main(String[] args) {
		ClientApplication client = new ClientApplication();
		client.execute();
	}
}
