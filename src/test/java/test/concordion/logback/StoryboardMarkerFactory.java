package test.concordion.logback;

import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

public class StoryboardMarkerFactory {
	// TODO:
	// 1. Would need to support additional properties such as result.
	// 2. How do data? Screenshot is logged to separate file, data is embedded in log file...

	private static final Marker STORYBOARD = MarkerFactory.getMarker("STORYBOARD");

	public static StoryboardMarker storyboard(String title) {
		StoryboardMarker marker = new StoryboardMarker("STORYBOARD_DETAIL", title);
		marker.add(STORYBOARD);

		return marker;
	}
	
	public static StoryboardMarker container(String title) {
		StoryboardMarker marker = new StoryboardMarker("STORYBOARD_CONTAINER", title);
		marker.add(STORYBOARD);

		return marker;
	}
}

