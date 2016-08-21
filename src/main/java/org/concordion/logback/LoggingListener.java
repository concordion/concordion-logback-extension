package org.concordion.logback;

import java.util.Iterator;

import org.slf4j.Marker;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;

//TODO Nigel: This would need to be made available to all extensions wanting to implement a listener,
//I'm hoping we can publish a tagged with a classifier as has been done with concordion tests
//ie: testCompile 'org.concordion:concordion:2.0.2:tests'.
public abstract class LoggingListener extends AppenderBase<ILoggingEvent> {

	public abstract String[] getFilterMarkers();
	
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
