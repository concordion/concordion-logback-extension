package org.concordion.logback;

import org.slf4j.ext.LogRecorder;

public class LogHtml implements LogRecorder {
	public static LogHtml capture(String data, String format, Object... arguments) {
		return new LogHtml();
	}

	private LogHtml() {
	}


}
