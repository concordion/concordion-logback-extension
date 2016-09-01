package org.concordion.logback;

import java.util.Iterator;

import org.slf4j.Marker;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;

/**
 * Extension wishing to listen to the log events must extends this class.
 * 
 * @author Andrew Sumner
 */
public abstract class LoggingListener extends AppenderBase<ILoggingEvent> {

	/**
	 * Implement this to provide a list of markers that this listener wants to deal with.
	 * 
	 * This listener will then only receive logging events for those log messages that contain that marker.
	 * 
	 * @return A list of markers.
	 */
	public abstract String[] getFilterMarkers();

	/**
	 * Implement this to provide a marker that will be attached to any logging statements that the logging extension
	 * creates.
	 * 
	 * <p>
	 * For example if the logging extension was to log a screenshot on example completion then the provided
	 * marker will be attached to the log event.
	 * </p>
	 * 
	 * @return A marker
	 */
	public abstract Marker getConcordionEventMarker();

	protected Marker findMarker(Marker reference, String name) {
		if (reference == null) {
			return null;
		}

		if (reference.getName().equals(name)) {
			return reference;
		}
		
		Iterator<Marker> references = reference.iterator();
		while (references.hasNext()) {
			Marker found = findMarker(references.next(), name);
			
			if (found != null) {
				return found;
			}
		}
		
		return null;
	}
	
	protected boolean containsMarker(Marker reference, String name) {
		if (reference == null) {
			return false;
		}

		return reference.contains(name);
	}
}
