package test;

public class Timer extends Thread{
	private Long time;
	
	public Timer(Long t) {
		time = t;
		
	}
	
	@Override
	public void run() {
		while(time >= 0) {
			try {
				sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			time--;
		}
	}
}
