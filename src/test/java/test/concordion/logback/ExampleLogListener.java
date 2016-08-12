package test.concordion.logback;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.concordion.logback.LoggingListener;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Layout;

public class ExampleLogListener extends LoggingListener {
	private ByteArrayOutputStream stream = new ByteArrayOutputStream();
	private ByteArrayOutputStream consoleStream = new ByteArrayOutputStream();
	private Layout<ILoggingEvent> htmlLayout;
	private Layout<ILoggingEvent> consoleLayout;

	@Override
	protected void append(ILoggingEvent event) {
		try {
			if (consoleLayout != null) {
				consoleStream.write(consoleLayout.doLayout(event).getBytes());
			}
			
			if (htmlLayout != null) {
				stream.write(htmlLayout.doLayout(event).getBytes());
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

	public String getHtmlLog() {
		return stream.toString();
	}

	public String getConsoleLog() {
		return consoleStream.toString();
	}
	
	public void reset() {
		stream.reset();
		consoleStream.reset();
	}

	public void setHtmlLayout(Layout<ILoggingEvent> layout) {
		this.htmlLayout = layout;
	}

	public void setConsoleLayout(Layout<ILoggingEvent> layout) {
		this.consoleLayout = layout;
	}
}
