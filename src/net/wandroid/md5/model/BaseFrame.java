package net.wandroid.md5.model;

import net.wandroid.md5.model.math.Quaternions;
import net.wandroid.md5.model.math.Vec3;

public class BaseFrame {
	protected Vec3[] pos;
	protected Quaternions[] q;
	
	public BaseFrame(int nrJoints) {
		pos=new Vec3[nrJoints];
		q=new Quaternions[nrJoints];
	}
}
