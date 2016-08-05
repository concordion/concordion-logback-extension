package specification;

public class ClassicLog extends BaseFixture {
	
	public boolean canUseClassicLogger() {
		//TODO need to be able to pass in snippet AND need to configure classic logger
		return true;
	}

	public boolean hasLinkToLogFile() {
		// TODO use TestRig to get specification and get footer
		// TODO repeat for example link 
		return true;
	}

	public boolean useLogViewer() {
		//TODO how test?
		return true;
	}
}
