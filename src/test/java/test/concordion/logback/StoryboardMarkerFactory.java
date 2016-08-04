package test.concordion.logback;

import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

public class StoryboardMarkerFactory {
	// TODO:
	// 1. Would need to support additional properties such as result.
	// 2. How do data? Screenshot is logged to separate file, data is embedded in log file...

	private static final Marker mark = MarkerFactory.getMarker("STORYBOARD");

	public static StoryboardMarker screenshot(String title) {
		StoryboardMarker marker = new StoryboardMarker("STORYBOARD_SCREENSHOT", title);
		marker.add(mark);

		return marker;
	}

	public static StoryboardMarker container(String title) {
		StoryboardMarker marker = new StoryboardMarker("STORYBOARD_CONTAINER", title);
		marker.add(mark);

		return marker;
	}
}

