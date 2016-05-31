package spec;

public class ToolTipLog extends BaseFixture {
	
	public String writeToolTip(String text) {
		addConcordionTooltip(text);
		
		return text;
	}

}
