package org.concordion.logback;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.concordion.logback.LogbackAdaptor;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogbackAdaptorTest {
	private static final int THREAD_LIMIT = 5;
	private static final int THREAD_POOL = 20;
	
	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

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

			String logfile = folder.getRoot().getAbsolutePath() + "/thread_" + thread +"_";
			lba.startLogFile(logfile);

			for (int i = 0; i < 10; i++) { 
				logger.debug("This is thread " + thread + " attempt " + String.valueOf(i));
		
				assertThat(lba.getLogFile().getName(), startsWith(new File(logfile).getName()));
			}

			return true;
		}
	}

}
