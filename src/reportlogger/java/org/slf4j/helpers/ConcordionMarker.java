package org.slf4j.helpers;

/**
 * Implements a reusable marker. Only required because SLF4Js BasicMarker constructor is not visible outside of the package org.slf4j.Marker.
 * 
 * @author Andrew Sumner
 */
public class ConcordionMarker extends BasicMarker {

	private static final long serialVersionUID = 6450408083626831104L;

	/***
	 * Note: Name must be distinct for each marker associated with a log entry, otherwise Logback will treat it as a duplicate ignore it.
	 * 
	 * @param name
	 */
	public ConcordionMarker(String name) {
		super(name);
	}

}
