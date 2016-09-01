package org.concordion.logback.html;

import ch.qos.logback.classic.Level;

public enum StepRecorder {
	/** Must supply step marker to log steps */
	STEP_MARKER(Level.ALL), 
	
	/** All INFO level log statements will be treated as steps */
	INFO_LOG_LEVEL(Level.INFO), 
	
	/** All BEBUG level log statements will be treated as steps */
	DEBUG_LOG_LEVEL(Level.DEBUG);
	
	private Level level;
	
	private StepRecorder (Level level) {
		this.level = level;
	}
	
	public Level getLevel() {
		return level;
	}
}