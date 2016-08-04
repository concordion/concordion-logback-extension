package test.concordion.logback;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;

public abstract class LoggingListener extends AppenderBase<ILoggingEvent> {

	public abstract String[] getFilterMarkers();
}
