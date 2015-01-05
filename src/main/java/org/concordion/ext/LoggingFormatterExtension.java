package org.concordion.ext;


import org.concordion.api.extension.ConcordionExtender;
import org.concordion.api.extension.ConcordionExtension;
import org.concordion.ext.loggingFormatter.LoggingFormatterSpecificationListener;

/**
 * Formats the footer of the Concordion specification to show a link to the log file that has been created for this test.<br><br>
 * By default this link leads to a log file viewer which attempts to format the log file for easier reading. For the log file 
 * viewer to work correctly the log file must contain the log level, if not switching the viewer off is advised.
 */
public class LoggingFormatterExtension implements ConcordionExtension {
	private boolean useLogFileViewer = true;
	
	public LoggingFormatterExtension() {
	}
	
	/**
	 * Constructor
	 * @param useLogFileViewer 
	 * 			Flag whether to show raw log file (false) or present the log file inside a log file viewer (true, default).
	 */
	public LoggingFormatterExtension(boolean useLogFileViewer) {
		this.useLogFileViewer = useLogFileViewer;
	}

	@Override
	public void addTo(final ConcordionExtender concordionExtender) {
		LoggingFormatterSpecificationListener listener = new LoggingFormatterSpecificationListener(useLogFileViewer);
		concordionExtender.withSpecificationProcessingListener(listener);
		concordionExtender.withBuildListener(listener);
	}
}
