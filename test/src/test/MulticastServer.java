package test;

import java.io.*;
import java.net.*;
import java.util.*;

public class MulticastServer {

	DatagramSocket socket = null;
	private String message;
	public MulticastServer(String msg) throws IOException {
		new Thread() {
			 @Override
			 public void run() {
				 try {
					 sleep(50);
				 } catch(InterruptedException e) {
					 e.printStackTrace();
				 }
				
			 }
		 }.run();
		socket = new DatagramSocket(9998);
		message = msg;
		send();
	}

	public void send() throws IOException {
		
		byte[] buf = new byte[256];

		// construct msg
		System.out.println(message + " (Server Mac)");
		buf = message.getBytes();

		// send it
		InetAddress group = InetAddress.getByName("228.5.6.7");
		DatagramPacket packet = new DatagramPacket(buf, buf.length, group, 9999);
		socket.send(packet);
		socket.close();
		

		// sleep for a while

	}

}