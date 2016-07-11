package ch.qos.logback.ext.html;

import java.util.HashMap;
import java.util.Map;

import ch.qos.logback.classic.Level;

public class Icon {
	private static Map<Level, String> map;
	
	static {
		map = new HashMap<Level, String>();
		
		map.put(Level.ERROR, "fa fa-times-circle-o");
		map.put(Level.WARN, "fa fa-exclamation-circle");
		map.put(Level.INFO, "fa fa-info-circle");
		map.put(Level.DEBUG, "fa fa-bug");
		map.put(Level.TRACE, "fa fa-circle-o");
		map.put(Level.ERROR, "fa fa-times-circle-o");
		
        // link to log file? fa-eye
        //or? fa-question-circle-o
        // ? fa-file-text-o
//        fa-search
        //fa-ellipsis-h
	}
    
	public Icon() { }
	
    public static String getIcon(Level status) {
        if (map.containsKey(status)) {
            return map.get(status);
    	}

    	return "fa-ellipsis-h";
    }
}
