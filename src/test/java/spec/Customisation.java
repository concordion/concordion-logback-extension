package spec;

import org.concordion.logback.LogMarkers;

public class Customisation extends BaseFixture {

	public void loginexample() {
		getLogger().debug(LogMarkers.step(), "Example Splitting");
		getLogger().debug("Example logged");
	}
}
