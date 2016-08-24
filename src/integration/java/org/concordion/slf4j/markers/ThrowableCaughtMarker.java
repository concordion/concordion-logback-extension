package org.concordion.slf4j.markers;

import org.slf4j.helpers.ConcordionMarker;

public class ThrowableCaughtMarker extends ConcordionMarker {
	private static final long serialVersionUID = 8750307001902436743L;

	private final Throwable throwable;

	public ThrowableCaughtMarker(Throwable throwable) {
		super(ReportLoggerMarkers.THROWABLE_CAUGHT_MARKER_NAME);

		this.throwable = throwable;
	}

	public Throwable getThrowable() {
		return throwable;
	}
}
