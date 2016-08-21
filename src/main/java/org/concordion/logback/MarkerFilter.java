package org.concordion.logback;

import org.slf4j.Marker;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;

public class MarkerFilter extends Filter<ILoggingEvent> {
	String[] filterMarkers = null;
	String threadName = null;
	
	public void setFilterMarkers(String[] markers) {
		this.filterMarkers = markers;
	}

	public void setThread(String name) {
		this.threadName = name;
	}

	@Override
	public FilterReply decide(ILoggingEvent event) {
		if (threadName != null && !threadName.isEmpty()) {
			if (!Thread.currentThread().getName().equals(threadName)) {
				return FilterReply.DENY;
			}
		}
		
		if (filterMarkers == null) {
			return FilterReply.ACCEPT;
		}
		
		for (String marker : filterMarkers) {
			if (containsMarker(event.getMarker(), marker)) {
				return FilterReply.ACCEPT;
			}
		}

		return FilterReply.DENY;
	}
	
	private boolean containsMarker(Marker reference, String name) {
		if (reference == null) {
			return false;
		}

		return reference.contains(name);
	}
}
