package controller;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class Controller {
	DatagramSocket socket = null;
	DatagramPacket packet = null;

	public Controller(String[] a) {
		try {
			socket = new DatagramSocket();
			socket.connect(InetAddress.getByName(a[0]), 9999);

		} catch (SocketException | UnknownHostException e) {
			System.err.println("Problems with creating socket: " + e);
			System.exit(1);
		}
	}

	public static void main(String[] args) throws SocketException {

		if (args.length != 2 || args.length != 3) {
			System.exit(1);
		}
		Controller contr = new Controller(args);

		if (args[1].equals("get")) {
			if (args[2].equals("counter")) {
				contr.send("get counter");
				String rep = contr.receiveReply();
				System.out.println(rep);
			}
			if(args[2].equals("period")) {
				contr.send("get period");
				String rep = contr.receiveReply();
				System.out.println(rep);
			}
		}
		if (args[1].equals("set")) {
			if(args[2].equals("counter")) {	
				try {
					Integer.parseInt(args[3]);
				}catch(NumberFormatException e){
					System.err.println("Need integer or long by 3rd parameter: " + e);
					System.exit(4);
			    }
				contr.send("set counter " + args[3]);
				String rep = contr.receiveReply();
				System.out.println(rep);
			}
			if(args[2].equals("period")) {	
				try {
					Long.parseLong(args[3]);
				}catch(NumberFormatException e){
					System.err.println("Need integer or long by 3rd parameter: " + e);
					System.exit(4);
			    }
				contr.send("set period" + args[3]);
				String rep = contr.receiveReply();
				System.out.println(rep);
			}
		}

	}

	public void send(String msg) {

		byte[] buf = new byte[256];

		// construct msg

		buf = msg.getBytes();
		// send it

		DatagramPacket packet = new DatagramPacket(buf, buf.length);
		try {
			socket.send(packet);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println("Problems with sending message: " + e);
			System.exit(2);
		}

		// sleep for a while
	}
	public String receiveReply() {
		byte[] buf = new byte[256];

		packet = new DatagramPacket(buf, buf.length);
		try {
			socket.receive(packet);
		} catch (IOException e) {
			
			System.err.println("Problems with receiving reply: " + e);
			System.exit(3);
		}
		String received;
		received = new String(packet.getData(), 0, packet.getLength());

		return received;
	}
}
