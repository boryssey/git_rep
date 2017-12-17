package test;

import java.io.*;
import java.net.*;
import java.util.*;

public class MulticastMessage{

	DatagramSocket socket = null;
	private String message;
	public MulticastMessage(String msg) throws IOException {
		new Thread() {
			@Override
			public void run() {
				try {
					sleep(50);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}.run();
		socket = new DatagramSocket(9998);
		message = msg;
		send();
		socket.close();
	}

	public void send() throws IOException {
		
		byte[] buf = new byte[256];

		// construct msg
		System.out.println(message + " (sent)");
		buf = message.getBytes();

		// send it
		InetAddress group = InetAddress.getByName("228.5.6.7");
		DatagramPacket packet = new DatagramPacket(buf, buf.length, group, 9999);
		socket.send(packet);
		

		// sleep for a while

	}

}