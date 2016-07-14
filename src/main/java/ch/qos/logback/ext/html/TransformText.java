package ch.qos.logback.ext.html;

/**
 * Utility class for transforming strings.
 * 
 * @author Andrew Sumner
 */
public class TransformText {
	/**
	 * This method takes a String which may contain special charactgers (ie, \t, \r, \n)
	 * and replaces these with the appropriate html tags
	 * 
	 * @param input
	 *            The text to be converted.
	 */
	public static String escapeText(final String input) {
		if (input == null || input.length() == 0) {
			return input;
		}
		StringBuffer buf = new StringBuffer(input);
		return escapeText(buf);
	}

	/**
	 * This method takes a StringBuilder which may contain special charactgers (ie, \t, \r, \n)
	 * and replaces these with the appropriate html tags.
	 * 
	 * @param buf StringBuffer to transform
	 * @return
	 */
	public static String escapeText(final StringBuffer buf) {
		for (int i = 0; i < buf.length(); i++) {
			char ch = buf.charAt(i);
			switch (ch) {
			case '\t':
				buf.replace(i, i + 1, "&nbsp;&nbsp;&nbsp;&nbsp;");
				break;
			case '\n':
				buf.replace(i, i + 1, "<br />");
				break;
			}
		}

		return buf.toString();
	}

}
