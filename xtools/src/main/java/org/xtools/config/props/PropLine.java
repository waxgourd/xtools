package org.xtools.config.props;

public class PropLine {
	private LineType lineType;
	private String key;
	private String value;
	private String commont;
	
	public PropLine(){
		
	}
	
	public PropLine(LineType lineType){
		this.lineType = lineType;
	}
	
	public PropLine(String key, String value, String commont){
		this.lineType = LineType.CommonLine;
		this.key = key;
		this.value = value;
		this.commont = commont;
	}

	public static enum LineType{
		EmptyLine,
		SectionLine,
		CommentLine,
		CommonLine
	}

	public LineType getLineType() {
		return lineType;
	}

	public void setLineType(LineType lineType) {
		this.lineType = lineType;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getCommont() {
		return commont;
	}

	public void setCommont(String commont) {
		this.commont = commont;
	}
}


