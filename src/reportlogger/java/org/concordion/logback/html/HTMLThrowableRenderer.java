package org.concordion.logback.html;

import static ch.qos.logback.core.CoreConstants.LINE_SEPARATOR;

/**
 * Logback: the reliable, generic, fast and flexible logging framework.
 * Copyright (C) 1999-2015, QOS.ch. All rights reserved.
 *
 * This program and the accompanying materials are dual-licensed under
 * either the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation
 *
 *   or (per the licensee's choosing)
 *
 * under the terms of the GNU Lesser General Public License version 2.1
 * as published by the Free Software Foundation.
 */

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.StackTraceElementProxy;
import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.helpers.Transform;
import ch.qos.logback.core.html.IThrowableRenderer;

public class HTMLThrowableRenderer implements IThrowableRenderer<ILoggingEvent> {

    static final String TRACE_PREFIX = "<br />&nbsp;&nbsp;&nbsp;&nbsp;";
	private int columnCount = 6;
	private int exceptionCount = 0;

    public void render(StringBuilder sbuf, ILoggingEvent event) {
		IThrowableProxy tp = event.getThrowableProxy();
		
		sbuf.append(LINE_SEPARATOR);
		sbuf.append("<tr class=\"companion\">");
		sbuf.append(LINE_SEPARATOR);
		sbuf.append("<td class=\"indent\"></td><td colspan=\"").append(columnCount).append("\" class=\"output exceptionMessage\">");

        while (tp != null) {
			exceptionCount++;
            render(sbuf, tp);
            tp = tp.getCause();
        }
        sbuf.append("</td></tr>");
    }

    void render(StringBuilder sbuf, IThrowableProxy tp) {
    	
		sbuf.append("<div>").append(CoreConstants.LINE_SEPARATOR);
		printFirstLine(sbuf, tp);
		sbuf.append(CoreConstants.LINE_SEPARATOR);
		sbuf.append("</div>").append(CoreConstants.LINE_SEPARATOR);

		sbuf.append("<input id=\"stackTraceButton").append(exceptionCount)
				.append("\" class=\"stackTraceButton\" type=\"button\" value=\"View Stack\" onclick=\"javascript:toggleStackTrace('").append(exceptionCount).append("')\"/>")
				.append(CoreConstants.LINE_SEPARATOR);
		sbuf.append("<div id=\"stackTrace").append(exceptionCount).append("\" class=\"stackTrace\" style=\"\">").append(CoreConstants.LINE_SEPARATOR);

        int commonFrames = tp.getCommonFrames();
        StackTraceElementProxy[] stepArray = tp.getStackTraceElementProxyArray();

        for (int i = 0; i < stepArray.length - commonFrames; i++) {
            StackTraceElementProxy step = stepArray[i];
			// sbuf.append(TRACE_PREFIX);
			sbuf.append("<div class=\"stackTraceEntry\">");
            sbuf.append(Transform.escapeTags(step.toString()));
			sbuf.append("</div>");
            sbuf.append(CoreConstants.LINE_SEPARATOR);
        }

        if (commonFrames > 0) {
            sbuf.append(TRACE_PREFIX);
            sbuf.append("\t... ").append(commonFrames).append(" common frames omitted").append(CoreConstants.LINE_SEPARATOR);
        }

		sbuf.append("</div>");

    }

    public void printFirstLine(StringBuilder sb, IThrowableProxy tp) {
        int commonFrames = tp.getCommonFrames();
        if (commonFrames > 0) {
            sb.append("<br />").append(CoreConstants.CAUSED_BY);
        }
		sb.append(tp.getClassName()).append(": ");
		sb.append(Transform.escapeTags(tp.getMessage()).replace("\r\n", "<br />").replace("\n", "<br />"));
        sb.append(CoreConstants.LINE_SEPARATOR);
    }

	public void setColumnCount(int columnCount) {
		this.columnCount  = columnCount;
	}

}
