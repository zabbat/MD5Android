package net.wandroid.md5.model;

/**
 * Describes a triangle.
 * A triangle consists of 3 polygons.
 * 
 * @author Jungbeck
 *
 */
public class Triangle {
	private int mIndex; // this triangles index
	int mVertIndex[]; // the vertex indices

	/**
	 * Constructor for a triangle
	 * @param index index of the triangle
	 * @param i0 vertex index 0
	 * @param i1 vertex index 1
	 * @param i2 vertex index 2
	 */
	public Triangle(int index, int i0,int i1,int i2) {
		this.mIndex = index;
		this.mVertIndex = new int[]{i0,i1,i2};
	}
	
	@Override
	public String toString() {
		String s="tri "+mIndex+" "+mVertIndex[0]+" "+mVertIndex[1]+" "+mVertIndex[2];
		return s;
	}
}
