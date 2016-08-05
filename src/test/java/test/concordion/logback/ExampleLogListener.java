package test.concordion.logback;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Layout;

public class ExampleLogListener extends LoggingListener {
	private ByteArrayOutputStream stream = new ByteArrayOutputStream();
	// private LayoutWrappingEncoder<ILoggingEvent> encoder;
	// private HTMLLayout layout = new HTMLLayout();

	private Layout<ILoggingEvent> layout;

	public ExampleLogListener() {
		// layout.setContext((LoggerContext) LoggerFactory.getILoggerFactory());
		// layout.setFormat(Format.COLUMN.name());
		// layout.setPattern("%date{HH:mm:ss.SSS}%message%file%line");
		// layout.setPattern("%date{HH:mm:ss.SSS}%message%file%line");
		// layout.start();

		// encoder = new LayoutWrappingEncoder<ILoggingEvent>();
		// encoder.setLayout(layout);
		// encoder.start();
		//
		// try {
		// encoder.init(stream);
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		

	}

	@Override
	protected void append(ILoggingEvent event) {
		// This will give Single table row

		try {
			if (layout != null) {
				stream.write(layout.doLayout(event).getBytes());
			} else {
				stream.write(event.getFormattedMessage().getBytes());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		// This will generate table and header info
		// try {
		// encoder.setImmediateFlush(true);
		// encoder.doEncode(event);
		// } catch (IOException e1) {
		// e1.printStackTrace();
		// }
	}

	@Override
	public String[] getFilterMarkers() {
		return null;
	}

	public String getStreamContent() {
		return stream.toString();
	}

	public void resetStream() {
		stream.reset();
	}

	public void setLayout(Layout<ILoggingEvent> layout) {
		this.layout = layout;
	}
}
