package org.concordion.logback;

import java.io.IOException;

import org.slf4j.helpers.ScreenshotMarker;

import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.FileAppender;

public class HTMLFileAppender<E> extends FileAppender<E> {
	private StepRecorder stepRecorder = StepRecorder.STEP_MARKER;
	
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
	
	@Override
	public void setLayout(ch.qos.logback.core.Layout<E> layout) {
		super.setLayout(layout);
		
		if (layout instanceof HTMLLayout) {
			((HTMLLayout) layout).setStepRecorder(stepRecorder);
		}
	}
	
	@Override
	public void setEncoder(ch.qos.logback.core.encoder.Encoder<E> encoder) {
		super.setEncoder(encoder);
		
		if (encoder instanceof ch.qos.logback.core.encoder.LayoutWrappingEncoder) {
			ch.qos.logback.core.encoder.LayoutWrappingEncoder<?> enc = (ch.qos.logback.core.encoder.LayoutWrappingEncoder<?>) encoder;
			
			if (enc.getLayout() instanceof HTMLLayout) {
				((HTMLLayout) enc.getLayout()).setStepRecorder(stepRecorder);
			}
		}
	}
	
	public void setStepRecorder(String value) {
		stepRecorder = StepRecorder.valueOf(value);
	}
}
