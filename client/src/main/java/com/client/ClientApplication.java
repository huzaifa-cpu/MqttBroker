package com.client;

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
	private String userName;

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

	void setUserName(String userName) {
		this.userName = userName;
	}

	String getUserName() {
		return this.userName;
	}


	public static void main(String[] args) {
//        if (args.length < 2) return;

		String hostname = "localhost";
		int port = 5555;

		ClientApplication client = new ClientApplication(hostname, port);
		client.execute();
	}
}

//@SpringBootApplication
//public class ClientApplication {
//
//	public static void main(String[] args) {
//
//		try {
//			// create socket
//			Socket clientSocket = new Socket(Config.getHost(), Config.getPort());
//			// output object
//			OutputStream os = clientSocket.getOutputStream();
//			ObjectOutputStream oos = new ObjectOutputStream(os);
//			// input object
//			InputStream is = clientSocket.getInputStream();
//			ObjectInputStream ois = new ObjectInputStream(is);
//			// input-output data
////			PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
////			BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
//			// input from terminal
//			BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
//
//			// Initializations
//			String fromServer;
//			String fromUser;
//			Object inputObject = null;
//			// request object
//			Map<String, String> connectMap = new HashMap<>();
//			connectMap.put("port", String.valueOf(clientSocket.getLocalPort()));
//			connectMap.put("packet", "CONNECT");
//			Object connectObject = (Object)connectMap ;
//			oos.writeObject(connectObject);
//
//			while(!Thread.interrupted()){
//
//
//			// make connection alive
//			while ((inputObject = ois.readObject()) != null) {
//
//				// from server
//				Map inputMap = (Map)inputObject;
//				Boolean returnCode = (Boolean) inputMap.get("returnCode");
//				System.out.println("Server: " + inputMap);
////				if(returnCode.equals(true) && inputMap.containsKey("message")){
////					String message = (String) inputMap.get("message");
////					String topic = (String) inputMap.get("topic");
////					System.out.println("message from client: " + message);
////					System.out.println("topic from client: " + topic);
////				}
//
//				// to server
//				fromUser = stdIn.readLine();
//				if (fromUser != null) {
//					Map<String, String> map = new HashMap<>();
//					map.put("port", String.valueOf(clientSocket.getLocalPort()));
//
//					if(fromUser.equals("SUBSCRIBE")){
//						Scanner sc = new Scanner(System.in);
//						System.out.print("Enter Topic: ");
//						String topic = sc.nextLine();
//						map.put("packet", "SUBSCRIBE");
//						map.put("topic", topic);
//						Object object = (Object)map ;
//						oos.writeObject(object);
//					}
//					if(fromUser.equals("PUBLISH")){
//						Scanner sc = new Scanner(System.in);
//						List<String> printData = new ArrayList<>();
//						printData.add("Topic");
//						printData.add("Message");
//						List<String> data = new ArrayList<>();
//						for (int i = 0; i < 2; i++)
//						{
//							System.out.print("Enter "+ printData.get(i) + " : ");
//							data.add(sc.nextLine());
//						}
//						map.put("packet", "PUBLISH");
//						map.put("topic", data.get(0));
//						map.put("message", data.get(1));
//						Object object = (Object)map ;
//						oos.writeObject(object);
//					}
//					if(fromUser.equals("UNSUBSCRIBE")){
//						map.put("packet", "UNSUBSCRIBE");
//						map.put("topic", "test");
//						map.put("message", "U-msg");
//						Object object = (Object)map ;
//						oos.writeObject(object);
//					}
//				}
//			}
//				}
//		} catch (UnknownHostException e) {
//			System.err.println("Don't know about host " + Config.getHost());
//			System.exit(1);
//		} catch (IOException e) {
//			System.err.println("Couldn't get I/O for the connection to " + Config.getHost());
//			System.exit(1);
//		}
//		catch (ClassNotFoundException e) {
//			throw new RuntimeException(e);
//		}
//	}
//}