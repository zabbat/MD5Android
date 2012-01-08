package net.wandroid.md5.model;

import net.wandroid.md5.model.math.Quaternion;
import net.wandroid.md5.model.math.Vec3;

/**
 * Class describing a base frame.
 * A base frame contains a skeleton, that other frames modify during animation
 * @author Jungbeck
 *
 */
public class BaseFrame {
	protected Vec3[] mPos;// position of the joints
	protected Quaternion[] mQ; // quaternions of the joints
	
	public BaseFrame(int nrJoints) {
		mPos=new Vec3[nrJoints];
		mQ=new Quaternion[nrJoints];
	}
}
