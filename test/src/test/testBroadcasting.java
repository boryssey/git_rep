package test;

import java.io.IOException;
import java.net.*;

import javax.swing.plaf.SliderUI;

public class testBroadcasting {
	public static void main(String[] args) throws IOException {
		// t.start();
		 System.setProperty("java.net.preferIPv4Stack", "true");
			MulticastClient n = new MulticastClient(100, 2000);
			n.start();
		//
//		 Clock n = new Clock(0);
//		 System.out.println("s");
		 

//		 MulticastServer s = new MulticastServer();
//		 s.send("Hello");
//		 new Thread() {
//			 @Override
//			 public void run() {
//				 try {
//					 sleep(5000);
//					 c.sendCLK();
//				 } catch(InterruptedException | IOException e) {
//					 e.printStackTrace();
//				 }
//				
//			 }
//		 }.start();

		// System.out.println("SUCCess");

	}

}
