package specification;

import org.concordion.api.AfterSpecification;
import org.concordion.api.BeforeSpecification;

import ch.qos.logback.ext.html.Format;
import ch.qos.logback.ext.html.HTMLLayout;
import ch.qos.logback.ext.html.StepRecorder;
import test.concordion.logback.LogBackHelper;

public class LogBackConfiguration extends BaseFixture {
	private HTMLLayout layout;
	private HTMLLayout backup;

	@BeforeSpecification
	private final void beforeSpecification() {
		// Force the logger to create the various appenders and layouts required for these tests
		getLogger().debug("preparing logger for testing");
		attchHtmlLayout();
	}

	@AfterSpecification
	private final void afterSpecification() {
		releaseHtmlLayout();
	}

	//// Helper Methods
	private void attchHtmlLayout() {
		layout = LogBackHelper.getHtmlLayout();

		backup = new HTMLLayout();
		copy(layout, backup);

		exampleLogListener.setLayout(layout);
	}

	private void releaseHtmlLayout() {
		exampleLogListener.setLayout(null);
	}

	private void resetLogListener() {
		exampleLogListener.reset();
	}

	private void restoreHtmlLayout() {
		copy(backup, layout);
	}

	private void copy(HTMLLayout src, HTMLLayout dest) {
		dest.setStylesheet(src.getStylesheet());
		dest.setFormat(src.getFormat());
		dest.setPattern(src.getPattern());
		dest.setStepRecorder(src.getStepRecorder());
	}
	//// END Helper Methods

	// Log statement is in table column format
	public boolean multiColumnLayout() {
		boolean result = true;

		resetLogListener();

		layout.setFormat(Format.COLUMN.name());
		layout.setPattern("%date{HH:mm:ss.SSS}%message%file%line");

		getLogger().debug("multiColumnLayout example");

		restoreHtmlLayout();

		return checkLogContains("<td class=\"Message\">multiColumnLayout example</td>", result);
	}

	// Log statement is in a single table column
	public boolean singleColumnLayout() {
		boolean result = true;

		resetLogListener();

		layout.setFormat(Format.STRING.name());
		layout.setPattern("%message %file");

		getLogger().debug("singleColumnLayout example");

		restoreHtmlLayout();

		return checkLogContains("<td>singleColumnLayout example HtmlLog.java</td>", result);
	}

	public boolean recordStepsUsingLogLevel() {
		boolean result = true;

		resetLogListener();

		layout.setStepRecorder(StepRecorder.INFO_LOG_LEVEL.name());

		getLogger().info("Step");
		getLogger().debug("Statement");

		restoreHtmlLayout();

		result = checkLogContains("<td colspan=\"5\">Step</td>", result);
		result = checkLogContains("<td class=\"Message\">Statement</td>", result);

		return result;
	}

	public boolean recordStepsUsingStepMarker() {
		boolean result = true;

		resetLogListener();

		layout.setStepRecorder(StepRecorder.STEP_MARKER.name());

		getLogger().step("Step");
		getLogger().info("Statement");

		restoreHtmlLayout();

		result = checkLogContains("<td colspan=\"5\">Step</td>", result);
		result = checkLogContains("<td class=\"Message\">Statement</td>", result);

		return result;
	}
}
