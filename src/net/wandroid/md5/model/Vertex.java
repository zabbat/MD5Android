package net.wandroid.md5.model;

public class Vertex {
	int index;
	float s,t;
	int startWeight;
	int countWeight;

	public Vertex(int index, float s, float t, int startWeight, int countWeight) {
		this.index = index;
		this.s = s;
		this.t = t;
		this.startWeight = startWeight;
		this.countWeight = countWeight;
	}
	
	@Override
	public String toString() {
		String str="vert "+index +" ( "+s +" "+t+" ) " +startWeight +" "+ countWeight;
		return str;
	}
	
}
