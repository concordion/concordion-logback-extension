package org.concordion.slf4j.markers;

import static ch.qos.logback.core.CoreConstants.LINE_SEPARATOR;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;

import org.slf4j.MDC;

public class AttachmentMarker extends BaseDataMarker<AttachmentMarker> {
	private static final long serialVersionUID = 5412731321120168078L;
	private static final String NEXT_FILE_NUMBER = "NEXT_FILE_NUMBER";

	private final String logFile;
	private final InputStream stream;
	private final String filename;
	private final String type;
	
	public AttachmentMarker(String logFile, InputStream stream, String filename, String type) {
		super("");

		this.logFile = logFile;
		this.stream = stream;
		this.filename = filename;
		this.type = type;
	}

	public String getFile() {
		return data;
	}

	@Override
	public String getFormattedData() {
		StringBuilder buf = new StringBuilder();

		buf.append("<div class=\"attachmentMenu\">").append(LINE_SEPARATOR);
		buf.append("<a href=\"").append(data).append("\" target=\"_blank\">Open</a>&nbsp;&nbsp;").append(LINE_SEPARATOR);
		buf.append("<a href=\"#\" onclick=\"toggleContent(this); return false;\">Expand</a>").append(LINE_SEPARATOR);
		buf.append("</div>").append(LINE_SEPARATOR);

		buf.append("<div class=\"resizeable\">");
		buf.append(LINE_SEPARATOR);

		// XML files are not handled by the object tag, so use the XMP tag for this. Doing same for other text based files
		// on the off-chance they have greater/less than characters
		boolean useXMP = (type.contains("text") || type.contains("xml") || type.contains("json") || type.contains("javascript"));
		if (useXMP) {
			// The format of this needs to remain in sync with DataMarker
			buf.append("<xmp class=\"fadeout\">");

			try {
				stream.reset();

				final char[] buffer = new char[1024];
				Reader in = new InputStreamReader(stream, "UTF-8");
				int len = in.read(buffer, 0, buffer.length);
				while (len != -1) {
					buf.append(buffer, 0, len);
					len = in.read(buffer, 0, buffer.length);
				}
			} catch (IOException e) {
				// Revert to using object tag
				buf.append(e.getMessage());
			}

			buf.append("</xmp>");
		} else {

			// TODO Do we want auto resizing of attachment?
			// <script language="javascript" type="text/javascript">
			// function resizeIframe(obj) {
			// var height = obj.contentWindow.document.body.scrollHeight;
			// if (height > 200) height = 200;
			//
			// obj.style.height = 0;
			// obj.style.height = height + 'px';
			// }
			// </script>
			// <a href="test.txt">Open File</a>
			// <object width="100%" height="50" type="text/plain" data="test.txt" border="1" onload="resizeIframe(this)"><a href="test.txt">test.txt</a></object>
			//
			//
			buf.append("<object");
			buf.append(" type=\"").append(type).append("\"");
			buf.append(" data=\"").append(data).append("\"");
			buf.append(">");
			buf.append("</object>");
		}
		
		buf.append(LINE_SEPARATOR);
		buf.append("</div>");

		return buf.toString();
	}

	@Override
	public void prepareData() {
		try {
			writeStream();

		} catch (Exception e) {
			data = e.getMessage();
		}
	}

	public void writeStream() throws IOException {
		int fileNumber = getNextFileNumber();
		String baseFile = getBaseFilename();

		OutputStream outputStream = null;
		File targetFile = new File(buildFileName(baseFile, fileNumber));

		try {
			outputStream = new FileOutputStream(targetFile);
			
			byte[] buffer = new byte[1024];
			int len = stream.read(buffer);
			while (len != -1) {
				outputStream.write(buffer, 0, len);
			    len = stream.read(buffer);
			}
			
			this.data = targetFile.getName();
		} finally {
			if (outputStream != null) {
				outputStream.close();
			}
		}
	}

	private int getNextFileNumber() {
		int nextNumber = -1;
		String next = MDC.get(NEXT_FILE_NUMBER);

		if (next != null && !next.isEmpty()) {
			nextNumber = Integer.valueOf(next);
		}

		nextNumber++;
		
		MDC.put(NEXT_FILE_NUMBER, String.valueOf(nextNumber));

		return nextNumber;
	}

	private String getBaseFilename() {
		int pos = logFile.lastIndexOf('.');

		if (pos > 0) {
			return logFile.substring(0, pos);
		} else {
			return logFile;
		}
	}

	private String buildFileName(String baseFile, int index) {
		return String.format("%sScreenShot%s-%s", baseFile, index, filename);
	}
}