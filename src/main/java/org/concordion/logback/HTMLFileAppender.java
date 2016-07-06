package org.concordion.logback;

import ch.qos.logback.core.FileAppender;

public class HTMLFileAppender<E> extends FileAppender<E> {
	
	/*
	@Override
    protected void writeOut(E event) throws IOException {
        if (event instanceof LoggingEvent) {
        	if ((((LoggingEvent) event).getMarker()) instanceof ScreenshotMarker) {
        		ScreenshotMarker marker = (ScreenshotMarker) ((LoggingEvent) event).getMarker();
        				
        		marker.setOutputFolder(this.getFile());
        	}
        }
        
        super.writeOut(event);
    }
    */
}
