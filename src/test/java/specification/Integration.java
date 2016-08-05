package specification;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import test.concordion.logback.DummyScreenshotTaker;
import test.concordion.logback.StoryboardMarkerFactory;

public class Integration extends BaseFixture {

	// Integration with other extensions
	public boolean integration()  {
		if (!exampleStoryboardListener.getStreamContent().isEmpty()) {
			return false;
		}

		getLogger().with()
				.marker(StoryboardMarkerFactory.container("Doing Stuff"))
				.trace();
		
		getLogger().with()
			.marker(StoryboardMarkerFactory.storyboard("DummyPage"))
			.screenshot(getLoggingAdaptor().getLogFile(), new DummyScreenshotTaker())
			.trace();

		String log = exampleStoryboardListener.getStreamContent();
		
		return log.contains("FOUND MARKER STORYBOARD_CONTAINER") &&
		 		log.contains("FOUND MARKER STORYBOARD_SCREENSHOT");
	}
	
	public boolean parallel() throws InterruptedException, ExecutionException {
		exampleStoryboardListener.resetStream();

		//TODO Nigel - how would I duplicate parallel runner extension so that can run these 2 tests 
		// in parallel to ensure listeners on pick up data for the thread they are on.  
		List<Callable<String>> tests = new ArrayList<>();

		tests.add(new WorkerThread(0));
		tests.add(new WorkerThread(1));
		
		ExecutorService executor = Executors.newFixedThreadPool(tests.size());

		try {
			List<Future<String>> results = executor.invokeAll(tests);
			 
			if (!exampleStoryboardListener.getStreamContent().isEmpty()) {
				return false;
			}
			
			for (Future<String> future : results) {
				if (!future.get().equals("FOUND MARKER STORYBOARD_CONTAINER")) {
					return false;
				}
			}
		} finally {
			executor.shutdown();
			exampleStoryboardListener.resetStream();
		}
		
		return true;
	}
	
	public class WorkerThread extends BaseFixture implements Callable<String> {
		final int index;
		
		public WorkerThread(int index) {
			this.index = index;
		}
		
	    @Override
	    public String toString(){
	        return this.exampleStoryboardListener.getStreamContent();
	    }

		@Override
		public String call() throws Exception {
			getLogger().with().marker(StoryboardMarkerFactory.container("Worker " + index + " on thread " + Thread.currentThread().getName()));
			return toString();
		}
	}
}
