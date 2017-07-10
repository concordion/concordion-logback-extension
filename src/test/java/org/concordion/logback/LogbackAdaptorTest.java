package org.concordion.logback;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.concordion.logback.LogbackAdaptor;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogbackAdaptorTest {
	private static final int THREAD_LIMIT = 5;
	private static final int THREAD_POOL = 20;

	@Test
	public void logginExtensionHandlesThreadingTest() throws InterruptedException, ExecutionException {
		ExecutorService executor = Executors.newFixedThreadPool(THREAD_LIMIT);
		List<Future<Boolean>> futures = new ArrayList<>();
		
		// Start 10 threads in parallel
		for (int i = 0; i < THREAD_POOL; i++) { 
			Callable<Boolean> worker = new MyRunnable(String.valueOf(i));
			
			futures.add(executor.submit(worker));
		}

		executor.shutdown();
		// Wait until all threads are finish
		while (!executor.isTerminated()) {
		}
		
		for (Future<Boolean> future : futures) {
			future.get();
		}
		
		System.out.println("\nFinished all threads");
	}


	public class MyRunnable implements Callable<Boolean> {
		Logger logger = LoggerFactory.getLogger(MyRunnable.class);
		
		private final String thread;

		MyRunnable(String thread) {
			this.thread = thread;
		}

		@Override
		public Boolean call() throws Exception {
			LogbackAdaptor lba = new LogbackAdaptor();

			String logfile = "/users/andrew/Documents/log" + thread + ".log";
			lba.startLogFile(logfile);

			for (int i = 0; i < 10; i++) { 
				logger.debug("This is thread " + thread + " attempt " + String.valueOf(i));
		
				assertThat(lba.getLogFile(), equalTo(logfile));
			}
			
			return true;
		}
	}

}
