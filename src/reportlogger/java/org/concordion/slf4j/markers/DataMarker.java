package org.concordion.slf4j.markers;

import ch.qos.logback.core.helpers.Transform;

public class DataMarker extends BaseDataMarker<DataMarker> {
	private static final long serialVersionUID = -3228456581564867488L;

	public DataMarker(String data) {
		super(data);
	}

	@Override
	public String getFormattedData() {
		return "<xmp>" + Transform.escapeTags(data) + "</xmp>";
	}

	@Override
	public void prepareData() {

	}
}