package specification;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.concordion.api.AfterSpecification;
import org.concordion.api.BeforeSpecification;

import ch.qos.logback.classic.PatternLayout;
import test.concordion.ProcessingResult;
import test.concordion.logback.LogBackHelper;
import test.concordion.logback.StoryboardMarkerFactory;

public class Extension extends BaseFixture {
	private PatternLayout layout;
	private PatternLayout backup;
	
	@BeforeSpecification
	private final void beforeSpecification() {
		switchToClassicLogger(false);
		getLogger().debug("preparing logger for testing");
		attchTextLayout();
	}

	@AfterSpecification
	private final void afterSpecification() {
		releaseTextLayout();
		restoreTextLayout();
	}
	
	////Helper Methods
	private void attchTextLayout() {
		layout = LogBackHelper.getTextLayout();
		
		backup = new PatternLayout();
		copy(layout, backup);
		
		// Remove date from Pattern so for easier comparison of log entry
		layout.setPattern("%-5level %logger{36} - %msg%n");
		layout.stop();
		layout.start();
		
		exampleLogListener.setLayout(layout);
	}
	
	private void releaseTextLayout() {
		exampleLogListener.setLayout(null);
	}
	
	private void resetLogListener() {
		exampleLogListener.reset();	
	}

	private void restoreTextLayout() {
		copy(backup, layout);
	}
	
	private void copy(PatternLayout src, PatternLayout dest) {
		dest.setPattern(src.getPattern());
	}
	////END Helper Methods
	
	// Classic logger is configured and has the pattern we need 
	public boolean isClassicLoggerConfigured() {
		return LogBackHelper.isConfiguredForTextLog();
	}

	public String canUseClassicLogger(String javaFragment) throws Exception {
		resetLogListener();
		
		processHtmlAndJava("<span concordion:execute=\"logSomething()\"></span>", javaFragment);

		return getLogContent();
	}

	public boolean specificationHasLinkToLogFile(String javaFragment) throws Exception {
		ProcessingResult processingResult = processHtmlAndJava("<span concordion:execute=\"logSomething()\"></span>", javaFragment);

		String html = processingResult.getElementXML("footer");
		
		return html.contains("href=\"testrig.log\">Log File</a>");
	}

	public boolean exampleHasLinkToLogFile(String javaFragment) throws Exception {
		ProcessingResult processingResult = processHtmlAndJava("<div class=\"example1\" concordion:example=\"example1\"><span concordion:execute=\"logSomething()\"></span></div>", javaFragment);

		String html = processingResult.getElementXML("example1");
		
		return html.contains("href=\"testrig[example1].log\">Log File</a>");
	}

	public boolean useLogViewer(String javaFragment, String method) throws Exception {
		javaFragment = javaFragment.replace("new LoggingFormatterExtension();", "new LoggingFormatterExtension()." + method + ";");
	
		ProcessingResult processingResult = processHtmlAndJava("<span concordion:execute=\"logSomething()\"></span>", javaFragment);

		String html = processingResult.getElementXML("footer");
		
		return html.contains("href=\"testrigLogViewer.html\">Log File</a>");
	}

	// Integration with other extensions
	public boolean integration() {
		if (!exampleStoryboardListener.getStreamContent().isEmpty()) {
			return false;
		}

		getLogger().with()
				.marker(StoryboardMarkerFactory.container("Doing Stuff"))
				.trace();

		getLogger().with()
				.marker(StoryboardMarkerFactory.storyboard("DummyPage"))
				.screenshot()
				.trace();

		String log = exampleStoryboardListener.getStreamContent();

		return log.contains("STORYBOARD_CONTAINER: Doing Stuff") &&
				log.contains("STORYBOARD_SCREENSHOT: DummyPage");
	}

	public boolean parallel() throws InterruptedException, ExecutionException {
		boolean result = true;

		exampleLogListener.reset();
		exampleStoryboardListener.resetStream();

		// TODO Nigel: Does this duplicate parallel runner adequately?
		List<Callable<WorkerThread>> tests = new ArrayList<Callable<WorkerThread>>();

		tests.add(new WorkerThread(0));
		tests.add(new WorkerThread(1));

		ExecutorService executor = Executors.newFixedThreadPool(tests.size());

		getLogger().debug("Master on thread " + Thread.currentThread().getName());
		getLogger().with().marker(StoryboardMarkerFactory.container("Master on thread " + Thread.currentThread().getName())).trace();

		try {
			List<Future<WorkerThread>> results = executor.invokeAll(tests);

			String thread = Thread.currentThread().getName();

			result = checkLogEqual("Master on thread " + thread, result);
			result = checkStoryboardLogEqual("STORYBOARD_CONTAINER: Master on thread " + thread, result);

			for (Future<WorkerThread> future : results) {
				String futureLog = future.get().logListenerContent;
				String futureStorybord = future.get().storyboardListenerContent;
				String message = "Worker " + future.get().index + " on thread " + future.get().thread;

				result = checkEqual(message, futureLog, result);
				result = checkEqual("STORYBOARD_CONTAINER: " + message, futureStorybord, result);
			}
		} finally {
			executor.shutdown();
			exampleStoryboardListener.resetStream();
		}

		return result;
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

			getTestRig()
					.withFixture(fixture)
					.processFragment("<span concordion:execute=\"writelog()\" />", "/" + this.getClass().getName().replace(".", "/").replace("$", "/"));

			storyboardListenerContent = fixture.exampleStoryboardListener.getStreamContent();
			logListenerContent = fixture.exampleLogListener.getLog();

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
