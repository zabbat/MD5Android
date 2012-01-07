package net.wandroid.md5.model;

public class Triangle {
	private int index;
	int vertIndex[];

	public Triangle(int index, int i0,int i1,int i2) {
		this.index = index;
		this.vertIndex = new int[]{i0,i1,i2};
	}
	
	@Override
	public String toString() {
		String s="tri "+index+" "+vertIndex[0]+" "+vertIndex[1]+" "+vertIndex[2];
		return s;
	}
}
