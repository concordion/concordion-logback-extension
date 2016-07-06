package org.concordion.logback;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Marker;
import org.slf4j.ext.CLogger;
import org.slf4j.ext.EventData;
import org.slf4j.ext.LogRecorder;

public class LogData implements LogRecorder {
	public static LogData capture(String data) {
		return new LogData(data);
	}

	private String data;

	private LogData(String data) {
		this.data = data;
	}


	public Marker getMarker() {
		return CLogger.DATA_MARKER;
	}

	@Override
	public EventData getEventData() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("data", data);

		return new EventData(map);
	}
}
