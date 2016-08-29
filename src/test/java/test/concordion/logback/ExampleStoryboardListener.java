package test.concordion.logback;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.concordion.logback.LoggingListener;
import org.concordion.slf4j.markers.ScreenshotMarker;
import org.slf4j.Marker;

import ch.qos.logback.classic.spi.ILoggingEvent;

public class ExampleStoryboardListener extends LoggingListener {
	ByteArrayOutputStream stream = new ByteArrayOutputStream();

	@Override
	protected void append(ILoggingEvent event) {
		try {
			StoryboardMarker marker = (StoryboardMarker) findMarker(event.getMarker(), "STORYBOARD");
			String title = marker.getTitle();
			
			if (containsMarker(event.getMarker(), "DATA")) {
				if (findMarker(event.getMarker(), "DATA") instanceof ScreenshotMarker) {
					stream.write(("STORYBOARD_SCREENSHOT: " + title).getBytes());
				}
			}

			if (containsMarker(event.getMarker(), "STORYBOARD_CONTAINER")) {
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

	@Override
	public Marker getThrowableCaughtMarker() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Marker getFailureReportedMarker() {
		// TODO Auto-generated method stub
		return null;
	}
}
