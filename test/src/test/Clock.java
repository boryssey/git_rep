package test;

public class Clock extends Thread {
	private int count;
	private boolean al = true;

	public Clock(int init) {
		count = init;
		start();
	}

	public long getClock() {
		return count;

	}

	public void setClock(int given) {
		count = given;
	}

	@Override
	public void run() {
		while (al) {
			try {
				sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				al = false;
			}
			count++;
		}
	}
}
