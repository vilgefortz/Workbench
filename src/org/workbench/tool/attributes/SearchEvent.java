package org.workbench.tool.attributes;

public class SearchEvent {
	Attribute source;
	String type;
	String value;
	public SearchEvent (String type,String value,Attribute source)  {
		this.type=type;
		this.value=value;
		this.source=source;
	}
	public Attribute getSource() {
		return source;
	}
	public String getType() {
		return type;
	}
	public String getValue() {
		return value;
	}
}
