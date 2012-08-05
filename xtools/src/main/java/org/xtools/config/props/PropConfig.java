package org.xtools.config.props;

public class PropConfig {
	private boolean valueTrim = true;
	private int spacesBesidesEqual = 1;
	private int spacesBeforeComment = 2;

	public boolean isValueTrim() {
		return valueTrim;
	}

	public void setValueTrim(boolean valueTrim) {
		this.valueTrim = valueTrim;
	}

	public int getSpacesBesidesEqual() {
		return spacesBesidesEqual;
	}

	public void setSpacesBesidesEqual(int spacesBesidesEqual) {
		this.spacesBesidesEqual = spacesBesidesEqual;
	}

	public int getSpacesBeforeComment() {
		return spacesBeforeComment;
	}

	public void setSpacesBeforeComment(int spacesBeforeComment) {
		this.spacesBeforeComment = spacesBeforeComment;
	}
	
	
	
}
