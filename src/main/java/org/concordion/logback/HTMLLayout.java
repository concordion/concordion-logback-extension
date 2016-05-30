package org.concordion.logback;

import java.io.OutputStream;
import java.util.Map;

import org.concordion.api.Element;
import org.slf4j.helpers.DataMarker;
import org.slf4j.helpers.ScreenshotMarker;

import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.pattern.MDCConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.html.DefaultCssBuilder;
import ch.qos.logback.classic.html.DefaultThrowableRenderer;
import ch.qos.logback.core.helpers.Transform;
import ch.qos.logback.core.html.HTMLLayoutBase;
import ch.qos.logback.core.html.IThrowableRenderer;
import ch.qos.logback.core.pattern.Converter;
import static ch.qos.logback.core.CoreConstants.LINE_SEPARATOR;

/**
 * 
 * HTMLLayout outputs events in an HTML table. <p> The content of the table
 * columns are specified using a conversion pattern. See
 * {@link ch.qos.logback.classic.PatternLayout} for documentation on the
 * available patterns. <p> For more information about this layout, please refer
 * to the online manual at
 * http://logback.qos.ch/manual/layouts.html#ClassicHTMLLayout
 * 
 * @author Ceki G&uuml;lc&uuml;
 * @author S&eacute;bastien Pennec
 */
public class HTMLLayout extends HTMLLayoutBase<ILoggingEvent> {
	int screenshotsTakenCount = 0;
	
    /**
     * Default pattern string for log output.
     */
    static final String DEFAULT_CONVERSION_PATTERN = "%date%thread%level%logger%mdc%msg";

    IThrowableRenderer<ILoggingEvent> throwableRenderer;

    /**
     * Constructs a PatternLayout using the DEFAULT_LAYOUT_PATTERN.
     * 
     * The default pattern just produces the application supplied message.
     */
    public HTMLLayout() {
        pattern = DEFAULT_CONVERSION_PATTERN;
        throwableRenderer = new DefaultThrowableRenderer();
        cssBuilder = new DefaultCssBuilder();
    }

    @Override
    public void start() {
        int errorCount = 0;
        if (throwableRenderer == null) {
            addError("ThrowableRender cannot be null.");
            errorCount++;
        }
        if (errorCount == 0) {
            super.start();
        }
    }

    protected Map<String, String> getDefaultConverterMap() {
        return PatternLayout.defaultConverterMap;
    }

    public String doLayout(ILoggingEvent event) {
        StringBuilder buf = new StringBuilder();
        startNewTableIfLimitReached(buf);

        boolean odd = true;
        if (((counter++) & 1) == 0) {
            odd = false;
        }

        String level = event.getLevel().toString().toLowerCase();

        buf.append(LINE_SEPARATOR);
        buf.append("<tr class=\"");
        buf.append(level);
        if (odd) {
            buf.append(" odd\">");
        } else {
            buf.append(" even\">");
        }
        buf.append(LINE_SEPARATOR);

        Converter<ILoggingEvent> c = head;
        while (c != null) {
            appendEventToBuffer(buf, c, event);
            c = c.getNext();
        }
        buf.append("</tr>");
        buf.append(LINE_SEPARATOR);

        if (event.getMarker() instanceof ScreenshotMarker) {
        	ScreenshotMarker sshot = (ScreenshotMarker) event.getMarker();
        	
        	buf.append(LINE_SEPARATOR);
        	buf.append("<tr><td  colspan=\"6\">");
        	
        	try {
        		buf.append("<img src=\"").append(sshot.writeScreenshot(screenshotsTakenCount)).append("\"/>");
        		screenshotsTakenCount ++;
    			
    		} catch (Exception e) {
    			buf.append(e.getMessage());
    		}
			
        	buf.append("</td></tr>");
        	buf.append(LINE_SEPARATOR);
        } 
        
        if (event.getMarker() instanceof DataMarker) {
        	DataMarker data = (DataMarker) event.getMarker();
        	
        	buf.append(LINE_SEPARATOR);
        	buf.append("<tr><td  colspan=\"6\">");
        	
        	try {
        		buf.append("<pre>");
        		buf.append(LINE_SEPARATOR);
        		buf.append(Transform.escapeTags(data.getData()));
        		buf.append("</pre>");
    		} catch (Exception e) {
    			buf.append(e.getMessage());
    		}
			
        	buf.append("</td></tr>");
        	buf.append(LINE_SEPARATOR);
        }
        
        if (event.getThrowableProxy() != null) {
            throwableRenderer.render(buf, event);
        }
        
        return buf.toString();
    }

    private void appendEventToBuffer(StringBuilder buf, Converter<ILoggingEvent> c, ILoggingEvent event) {
        buf.append("<td class=\"");
        buf.append(computeConverterName(c));
        buf.append("\">");
        buf.append(Transform.escapeTags(c.convert(event)));
        buf.append("</td>");
        buf.append(LINE_SEPARATOR);
    }

    public IThrowableRenderer getThrowableRenderer() {
        return throwableRenderer;
    }

    public void setThrowableRenderer(IThrowableRenderer<ILoggingEvent> throwableRenderer) {
        this.throwableRenderer = throwableRenderer;
    }

    @Override
    protected String computeConverterName(Converter c) {
        if (c instanceof MDCConverter) {
            MDCConverter mc = (MDCConverter) c;
            String key = mc.getFirstOption();
            if (key != null) {
                return key;
            } else {
                return "MDC";
            }
        } else {
            return super.computeConverterName(c);
        }
    }
}
