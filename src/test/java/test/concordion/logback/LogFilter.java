package test.concordion.logback;

import org.slf4j.MDC;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;

public class LogFilter extends Filter<ILoggingEvent> {
	String[] markers;
	String mdcKey = null;
	String mdcValue = null;
	
	public LogFilter(String... markers) {
		if (markers == null) {
			this.markers = new String[] {};
		} else {
			this.markers = markers;
		}
	}

	public void setMDCKey(String mdcKey) {
		this.mdcKey = mdcKey;
	}

	public void setMDCValue(String mdcValue) {
		this.mdcValue = mdcValue;
	}

	@Override
	public FilterReply decide(ILoggingEvent event) {
		if (mdcKey != null && !mdcKey.isEmpty()) {
			if (!MDC.get(mdcKey).equals(mdcValue)) {
				return FilterReply.DENY;
			}
		}
		
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
