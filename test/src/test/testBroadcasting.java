package test;

import java.io.IOException;
import java.net.*;

import javax.swing.plaf.SliderUI;

public class testBroadcasting {
	public static void main(String[] args) throws IOException {
		System.setProperty("java.net.preferIPv4Stack", "true");
		MulticastClient n = new MulticastClient(100, 1000);
		n.start();
		n.handleController();
//		System.out.println(InetAddress.getByName("localhost"));
		
	}

}
