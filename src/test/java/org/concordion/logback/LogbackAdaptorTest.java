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

			String logfile = "/users/andrew/Documents/thread_" + thread +"_";
			lba.startLogFile(logfile);

			for (int i = 0; i < 10; i++) { 
				// TODO test without debug as that caused another problem in LogbackAdaptor
				logger.debug("This is thread " + thread + " attempt " + String.valueOf(i));
		
// TODO Expecting to occasionally get java.util.ConcurrentModificationException at java.util.LinkedHashMap$LinkedHashIterator.nextNode (LinkedHashMap.java:711)
// when calling getLogFile().  Need to fix this.
//				
// 	Sometimes this test gets following exception.  Why?  I've never seen it at MSD but not to say it hasn't happened.
//				Caused by: java.lang.NullPointerException
//				at org.concordion.logback.LogbackAdaptorTest$MyRunnable.call(LogbackAdaptorTest.java:71)
//				at org.concordion.logback.LogbackAdaptorTest$MyRunnable.call(LogbackAdaptorTest.java:1)
//				at java.util.concurrent.FutureTask.run(FutureTask.java:266)
//				at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1142)
//				at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:617)
//				at java.lang.Thread.run(Thread.java:748)
				assertThat(lba.getLogFile().getAbsolutePath(), startsWith(logfile));
			}
			
	



			return true;
		}
	}

}
