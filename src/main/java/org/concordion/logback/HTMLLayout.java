package org.concordion.logback;

// TODO: Expand/Collapse button for Data; Copy Text Button for Data; Link = Eye on example.

import static ch.qos.logback.core.CoreConstants.LINE_SEPARATOR;

import java.awt.Dimension;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.concordion.ext.loggingFormatter.LogbackAdaptor;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.DataMarker;
import org.slf4j.helpers.ScreenshotMarker;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.pattern.MDCConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.helpers.Transform;
import ch.qos.logback.core.html.HTMLLayoutBase;
import ch.qos.logback.core.html.IThrowableRenderer;
import ch.qos.logback.core.pattern.Converter;

/**
 * 
 * HTMLLayout outputs events in an HTML table.
 * <p>
 * The content of the table
 * columns are specified using a conversion pattern. See
 * {@link ch.qos.logback.classic.PatternLayout} for documentation on the
 * available patterns.
 * <p>
 * For more information about this layout, please refer
 * to the online manual at
 * http://logback.qos.ch/manual/layouts.html#ClassicHTMLLayout
 * 
 * @author Andrew Sumner
 */
public class HTMLLayout extends HTMLLayoutBase<ILoggingEvent> {
    /**
     * Default pattern string for log output.
     */
    static final String DEFAULT_CONVERSION_PATTERN = "%date{HH:mm:ss.SSS}%logger{30}%level%message";

    private IThrowableRenderer<ILoggingEvent> throwableRenderer;
    private StepRecorder stepRecorder = StepRecorder.STEP_MARKER;
	private Format format = Format.COLUMN;
	private int screenshotsTakenCount = 0;
	private int columnCount;
	private PatternLayout stringLayout = null;
	private String stylesheet = "";
	
    /**
     * Constructs a PatternLayout using the DEFAULT_LAYOUT_PATTERN.
     * 
     * The default pattern just produces the application supplied message.
     */
    public HTMLLayout() {
        pattern = DEFAULT_CONVERSION_PATTERN;
        throwableRenderer = new HTMLThrowableRenderer();
		cssBuilder = null;
        columnCount = getColumnCount();
    }

    public void setStepRecorder(String value) {
		stepRecorder = StepRecorder.valueOf(value);
	}

	public void setFormat(String value) {
		format = Format.valueOf(value);
	}
	
	public void setStylesheet(String value) {
		stylesheet = value;
	}
	
	public boolean hasStylesheet() {
		if (stylesheet == null || stylesheet.isEmpty()) {
			LoggerContext context = (LoggerContext)LoggerFactory.getILoggerFactory();
			stylesheet = context.getProperty(LogbackAdaptor.LAYOUT_STYLESHEET);
		}
		
		return stylesheet != null && !stylesheet.isEmpty();
	}
	
    @Override
    public void setPattern(String conversionPattern) {
    	super.setPattern(conversionPattern);
    	columnCount = getColumnCount();
    };
    
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

		if (containsMarker(event, LogMarkers.STEP) || event.getLevel() == stepRecorder.getLevel()) {
			appendStepToBuffer(buf, event);
        	return buf.toString();
        }
        
		appendMessageToBuffer(buf, event);
        
        if (event.getMarker() instanceof ScreenshotMarker) {
			appendScreenshotToBuffer(buf, (ScreenshotMarker) event.getMarker());
        } 
        
        if (event.getMarker() instanceof DataMarker) {
			appendDataToBuffer(buf, (DataMarker) event.getMarker());
        }
        
        if (event.getThrowableProxy() != null) {
        	if (throwableRenderer instanceof HTMLThrowableRenderer) {
        		((HTMLThrowableRenderer) throwableRenderer).setColumnCount(columnCount);
        	}
        		
            throwableRenderer.render(buf, event);
        }
        
        return buf.toString();
    }

	public void appendStepToBuffer(StringBuilder buf, ILoggingEvent event) {
		counter = 0;

		buf.append(LINE_SEPARATOR);
		buf.append("<tr class=\"step\">");
        buf.append(LINE_SEPARATOR);
		buf.append("<td colspan=\"").append(columnCount + 1).append("\">");
        
		if (event.getMarker() instanceof DataMarker) {
			buf.append(event.getMessage());
		} else {
			buf.append(Transform.escapeTags(event.getMessage()));
		}
        
        buf.append("</td>");
		buf.append(LINE_SEPARATOR);
		buf.append("</tr>");
	}
	
	private void appendMessageToBuffer(StringBuilder buf, ILoggingEvent event) {
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
		buf.append("<td class=\"indent ").append(event.getLevel().toString().toLowerCase()).append("\">");
		buf.append("<i class=\"").append(Icon.getIcon(event.getLevel())).append("\"></i>");
		buf.append("</td>");
    
		Converter<ILoggingEvent> c = head;
		if (format == Format.COLUMN) {
			while (c != null) {
				appendEventToBuffer(buf, c, event);
				c = c.getNext();
			}
		} else {
			buf.append("<td>");
			
			if (stringLayout == null) {
				stringLayout = new PatternLayout();
				stringLayout.setPattern("%nopex" + this.getPattern());
				stringLayout.setContext(this.getContext());
				stringLayout.start();
			}
			
			String text = stringLayout.doLayout(event);

			if (event.getMarker() instanceof DataMarker) {
				buf.append(text);
			} else {
				buf.append(Transform.escapeTags(text));
			}
			buf.append("</td>");
        }
        
        buf.append("</tr>");
	}

	private void appendEventToBuffer(StringBuilder buf, Converter<ILoggingEvent> c, ILoggingEvent event) {
        buf.append("<td class=\"");
        buf.append(computeConverterName(c));
        buf.append("\">");
		if (event.getMarker() instanceof DataMarker) {
			buf.append(c.convert(event));
		} else {
			buf.append(Transform.escapeTags(c.convert(event)));
		}
        buf.append("</td>");
        buf.append(LINE_SEPARATOR);
    }
	
	public void appendScreenshotToBuffer(StringBuilder buf, ScreenshotMarker screenshot) {
		buf.append(LINE_SEPARATOR);
		buf.append("<tr>");
		buf.append(LINE_SEPARATOR);
		buf.append("<td class=\"indent\"></td><td align=\"center\" colspan=\"").append(columnCount).append("\" class=\"data\">");
        
		try {
			// TODO Nigel: should we pass screenshot taker in once via property or every time via Marker?
			// * I'd like to not pass via marker so less code when log screenshot...
			// * But what about multiple browsers?

			// ScreenshotTaker taker = LogbackAdaptor.getScreenshotTaker();

			screenshot.writeScreenshot(screenshotsTakenCount);
			screenshotsTakenCount++;

			buf.append("<a href=\"").append(screenshot.getFileName()).append("\">");
			buf.append("<img");
			buf.append(" src=\"").append(screenshot.getFileName()).append("\"");
			buf.append(" onMouseOver=\"showScreenPopup(this);this.style.cursor='pointer'\"");
			buf.append(" onMouseOut=\"hideScreenPopup();this.style.cursor='default'\"");

			Dimension imageSize = screenshot.getImageSize();

			if (imageSize.width * 1.15 > imageSize.height) {
				int displaySize = 350;

				if (imageSize.width < displaySize) {
					displaySize = imageSize.width;
				}

				buf.append(" width=\"").append(displaySize).append("px\" ");
				buf.append(" class=\"");
				buf.append("sizewidth");
				buf.append("\"");
			} else {
				int displaySize = 200;

				if (imageSize.height < displaySize) {
					displaySize = imageSize.height;
				}

				buf.append(" height=\"").append(displaySize).append("px\" ");
				buf.append(" class=\"");
				buf.append("sizeheight");
				buf.append("\"");
			}

			buf.append("/>");
			buf.append("</a>");

		} catch (Exception e) {
			buf.append(e.getMessage());
		}

		buf.append("</td>");
		buf.append(LINE_SEPARATOR);
		buf.append("</tr>");
	}

	public void appendDataToBuffer(StringBuilder buf, DataMarker data) {
		if (!data.hasData()) {
			return;
		}

		buf.append(LINE_SEPARATOR);
		buf.append("<tr>");
		buf.append(LINE_SEPARATOR);
		buf.append("<td class=\"indent\"></td><td colspan=\"").append(columnCount).append("\" class=\"data\">");
		
		try {
			buf.append(LINE_SEPARATOR);
			buf.append("<pre>");

			if (data.escapeData()) {
				buf.append(Transform.escapeTags(data.getData()));
			} else {
				buf.append(data.getData());
			}
			
			buf.append("</pre>");
			buf.append(LINE_SEPARATOR);
		} catch (Exception e) {
			buf.append(e.getMessage());
		}
		
		buf.append("</td>");
		buf.append(LINE_SEPARATOR);
		buf.append("</tr>");
	}

	public IThrowableRenderer<?> getThrowableRenderer() {
        return throwableRenderer;
    }

    public void setThrowableRenderer(IThrowableRenderer<ILoggingEvent> throwableRenderer) {
        this.throwableRenderer = throwableRenderer;
    }

    @Override
	protected String computeConverterName(@SuppressWarnings("rawtypes") Converter c) {
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

    @Override
	public String getFileHeader() {
		StringBuilder sbuf = new StringBuilder();
		sbuf.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\"");
		sbuf.append(" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">");
		sbuf.append(LINE_SEPARATOR);
		sbuf.append("<html>");
		sbuf.append(LINE_SEPARATOR);
		sbuf.append("  <head>");
		sbuf.append(LINE_SEPARATOR);
		sbuf.append("    <title>").append(title).append("</title>").append(LINE_SEPARATOR);
		if (hasStylesheet()) {
			sbuf.append("    <link rel=\"stylesheet\" type=\"text/css\" href=\"").append(stylesheet).append("\"/>").append(LINE_SEPARATOR);
		}
		
		sbuf.append(readFile("htmllog.css"));
		sbuf.append(readFile("htmllog.js"));
		// cssBuilder.addCss(sbuf);

		sbuf.append(LINE_SEPARATOR);
		sbuf.append("  </head>");
		sbuf.append(LINE_SEPARATOR);
		sbuf.append("<body>");
		sbuf.append(LINE_SEPARATOR);

		// Required for screenshot popup
		sbuf.append("<img id=\"ScreenshotPopup\" class=\"screenshot\" />");

		return sbuf.toString();
	}

	@Override
    public String getPresentationHeader() {
        StringBuilder sbuf = new StringBuilder();
//        sbuf.append("<hr/>");
//        sbuf.append(LINE_SEPARATOR);
        sbuf.append("<p>Log session start time ");
        sbuf.append(new java.util.Date());
        sbuf.append("</p><p></p>");
        sbuf.append(LINE_SEPARATOR);
        sbuf.append(LINE_SEPARATOR);
        sbuf.append("<table cellspacing=\"0\">");
        sbuf.append(LINE_SEPARATOR);

        buildHeaderRowForTable(sbuf);

        return sbuf.toString();
    }

    private void buildHeaderRowForTable(StringBuilder sbuf) {
		Converter<?> c = head;
        String name;
		sbuf.append("<tr class=\"header\"><td style=\"width:50px\">Step</td>");
        sbuf.append(LINE_SEPARATOR);
        
        if (format == Format.COLUMN) {
	        while (c != null) {
	            name = computeConverterName(c);
	            if (name == null) {
	                c = c.getNext();
	                continue;
	            }
	            // sbuf.append("<td class=\"").append(name).append("\">");
	            sbuf.append("<td>");
	            sbuf.append(name.replaceAll("(.)([A-Z])", "$1&nbsp;$2"));
	            sbuf.append("</td>");
	            sbuf.append(LINE_SEPARATOR);
	            c = c.getNext();
	        }
        } else {
			sbuf.append("<td>Message</td>");
        }
        
        sbuf.append("</tr>");
        sbuf.append(LINE_SEPARATOR);
    }

	private boolean containsMarker(ILoggingEvent event, String name) {
		if (event.getMarker() == null) {
			return false;
		}
		
		return event.getMarker().contains(name);
	}
	
	private int getColumnCount() {
		if (format == Format.COLUMN) {
			return pattern.length() - pattern.replace("%", "").length();
		} else {
			return 1;
		}
	}

	public static String readFile(String filename) {
		InputStream input = null;

		try {
			input = HTMLLayout.class.getResourceAsStream(filename);
			if (input != null) {
				return IOUtils.toString(input, StandardCharsets.UTF_8.name());
			}
		} catch (Exception e) {
			System.err.println(e.getMessage());
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					System.err.println(e.getMessage());
				}
			}
		}

		return null;
	}
}
