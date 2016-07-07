package org.slf4j.helpers;

public class HtmlMarker extends BaseDataMarker<HtmlMarker> {
	private static final long serialVersionUID = 5412731321120168078L;
	
	public HtmlMarker(String html) {
		super(html);
	}

	@Override
	public String getFormattedData() {
		return data;
	}
}