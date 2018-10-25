package skj.project.Agent;

public class Clock extends Thread {
	private long count;
	private boolean al = true;

	public Clock(long init) {
		count = init;
		start();
	}

	public long getClock() {
		return count;

	}

	public void setClock(long given) {
		count = given;
	}

	@Override
	public void run() {
		while (al) {
			try {
				sleep(1);
			} catch (InterruptedException e) {
				
				e.printStackTrace();
				al = false;
			}
			count++;
		}
	}
}
