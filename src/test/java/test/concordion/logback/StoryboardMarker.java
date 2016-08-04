package test.concordion.logback;

import org.slf4j.helpers.ConcordionMarker;

public class StoryboardMarker extends ConcordionMarker {
	private static final long serialVersionUID = 9114866777936384119L;

	private String title;

	public StoryboardMarker(String name, String title) {
		super(name);
	}

	public String getTitle() {
		return title;
	}
}