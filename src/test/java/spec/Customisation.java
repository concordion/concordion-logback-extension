package spec;

public class Customisation extends BaseFixture {

	public boolean splitexample() {
		getLogger().step("Example Splitting");
		getLogger().debug("Example logged");

		return true;
	}

	public void printlog() {
		getLogger().debug("do domething here");
	}
}
