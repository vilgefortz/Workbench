package org.workbench.tool;

public class ToolSetting {
	boolean changeable=true;
	String name = "";
	boolean necessary=false;
	String type = "";
	private boolean searchable; 
	
	public void setSearchable (boolean searchable) {
		this.searchable=searchable;
	}
	public String getName() {
		return name;
	}
	public String getType() {
		return type;
	}
	public boolean isChangeable() {
		return changeable;
	}
	public boolean isNecessary() {
		return necessary;
	}
	public void setChangeable(boolean changeAble) {
		this.changeable = changeAble;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setNecessary(boolean necesarry) {
		this.necessary = necesarry;
	}
	public void setType(String type) {
		this.type = type;
	}
	public boolean isSearchable() {
		return searchable;
	}
	
}
