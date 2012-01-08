package net.wandroid.md5.model;

/**
 * Class that describes a vertex
 * @author Jungbeck
 *
 */
public class Vertex {
	protected int mIndex; // the vertex index
	protected float mS,mT; // the texture coordinates for the vertex
	protected int mStartWeight; // the start weight of this vertex
	protected int mCountWeight; // the number of weights used for this vertex

	
	public Vertex(int index, float s, float t, int startWeight, int countWeight) {
		this.mIndex = index;
		this.mS = s;
		this.mT = t;
		this.mStartWeight = startWeight;
		this.mCountWeight = countWeight;
	}
	
	@Override
	public String toString() {
		String str="vert "+mIndex +" ( "+mS +" "+mT+" ) " +mStartWeight +" "+ mCountWeight;
		return str;
	}
	
}
