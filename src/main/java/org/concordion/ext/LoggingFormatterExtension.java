package org.concordion.ext;


import org.concordion.api.extension.ConcordionExtender;
import org.concordion.api.extension.ConcordionExtension;
import org.concordion.ext.loggingFormatter.LoggingFormatterSpecificationListener;

/**
 * Formats the footer of the Concordion output HTML to show a link to the log file that has been created for this test.
 */
public class LoggingFormatterExtension implements ConcordionExtension {

	@Override
	public void addTo(final ConcordionExtender concordionExtender) {
		LoggingFormatterSpecificationListener listener = new LoggingFormatterSpecificationListener();
		concordionExtender.withSpecificationProcessingListener(listener);
		concordionExtender.withBuildListener(listener);
	}
}
