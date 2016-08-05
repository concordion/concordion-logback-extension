package test.concordion.logback;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Layout;

public class ExampleLogListener extends LoggingListener {
	private ByteArrayOutputStream stream = new ByteArrayOutputStream();
	private Layout<ILoggingEvent> layout;

	@Override
	protected void append(ILoggingEvent event) {
		try {
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

	public String getStreamContent() {
		return stream.toString();
	}

	public void resetStream() {
		stream.reset();
	}

	public void setLayout(Layout<ILoggingEvent> layout) {
		this.layout = layout;
	}
}
