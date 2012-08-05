package org.xtools.config.props;

import java.util.ArrayList;
import java.util.List;

public class PropPart {
	private String name;
	private List<PropLine> lines = new ArrayList<PropLine>();
	
	public void addLine(PropLine line){
		this.lines.add(line);
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<PropLine> getLines() {
		return lines;
	}
	public void setLines(List<PropLine> lines) {
		this.lines = lines;
	}
	
	
}
