package specification;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.concordion.api.Resource;

import test.concordion.TestRig;
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
		
		return log.contains("STORYBOARD_CONTAINER: Doing Stuff") &&
		 		log.contains("STORYBOARD_SCREENSHOT: DummyPage");
	}
	
	public boolean parallel() throws InterruptedException, ExecutionException {
		boolean result = true;
		
		exampleLogListener.resetStream();
		exampleStoryboardListener.resetStream();

		//TODO Nigel - how would I duplicate parallel runner extension so that can run these 2 tests 
		// in parallel to ensure listeners on pick up data for the thread they are on.  
		List<Callable<WorkerThread>> tests = new ArrayList<>();

		tests.add(new WorkerThread(0));
		tests.add(new WorkerThread(1));
		
		ExecutorService executor = Executors.newFixedThreadPool(tests.size());

		getLogger().debug("Master on thread " + Thread.currentThread().getName());
		getLogger().with().marker(StoryboardMarkerFactory.container("Master on thread " + Thread.currentThread().getName())).trace();
				
		try {
			List<Future<WorkerThread>> results = executor.invokeAll(tests);
						
			String logListener = exampleLogListener.getStreamContent();
			String expected = "Master on thread " + Thread.currentThread().getName();
			if (!logListener.equals(expected)) {
				logFailure(expected, logListener);
				result = false;
			}
			
			String sbListener = exampleStoryboardListener.getStreamContent();
			expected = "STORYBOARD_CONTAINER: Master on thread " + Thread.currentThread().getName();
			if (!sbListener.equals(expected)) {
				logFailure(expected, logListener);
				result = false;
			}
			
			for (Future<WorkerThread> future : results) {
				String futureLog = future.get().logListenerContent;
				String futureStorybord = future.get().storyboardListenerContent;
				String message = "Worker " + future.get().index + " on thread " + future.get().thread;
				expected = message;
				
				if (!futureLog.equals(expected)) {
					logFailure(expected, logListener);
					result = false;
				}

				expected = "STORYBOARD_CONTAINER: " + message;
				if (!futureStorybord.equals(expected)) {
					logFailure(expected, logListener);
					result = false;
				}
			}
		} finally {
			executor.shutdown();
			exampleStoryboardListener.resetStream();
		}
		
		return result;
	}
	
	private void logFailure(String expected, String actual) {
		getLogger().error("Actual result not equal to expected: [Expected]: {}, [Actual]: {}", expected, actual);
	}

	public class WorkerThread implements Callable<WorkerThread> {
		final int index;
		String thread;
		String storyboardListenerContent;
		String logListenerContent;
		
		public WorkerThread(int index) {
			this.index = index;
		}
	    
		@Override
		public WorkerThread call() throws Exception {
			this.thread = Thread.currentThread().getName();
			
			WorkerFixture fixture = new WorkerFixture(index, thread);
			
			TestRig rig = new TestRig();
			rig.withFixture(fixture);
			rig.withResource(new Resource("/org/concordion/ext/resource/tooltip.css"), "");
			rig.withResource(new Resource("/org/concordion/ext/resource/bubble.gif"), "");
			rig.withResource(new Resource("/org/concordion/ext/resource/bubble_filler.gif"), "");
			rig.withResource(new Resource("/org/concordion/ext/resource/i16.png"), "");
			rig.withResource(new Resource("/font-awesome-4.6.3/css/font-awesome.css"), "");
			rig.withResource(new Resource("/font-awesome-4.6.3/fonts/fontawesome-webfont.eot"), "");
			rig.withResource(new Resource("/font-awesome-4.6.3/fonts/fontawesome-webfont.svg"), "");
			rig.withResource(new Resource("/font-awesome-4.6.3/fonts/fontawesome-webfont.ttf"), "");
			rig.withResource(new Resource("/font-awesome-4.6.3/fonts/fontawesome-webfont.woff"), "");
			rig.withResource(new Resource("/font-awesome-4.6.3/fonts/fontawesome-webfont.woff2"), "");
			rig.withResource(new Resource("/font-awesome-4.6.3/fonts/FontAwesome.otf"), "");
						
			rig.processFragment("<span concordion:execute=\"writelog()\" />", "/" + this.getClass().getName().replace(".", "/").replace("$", "/"));

			storyboardListenerContent = fixture.exampleStoryboardListener.getStreamContent();
	        logListenerContent = fixture.exampleLogListener.getStreamContent();
	        
			return this;
		}
		
		public void writelog() {
			getLogger().debug("Worker " + index + " on thread " + this.thread);
			getLogger().with().marker(StoryboardMarkerFactory.container("Worker " + index + " on thread " + this.thread)).trace();
		}

	}
	
	public class WorkerFixture extends BaseFixture {
		final int index;
		String thread;
		
		public WorkerFixture(int index, String thread) {
			this.index = index;
			this.thread = thread;
		}
	    
		public void writelog() {
			getLogger().debug("Worker " + index + " on thread " + this.thread);
			getLogger().with().marker(StoryboardMarkerFactory.container("Worker " + index + " on thread " + this.thread)).trace();
		}
	}
}
