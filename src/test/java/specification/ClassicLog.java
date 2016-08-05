package specification;

public class ClassicLog extends BaseFixture {
	
	public boolean canUseClassicLogger() {
		return false;
	}

	public boolean hasLinkToLogFile() {
		return false;
	}

	public boolean useLogViewer() {
		return false;
	}
}
