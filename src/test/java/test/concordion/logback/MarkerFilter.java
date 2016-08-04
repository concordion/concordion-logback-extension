package test.concordion.logback;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;

public class MarkerFilter extends Filter<ILoggingEvent> {
	String[] markers;

	public MarkerFilter(String... markers) {
		if (markers == null) {
			this.markers = new String[] {};
		} else {
			this.markers = markers;
		}
	}

	@Override
	public FilterReply decide(ILoggingEvent event) {
		if (event.getMarker() != null) {
			for (String marker : markers) {
				if (event.getMarker().contains(marker)) {
					return FilterReply.ACCEPT;
				}
			}
		}

		return FilterReply.DENY;
	}
}
