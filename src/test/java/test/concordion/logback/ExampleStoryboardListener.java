package test.concordion.logback;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.slf4j.Marker;

import ch.qos.logback.classic.spi.ILoggingEvent;

public class ExampleStoryboardListener extends LoggingListener {
	ByteArrayOutputStream stream = new ByteArrayOutputStream();

	@Override
	protected void append(ILoggingEvent event) {
		try {
			StoryboardMarker marker = (StoryboardMarker) findMarker(event, "STORYBOARD");
			String title = "";
			if (marker != null) {
				title = marker.getTitle();
			}
			
			if (event.getMarker().contains("SCREENSHOT")) {
				stream.write(("STORYBOARD_SCREENSHOT: " + title).getBytes());
			}

			if (event.getMarker().contains("STORYBOARD_CONTAINER")) {
				stream.write(("STORYBOARD_CONTAINER: " + title).getBytes());
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
