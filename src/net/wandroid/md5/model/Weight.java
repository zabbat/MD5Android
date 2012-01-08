package net.wandroid.md5.model;

import net.wandroid.md5.model.math.Vec3;

/**
 * A class describing a Weight
 * @author Jungbeck
 *
 */
public class Weight {

	int mIndex; // weight index
	int mJoint; // the joint index of this weight
	float mBias; // // the bias of the weight
	Vec3 mPos; // the position of the weight

	/**
	 * Constructor for a Weight
	 * @param index index of the weight
	 * @param joint joint index for the weight
	 * @param bias bias of the weight
	 * @param pos position of the weight
	 */
	public Weight(int index, int joint, float bias, Vec3 pos) {
		this.mIndex = index;
		this.mJoint = joint;
		this.mBias = bias;
		this.mPos = pos;
	}
	
	@Override 
	public String toString() {
		String s="weight "+ mIndex+" "+mJoint+" "+mBias+" ( "+mPos.getX() + " "+mPos.getY()+" "+mPos.getZ()+" )";
		return s;
	}
	
}
