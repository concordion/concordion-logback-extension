package test.concordion.logback;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.concordion.logback.LoggingListener;
import org.slf4j.Marker;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Layout;

public class LayoutFormattedLogListener extends LoggingListener {
	private Layout<ILoggingEvent> layout;
	private ByteArrayOutputStream stream = new ByteArrayOutputStream();

	private Layout<ILoggingEvent> consoleLayout;
	private ByteArrayOutputStream consoleStream = new ByteArrayOutputStream();

	@Override
	protected void append(ILoggingEvent event) {
		try {
			if (consoleLayout != null) {
				consoleStream.write(consoleLayout.doLayout(event).getBytes());
			}
			
			if (layout != null) {
				stream.write(layout.doLayout(event).getBytes());
			} else {
				stream.write(event.getFormattedMessage().getBytes());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String[] getFilterMarkers() {
		return null;
	}

	public String getLog() {
		return stream.toString();
	}

	public String getConsoleLog() {
		return consoleStream.toString();
	}
	
	public void reset() {
		stream.reset();
		consoleStream.reset();
	}

	public void setLayout(Layout<ILoggingEvent> layout) {
		this.layout = layout;
	}

	public void setConsoleLayout(Layout<ILoggingEvent> layout) {
		this.consoleLayout = layout;
	}

	@Override
	public Marker getConcordionEventMarker() {
		return null;
	}

	@Override
	public boolean getHandleFailureAndThrowableEvents() {
		return true;
	}
}
