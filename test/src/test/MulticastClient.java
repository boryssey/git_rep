package test;

import java.io.IOException;
import java.net.*;

public class MulticastClient extends Thread {
	MulticastSocket socket = null;
	DatagramSocket dsocket = null;
	private boolean CLKsent = false;
	private Clock clock;
	private InetAddress address;
	private long sync_time;
	private int id = 0;
	boolean idb = false;
	boolean alive = true;

	public MulticastClient(int init, long sync_time) {
		super("MulticastClient");
		try {
			socket = new MulticastSocket(9999);
			address = InetAddress.getByName("228.5.6.7");
			dsocket = new DatagramSocket(9998);
			socket.joinGroup(address);
		} catch (IOException e) {
			e.printStackTrace();
		}
		startSendingCLK();
		clock = new Clock(init);
		this.sync_time = sync_time;
	}
	
	
	public void startSendingCLK() {
		new Thread() {
			@Override
			public void run() {
				while (alive) {
					try {
						sleep(sync_time);
						sendCLK();
						CLKsent = true;
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
			send("CLK " + InetAddress.getLocalHost());
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

	public void handleController() {
		new Thread() {
			@Override
			public void run() {
				while (!dsocket.isClosed()) {
					DatagramPacket packet;
					byte[] buf = new byte[256];
					packet = new DatagramPacket(buf, buf.length);
					InetAddress addr = packet.getAddress();
					int length = packet.getLength();
					try {
						dsocket.receive(packet);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					String received;
					received = new String(packet.getData(), 0, packet.getLength());
					String[] msg = received.split(" ");
					if (msg.length == 2) {
						if (msg[2].equals("counter")) {
							buf = (clock.getClock() + "").getBytes();
							packet = new DatagramPacket(buf, length, addr, 9999);
							try {
								dsocket.send(packet);
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
						if (msg[2].equals("period")) {
							buf = (sync_time + "").getBytes();
							packet = new DatagramPacket(buf, length, addr, 9999);
							try {
								dsocket.send(packet);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}

					}
					if (msg.length == 3) {
						if (msg[2].equals("counter")) {
							try {
								int tmp = Integer.parseInt(msg[2]);
								clock.setClock(tmp);
								buf = ("ACK").getBytes();
								packet = new DatagramPacket(buf, length, addr, 9999);
								try {
									dsocket.send(packet);
								} catch (IOException e) {
									e.printStackTrace();
								}
							} catch (NumberFormatException e) {
							}
						}
						if(msg[2].equals("period")) {
							try {
								long tmp = Long.parseLong(msg[2]);
								sync_time = tmp;
								buf = ("ACK").getBytes();
								packet = new DatagramPacket(buf, length, addr, 9999);
								try {
									dsocket.send(packet);
								} catch (IOException e) {
									e.printStackTrace();
								}
							} catch (NumberFormatException e) {
							}
						}
					}
				}
			}
		}.start();
	}

	protected boolean thr = false;
	protected int count = 0;
	protected int total = 0;
	protected int s = 0;

	public void handleCommand(String given) throws IOException {

		if (given.matches("^(CLK)\\s.+$")) {
			send(clock.getClock() + " " + given.split(" ")[1]);
		}

		if (CLKsent && given.matches("^\\d+\\s" + InetAddress.getLocalHost() + "$")) {
			count++;
			total += Integer.parseInt(given.split(" ")[0]);
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
