package org.slf4j.helpers;

import ch.qos.logback.core.helpers.Transform;

public class DataMarker extends BaseDataMarker<DataMarker> {
	private static final long serialVersionUID = -3228456581564867488L;

	public DataMarker(String data) {
		super(data);
	}

	@Override
	public String getFormattedData() {
		return Transform.escapeTags(data);
	}

	@Override
	public void prepareData() {

	}
}