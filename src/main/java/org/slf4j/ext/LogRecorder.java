package org.slf4j.ext;

import org.slf4j.Marker;

public interface LogRecorder {
	public Marker getMarker();

	public EventData getEventData();

}
