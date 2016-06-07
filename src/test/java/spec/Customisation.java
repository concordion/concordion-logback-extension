package spec;

import org.concordion.logback.LogMarkers;

public class Customisation extends BaseFixture {

	public boolean splitexample() {
		getLogger().debug(LogMarkers.step(), "Example Splitting");
		getLogger().debug("Example logged");

		return true;
	}

	public void printlog() {
		getLogger().debug("do domething here");
	}
}
