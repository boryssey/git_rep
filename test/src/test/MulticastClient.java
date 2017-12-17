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
			// TODO Auto-generated catch block
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
		// startSyncThread();
	}

	// public void startSyncThread() {
	// new Thread() {
	// @Override
	// public void run() {
	// while(al) {
	// try {
	// sleep(getSTime());
	// sendCLK();
	// } catch (InterruptedException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
	// }
	// }.start();
	// }


	public long getSTime() {
		return sync_time;
	}

	public void sendCLK() {
		try {
			new MulticastMessage("CLK");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		CLKsent = false;

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
					new MulticastMessage("Good Morning from Mac");
				}
				if (received.equals("CLK")) {
					new MulticastMessage(clock.getClock() + "");
				}
				if(CLKsent && received.matches("\\b([0-9])\\d+\\b")) {
					Timer t = new Timer((long) 50);
					t.start();
					int count = 0;
					long total = 0;
					while (t.isAlive()) {
						
						try {
							total += Long.parseLong(received);
							count++;
						} catch (NumberFormatException e) {
						}
					}
					clock.setClock(((int) total) / count);
					count = 0;
					total = 0;
					CLKsent = false;
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
