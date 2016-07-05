package org.concordion.logback;

import org.slf4j.ext.LogRecorder;

public class LogData implements LogRecorder {
	private LogData() {
	}

	public static LogData capture(String format, Object... arguments) {
		return new LogData();
	}
}
