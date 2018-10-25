package skj.project.Agent;

import java.io.IOException;
import java.net.*;

public class Agent extends Thread {
	private MulticastSocket socket = null; // Socket which sends and receives packets to/from broadcast IP
	private DatagramSocket dsocket = null; // Socket which sends and receives packets to/from Controller
	private boolean CLKsent = false; // true if we sent a CLK request /
										//false if we didn't send CLK request yet
											// or
										// we finished receiving responses to CLK request
	private Clock clock;
	private InetAddress address; // Broadcast IP;
	private long period; // Period
	boolean alive = true; // true if main Thread is alive

	public Agent(long init, long period) {
		super("MulticastClient");
		try {
			socket = new MulticastSocket(9999);
			address = InetAddress.getByName("228.5.6.7");
			dsocket = new DatagramSocket(9997);
			socket.joinGroup(address);
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.period = period;
		clock = new Clock(init);
		start();
		startSendingCLK();

	}
	/*
	 * Starts thread which sends CLK request every specified time period
	 * 
	 */
	public void startSendingCLK() {
		new Thread() {
			@Override
			public void run() {
				while (alive) {
					try {
						sleep(period);
						sendCLK();
						CLKsent = true;
					} catch (InterruptedException e) {
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
			e.printStackTrace();
		}

	}
	/*
	 * 
	 * Broadcasts given message.
	 * 
	 */
	public void send(String msg) throws IOException {

		byte[] buf = new byte[256];

		// Byte representation of the string
		System.out.println(msg + " (sent)");
		buf = msg.getBytes();
		// send it

		DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 9999);
		socket.send(packet);

	}

	/*
	 * 
	 * Starts new thread which receives messages from controller and handles them.
	 * 
	 */
	public void handleController() {
		new Thread() {
			@Override
			public void run() {

				DatagramPacket packet;
				int port = 0;
				while (!dsocket.isClosed() && alive) {
					// receives while DatagramSocket isn't closed and while main thread runs.
					byte[] buf = new byte[256];
					packet = new DatagramPacket(buf, buf.length);

					try {
						dsocket.receive(packet);
					} catch (IOException e) {
						e.printStackTrace();
					}

					InetAddress addr = packet.getAddress();
					port = packet.getPort();
					String received;
					received = new String(packet.getData(), 0, packet.getLength());
					System.out.println(received + "received");
					String[] msg = received.split(" ");
					// handling get request
					if (msg.length == 2) {
						// handling get counter request
						if (msg[1].equals("counter")) {
							buf = (clock.getClock() + "").getBytes();
							packet = new DatagramPacket(buf, buf.length, addr, port);
							try {
								dsocket.send(packet);
							} catch (IOException e) {
								e.printStackTrace();

							}
						}
						// handling get period request
						if (msg[1].equals("period")) {
							buf = (period + "").getBytes();
							packet = new DatagramPacket(buf, buf.length, addr, port);
							try {
								dsocket.send(packet);
							} catch (IOException e) {
								e.printStackTrace();
							}
						}

					}
					// handling set request
					if (msg.length == 3) {
						// handles set counter/period request and sends ACK message when done
						try {
							long tmp = Long.parseLong(msg[2]);
							if (msg[1].equals("counter"))
								clock.setClock(tmp);
							if (msg[1].equals("period"))
								period = tmp;
							buf = ("ACK").getBytes();
							packet = new DatagramPacket(buf, buf.length, addr, port);
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
		}.start();
	}

	protected boolean thr = false; // true if we already have started receiving CLK responses.
	protected int count = 0; // counts number of received clock values.
	protected int total = 0; // total value of clocks

	public void handleCommand(String given) throws IOException {

		/*
		 * If message is in format "CLK " then it is some Agent's CLK request
		 * Reply with your clock value and senders Address (second part of CLK request).
		 */

		if (given.matches("^(CLK)\\s.+$")) {

			send(clock.getClock() + " " + given.split(" ")[1]);
		}

		/*
		 * Checking if this Agent has sent a CLK request earlier and if the reply that
		 * we received is with this Agent's address
		 * 
		 */

		if (CLKsent && given.matches("^\\d+\\s" + InetAddress.getLocalHost() + "$")) {
			count++;
			total += Integer.parseInt(given.split(" ")[0]);
			if (!thr) { // checking if we started a timer for receiving CLK responses.
				thr = true;
				new Thread() { // Starting receiving messages for 0,1sec.
					@Override
					public void run() {
						try {
							sleep(100);
							thr = false;
							CLKsent = false;
							int s = total / count;
							clock.setClock(s);
							System.out.println(s + " synchronized");
							total = count = 0;
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}.start();

			}
		}
	}

	/*
	 * 
	 * During runtime receives messages from other agents and passes them to
	 * handleCommand method.
	 *
	 */
	@Override
	public void run() {
		System.setProperty("java.net.preferIPv4Stack", "true");
		try {
			while (alive) {
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
			e.printStackTrace();
			close();
		}
		close();
		}
	public void close() {
		try {
			socket.leaveGroup(address);
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
	}
	public static void main(String[] args) throws IOException {
		System.setProperty("java.net.preferIPv4Stack", "true");
		if (args.length != 2) {
			System.err.println("Two parameters: long and long. Is it so hard?");
			System.exit(0);
		}
		long clock;
		Agent n = null;
		long period;
		try {
			clock = Long.parseLong(args[0]);
			period = Long.parseLong(args[1]);
			n = new Agent(clock, period);
		} catch (NumberFormatException e) {
			System.err.println("Two parameters: long and long. Is it so hard?");
			System.exit(0);
		}
		n.handleController();

	}

}
