package net.wandroid.md5.model;

import net.wandroid.md5.model.math.Quaternion;
import net.wandroid.md5.model.math.Vec3;

public class BaseFrame {
	protected Vec3[] pos;
	protected Quaternion[] q;
	
	public BaseFrame(int nrJoints) {
		pos=new Vec3[nrJoints];
		q=new Quaternion[nrJoints];
	}
}
