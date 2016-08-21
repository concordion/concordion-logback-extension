package org.slf4j.ext;

/**
 * TODO Is it ok to steal this code?
 * 
 * Represents an <a href="http://en.wikipedia.org/wiki/Internet_media_type">Internet Media Type</a>
 * (also known as a MIME Type or Content Type), a complete list can be found <a href="http://www.freeformatter.com/mime-types-list.html">here</a>.
 * 
 *  <p>This class has been copied from http://google.github.io/guava/ and trimmed down to the most relevant codes.  
 *  Only doing this as there is no convenient alternative in Java 1.6.</p>
 */
public final class MediaType {
	private static final String APPLICATION_TYPE = "application";
	private static final String AUDIO_TYPE = "audio";
	private static final String IMAGE_TYPE = "image";
	private static final String TEXT_TYPE = "text";
	private static final String VIDEO_TYPE = "video";

	private static final String WILDCARD = "*";

	private static MediaType createConstant(String type, String subtype) {
		return new MediaType(type, subtype);
	}

	private static MediaType createConstantUtf8(String type, String subtype) {
		return new MediaType(type, subtype);
	}

	public static final MediaType ANY_TYPE = createConstant(WILDCARD, WILDCARD);
	public static final MediaType ANY_TEXT_TYPE = createConstant(TEXT_TYPE, WILDCARD);
	public static final MediaType ANY_IMAGE_TYPE = createConstant(IMAGE_TYPE, WILDCARD);
	public static final MediaType ANY_AUDIO_TYPE = createConstant(AUDIO_TYPE, WILDCARD);
	public static final MediaType ANY_VIDEO_TYPE = createConstant(VIDEO_TYPE, WILDCARD);
	public static final MediaType ANY_APPLICATION_TYPE = createConstant(APPLICATION_TYPE, WILDCARD);

	/* text types */
	public static final MediaType CSS = createConstantUtf8(TEXT_TYPE, "css");
	public static final MediaType CSV = createConstantUtf8(TEXT_TYPE, "csv");
	public static final MediaType HTML = createConstantUtf8(TEXT_TYPE, "html");
	public static final MediaType PLAIN_TEXT = createConstantUtf8(TEXT_TYPE, "plain");
	public static final MediaType TEXT_JAVASCRIPT = createConstantUtf8(TEXT_TYPE, "javascript");
	public static final MediaType TSV = createConstantUtf8(TEXT_TYPE, "tab-separated-values");

	/**
	 * As described in <a href="http://www.ietf.org/rfc/rfc3023.txt">RFC 3023</a>, this constant
	 * ({@code text/xml}) is used for XML documents that are "readable by casual users."
	 * {@link #APPLICATION_XML} is provided for documents that are intended for applications.
	 */
	public static final MediaType XML = createConstantUtf8(TEXT_TYPE, "xml");

	/* image types */
	public static final MediaType BMP = createConstant(IMAGE_TYPE, "bmp");

	public static final MediaType GIF = createConstant(IMAGE_TYPE, "gif");
	public static final MediaType ICO = createConstant(IMAGE_TYPE, "vnd.microsoft.icon");
	public static final MediaType JPEG = createConstant(IMAGE_TYPE, "jpeg");
	public static final MediaType PNG = createConstant(IMAGE_TYPE, "png");
	public static final MediaType TIFF = createConstant(IMAGE_TYPE, "tiff");

	/* audio types */
	public static final MediaType MP4_AUDIO = createConstant(AUDIO_TYPE, "mp4");
	public static final MediaType MPEG_AUDIO = createConstant(AUDIO_TYPE, "mpeg");
	public static final MediaType OGG_AUDIO = createConstant(AUDIO_TYPE, "ogg");
	public static final MediaType WEBM_AUDIO = createConstant(AUDIO_TYPE, "webm");

	/* video types */
	public static final MediaType MP4_VIDEO = createConstant(VIDEO_TYPE, "mp4");
	public static final MediaType MPEG_VIDEO = createConstant(VIDEO_TYPE, "mpeg");
	public static final MediaType OGG_VIDEO = createConstant(VIDEO_TYPE, "ogg");
	public static final MediaType QUICKTIME = createConstant(VIDEO_TYPE, "quicktime");
	public static final MediaType WEBM_VIDEO = createConstant(VIDEO_TYPE, "webm");
	public static final MediaType WMV = createConstant(VIDEO_TYPE, "x-ms-wmv");

	/* application types */
	/**
	 * As described in <a href="http://www.ietf.org/rfc/rfc3023.txt">RFC 3023</a>, this constant
	 * ({@code application/xml}) is used for XML documents that are "unreadable by casual users."
	 * {@link #XML} is provided for documents that may be read by users.
	 */
	public static final MediaType APPLICATION_XML = createConstantUtf8(APPLICATION_TYPE, "xml");

	public static final MediaType JAVASCRIPT = createConstantUtf8(APPLICATION_TYPE, "javascript");
	public static final MediaType JSON = createConstantUtf8(APPLICATION_TYPE, "json");
	public static final MediaType MICROSOFT_EXCEL = createConstant(APPLICATION_TYPE, "vnd.ms-excel");
	public static final MediaType MICROSOFT_POWERPOINT = createConstant(APPLICATION_TYPE, "vnd.ms-powerpoint");
	public static final MediaType MICROSOFT_WORD = createConstant(APPLICATION_TYPE, "msword");
	public static final MediaType OCTET_STREAM = createConstant(APPLICATION_TYPE, "octet-stream");
	public static final MediaType OGG_CONTAINER = createConstant(APPLICATION_TYPE, "ogg");
	public static final MediaType OOXML_DOCUMENT = createConstant(APPLICATION_TYPE, "vnd.openxmlformats-officedocument.wordprocessingml.document");
	public static final MediaType OOXML_PRESENTATION = createConstant(APPLICATION_TYPE, "vnd.openxmlformats-officedocument.presentationml.presentation");
	public static final MediaType OOXML_SHEET = createConstant(APPLICATION_TYPE, "vnd.openxmlformats-officedocument.spreadsheetml.sheet");
	public static final MediaType OPENDOCUMENT_GRAPHICS = createConstant(APPLICATION_TYPE, "vnd.oasis.opendocument.graphics");
	public static final MediaType OPENDOCUMENT_PRESENTATION = createConstant(APPLICATION_TYPE, "vnd.oasis.opendocument.presentation");
	public static final MediaType OPENDOCUMENT_SPREADSHEET = createConstant(APPLICATION_TYPE, "vnd.oasis.opendocument.spreadsheet");
	public static final MediaType OPENDOCUMENT_TEXT = createConstant(APPLICATION_TYPE, "vnd.oasis.opendocument.text");
	public static final MediaType PDF = createConstant(APPLICATION_TYPE, "pdf");
	public static final MediaType POSTSCRIPT = createConstant(APPLICATION_TYPE, "postscript");
	public static final MediaType RTF = createConstantUtf8(APPLICATION_TYPE, "rtf");
	public static final MediaType XHTML = createConstantUtf8(APPLICATION_TYPE, "xhtml+xml");



	private final String mimeType;

	private MediaType(String type, String subtype) {
		this.mimeType = type + "/" + subtype;
	}

	@Override
	public String toString() {
		return mimeType;
	}
}
