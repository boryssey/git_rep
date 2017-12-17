package test;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class MulticastClient extends Thread {
	MulticastSocket socket = null;
	private boolean t = false;
	private Clock clock;
	private InetAddress address;

	public MulticastClient(int init) {
		super("MulticastClient");
		try {
			socket = new MulticastSocket(9999);
			address = InetAddress.getByName("228.5.6.7");
			socket.joinGroup(address);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		clock = new Clock(init);
	}

	public void changebool() {
		t = true;
	}

	public void sendCLK() throws IOException{
		try {
			new MulticastServer("CLK");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Timer t = new Timer((long) 10);
		DatagramPacket packet;
		int count = 0;
		long total = 0;
		byte[] buf = new byte[256];
		while (t.isAlive()) {
			packet = new DatagramPacket(buf, buf.length);
			socket.receive(packet);
			String received = new String(packet.getData(), 0, packet.getLength());
			try {
				total += Long.parseLong(received);
				count++;
			} catch(NumberFormatException e) {
			}
		}
		clock.setClock(((int)total)/count);
	}

	@Override
	public void run() {
		boolean f = true;

		System.setProperty("java.net.preferIPv4Stack", "true");
		try {

			while (f) {
				DatagramPacket packet;

				// get a msg

				byte[] buf = new byte[256];

				packet = new DatagramPacket(buf, buf.length);
				socket.receive(packet);
				String received = new String(packet.getData(), 0, packet.getLength());
				System.out.println(received + " (received Client Mac)");
				if (received.equals("Hello")) {
					new MulticastServer("Good Morning from Mac");
				}
				if (received.equals("CLK")) {
					new MulticastServer(clock.getClock() + "");
				}

			}
			socket.leaveGroup(address);
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

			socket.close();
		}
		socket.close();
	}

}
