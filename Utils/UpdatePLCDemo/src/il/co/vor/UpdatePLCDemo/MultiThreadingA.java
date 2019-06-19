package il.co.vor.UpdatePLCDemo;

public class MultiThreadingA {
	static long c = 0;

	public static void main(String[] args) {
		
		Thread A = new Thread(new Runnable() {

			@Override
			public void run() {
				System.out.println("thread a run");
				for(long i = 0; i < 10; i++) {
					//System.out.println("thread a: " + c);
					c++;
				}
				
			}});
		Thread B = new Thread(new Runnable() {

			@Override
			public void run() {
				System.out.println("thread b run");
				for(long i = 0; i < 10; i++) {
					//System.out.println("thread b: " + c);
					c++;
				}
				
			}});
		
		A.start();
		B.start();
		
		try {
			A.join();
			B.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		System.out.println(c);
	}

}