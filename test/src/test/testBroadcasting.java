package test;

import java.io.IOException;
import java.net.*;

import javax.swing.plaf.SliderUI;

public class testBroadcasting {
	public static void main(String[] args) throws IOException {

		System.setProperty("java.net.preferIPv4Stack", "true");
		MulticastClient n = new MulticastClient(50, 3000);
		n.start();
//		String id = "id 5";
//		String a = "id554";
//		if(id.matches("^\\b(id)\\b\\s\\d+$")) {
//			String[] s = id.split(" ");
//			System.out.println(id.split(" ")[1]);
//		}
	}

}
//CLKsent && given.matches("^\\b(CLK)\\b\\s\\d+$"))