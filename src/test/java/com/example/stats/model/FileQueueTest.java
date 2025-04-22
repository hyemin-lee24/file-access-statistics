package com.example.stats.model;

import java.util.Scanner;

import org.junit.jupiter.api.Test;

public class FileQueueTest {
	
	private FileQueue queue;
	
	@Test
	public void testQueue() {
		queue = new FileQueue("sample");
		
		ThreadGroup group = new ThreadGroup("polls");
		PollThread t1 = new PollThread(group, 1);
		PollThread t2 = new PollThread(group, 2);
		PollThread t3 = new PollThread(group, 3);
		
		t1.start();
		t2.start();
		t3.start();
		
		Scanner sc = new Scanner(System.in);
		String cmd = null;
		while (sc.hasNext()) {
			cmd = sc.nextLine();
			if (cmd.equals("stop")) {
				group.interrupt();
				break;
			} else {
				queue.offer(cmd);
			}
		}
		sc.close();
	}
	
	public class PollThread extends Thread {
		public PollThread(ThreadGroup group, int num) {
			super(group, group.getName() + num);
		}
		
		@Override
		public void run() {
			String data = null;
			while (!isInterrupted()) {
				data = queue.poll();
				System.out.println("[" + getName() + "] poll data (" + data + ")");
			}
			
			System.out.println("[" + getName() + "] interrupted!");
		}
		
	}

}
