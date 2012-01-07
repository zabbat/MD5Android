package net.wandroid.md5.model;

public class Hierarchy {

	protected String name;
	protected int parent;
	protected int flags;
	protected int startOffset;

	public Hierarchy(String name,int parent, int flags, int startOffset) {
		this.name = name;
		this.parent=parent;
		this.flags = flags;
		this.startOffset = startOffset;
	}
	
	@Override
	public String toString() {
		return name+" "+parent+" "+flags+" "+startOffset;
	}
	
}
