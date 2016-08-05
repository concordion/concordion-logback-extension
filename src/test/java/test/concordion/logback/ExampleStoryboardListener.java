package test.concordion.logback;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import ch.qos.logback.classic.spi.ILoggingEvent;

public class ExampleStoryboardListener extends LoggingListener {
	ByteArrayOutputStream stream = new ByteArrayOutputStream();

	@Override
	protected void append(ILoggingEvent event) {
		try {
			if (event.getMarker().contains("DATA")) {
				stream.write("FOUND MARKER STORYBOARD_SCREENSHOT".getBytes());
			}

			if (event.getMarker().contains("STORYBOARD_CONTAINER")) {
				stream.write("FOUND MARKER STORYBOARD_CONTAINER".getBytes());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String[] getFilterMarkers() {
		return new String[] { "STORYBOARD" };
	}

	public String getStreamContent() {
		return stream.toString();
	}

	public void resetStream() {
		stream.reset();
	}
}
