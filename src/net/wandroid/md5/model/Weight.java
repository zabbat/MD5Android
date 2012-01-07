package net.wandroid.md5.model;

import net.wandroid.md5.model.math.Vec3;

public class Weight {

	int index;
	int joint;
	float bias;
	Vec3 pos;

	public Weight(int index, int joint, float bias, Vec3 pos) {
		this.index = index;
		this.joint = joint;
		this.bias = bias;
		this.pos = pos;
	}
	
	@Override 
	public String toString() {
		String s="weight "+ index+" "+joint+" "+bias+" ( "+pos.getX() + " "+pos.getY()+" "+pos.getZ()+" )";
		return s;
	}
	
}
