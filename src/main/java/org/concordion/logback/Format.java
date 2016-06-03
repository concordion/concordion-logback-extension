package org.concordion.logback;

public enum Format {
	/** Elements from Log pattern split into columns, string literals not supported */
	STRING,
	
	/** Log pattern used as is to build the log statement to add to the log file, string literals are supported */
	COLUMN;
}