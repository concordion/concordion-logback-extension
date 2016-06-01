package org.concordion.logback;

public enum StepRecorder {
	/** Must supply step marker to log steps */
	STEP_MARKER, 
	
	/** All INFO level log statements will be treated as steps */
	INFO_LOG_LEVEL, 
	
	/** All BEBUG level log statements will be treated as steps */
	DEBUG_LOG_LEVEL
}