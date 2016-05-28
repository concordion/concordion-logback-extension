package spec;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import org.apache.commons.io.IOUtils;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.sift.SiftingAppender;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.FileAppender;
import ch.qos.logback.core.util.StatusPrinter;

public class HTMLLog extends AcceptanceTest {
	
	public boolean configuration() throws IOException {
		getLogger().debug("Hello World!");
		
		FileAppender<?> fileAppender = getHTMLAppender();
		
		if (fileAppender == null) {
			return false;
		}
		
//		Marker ss = MarkerFactory.getMarker("SCREENSHOT");
//		getLogger().debug(ss, "Hello World!");
		
		System.out.println(fileAppender.getFile());
		
		try (InputStream input = new FileInputStream(new File(fileAppender.getFile()))) {
			String log = IOUtils.toString(input);
			return log.contains("Hello World");
		}

		
	}
	
	private FileAppender<?> getHTMLAppender() {
		LoggerContext context = (LoggerContext)LoggerFactory.getILoggerFactory();
		for (Logger logger : context.getLoggerList())
		{
		     for (Iterator<Appender<ILoggingEvent>> index = logger.iteratorForAppenders(); index.hasNext();) {
		           Object enumElement = index.next();
		           if (enumElement instanceof SiftingAppender) {
		        	   SiftingAppender sift = (SiftingAppender)enumElement;
		        	   
		        	    if (sift.getName().equals("FILE-PER-TEST")) {
		        	    	for (Appender<?> appender : sift.getAppenderTracker().allComponents()) {
		        	    		if (appender instanceof FileAppender) {
		        	    			return (FileAppender<?>)appender;
		        	    		}
		        	    	}
		                }
		           }
		     }
		}
		
		return null;
	}
}
