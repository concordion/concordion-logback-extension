package org.concordion.logback.filter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Marker;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;

public class MarkerFilter extends Filter<ILoggingEvent> {
	private List<String> filterMarkers = new ArrayList<String>();
	private String threadName = null;

	protected FilterReply onMatch = FilterReply.NEUTRAL;
	protected FilterReply onMismatch = FilterReply.NEUTRAL;

	/**
	 * Appends an array of markers to the list of markers to filter by.
	 * 
	 * @param markers
	 */
	public void setMarkers(String[] markers) {
		if (markers == null) {
			return;
		}

		this.filterMarkers.addAll(Arrays.asList(markers));
	}

// TODO JUnit giving "Warning: Class 'org.concordion.logback.filter.MarkerFilter' contains multiple setters for the same property 'onMatch'." 
//// when this present
//	public void setOnMatch(String action) {
//		onMatch = FilterReply.valueOf(action);
//	}

	public void setOnMatch(FilterReply action) {
		onMatch = action;
	}

//	public void setOnMismatch(String action) {
//		onMismatch = FilterReply.valueOf(action);
//	}

	public void setOnMismatch(FilterReply action) {
		onMismatch = action;
	}
	/**
	 * Appends a marker to the list of markers to filter by.
	 * 
	 * @param marker
	 */
	public void setMarker(String marker) {
		if (marker == null) {
			return;
		}

		this.filterMarkers.add(marker);
	}

	public void clearMarkers() {
		this.filterMarkers.clear();
	}

	/**
	 * Sets the name of the thread to filter by.
	 * 
	 * @param name Thread name
	 */
	public void setThread(String name) {
		this.threadName = name;
	}

	@Override
	public FilterReply decide(ILoggingEvent event) {
		if (threadName != null && !threadName.isEmpty()) {
			if (!Thread.currentThread().getName().equals(threadName)) {
				return FilterReply.DENY;
			}

			if (filterMarkers.isEmpty()) {
				return FilterReply.NEUTRAL;
			}
		}
		
		for (String marker : filterMarkers) {
			if (containsMarker(event.getMarker(), marker)) {
				return onMatch;
			}
		}

		return onMismatch;
	}
	
	private boolean containsMarker(Marker reference, String name) {
		if (reference == null) {
			return false;
		}

		return reference.contains(name);
	}
}
