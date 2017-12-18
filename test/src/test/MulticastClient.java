package test;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import org.omg.Messaging.SYNC_WITH_TRANSPORT;

public class MulticastClient extends Thread {
	MulticastSocket socket = null;
	private boolean CLKsent = false;
	private Clock clock;
	private InetAddress address;
	private long sync_time;

	public MulticastClient(int init, long sync_time) {
		super("MulticastClient");
		try {
			socket = new MulticastSocket(9999);
			address = InetAddress.getByName("228.5.6.7");
			socket.joinGroup(address);
		} catch (IOException e) {
			e.printStackTrace();
		}
		clock = new Clock(init);
		this.sync_time = sync_time;
		new Thread() {
			@Override
			public void run() {
				while (true) {
					try {
						sleep(sync_time);
						sendCLK();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}.start();
	}

	public void sendCLK() {
		try {
			send("CLK");
			CLKsent = true;
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

				if (received.equals("CLK")) {
					send(clock.getClock() + "");
				}

				if (CLKsent && received.matches("\\b([0-9])\\d+\\b")) {
					count++;
					total += Integer.parseInt(received);
					if (!thr) {
						thr = true;
						new Thread() {
							@Override
							public void run() {
								try {
									sleep(100);
									thr = false;
									CLKsent = false;
									s = total / count;
									clock.setClock(s);
									System.out.println(s + " synchronized");
									total = count = 0;
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						}.start();
					}
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
