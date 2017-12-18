package test;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.rmi.server.SocketSecurityException;

import org.omg.Messaging.SYNC_WITH_TRANSPORT;

public class MulticastClient extends Thread {
	MulticastSocket socket = null;
	private boolean CLKsent = false;
	private Clock clock;
	private InetAddress address;
	private long sync_time;
	private int ID = 0;
	boolean idb = false;
	boolean alive = true;

	public MulticastClient(int init, long sync_time) {
		super("MulticastClient");
		try {
			socket = new MulticastSocket(9999);
			address = InetAddress.getByName("228.5.6.7");
			socket.joinGroup(address);
			getID();
		} catch (IOException e) {
			e.printStackTrace();
		}
		clock = new Clock(init);
		this.sync_time = sync_time;
//		new Thread() {
//			@Override
//			public void run() {
//				while (alive) {
//					try {
//						sleep(sync_time);
//						sendCLK();
//						CLKsent = true;
//					} catch (InterruptedException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//				}
//			}
//		}.start();

	}

	public void getID() {
		try {
			idb = true;
			send("ID");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		new Thread() {
			@Override
			public void run() {
				try {
					sleep(1000);
					idb = false;
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}.run();
	}

	public void sendCLK() {
		try {
			send("CLK " + ID);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void send(String msg) throws IOException {

		byte[] buf = new byte[256];

		// construct msg
		System.out.println(msg + " (sent)");
		buf = msg.getBytes();
		// send it

		DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 9999);
		socket.send(packet);

		// sleep for a while

	}

	boolean thr = false;
	int count = 0;
	int total = 0;
	int s = 0;
	String[] r;

	public void handleCommand(String given) throws IOException {
		if (given.matches("\\s")) {
			if (r[0].equals("ID") && idb == false) {
				send("ID" + ID);
				System.out.println("sent my ID" + ID);
			}
			if (r[0].equals("CLK")) {
				send(clock.getClock() + " " + given.split(" ")[1]);

			}
		}
		
		// if (given.equals("ID") && idb == false) {
		// send("ID " + ID);
		// System.out.println("sent my ID " + ID);
		// }
		// if (given.matches("^\\b(CLK)\\b\\s\\d+$")) {
		// send(clock.getClock() + " " + given.split(" ")[1]);
		// }
		// if (idb && given.matches("^\\b(ID)\\b\\s\\d+$")) {
		// System.out.println(given + " matched wtf");
		// int tmp =Integer.parseInt(given.split(" ")[1]) + 1;
		// if(tmp > ID) {
		// ID = tmp + 1;
		//
		// }
		// }
		//
		// if (CLKsent && given.matches("^\\d+\\s\\d+$")) {
		// if (Integer.parseInt(given.split(" ")[1]) == ID) {
		// count++;
		// total += Integer.parseInt(given.split(" ")[0]);
		// if (!thr) {
		// thr = true;
		// new Thread() {
		// @Override
		// public void run() {
		// try {
		// sleep(100);
		// thr = false;
		// CLKsent = false;
		// s = total / count;
		// clock.setClock(s);
		// System.out.println(s + " synchronized");
		// total = count = 0;
		// } catch (InterruptedException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// }
		// }.start();
		// }
		// }
		// }
	}

	@Override
	public void run() {
		boolean f = true;
		System.setProperty("java.net.preferIPv4Stack", "true");
		try {

			while (f) {
				DatagramPacket packet;
				byte[] buf = new byte[256];

				packet = new DatagramPacket(buf, buf.length);
				socket.receive(packet);
				String received;
				received = new String(packet.getData(), 0, packet.getLength());
				System.out.println(received + " (received)");
				handleCommand(received);
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
