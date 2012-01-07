package net.wandroid.md5.model;

import android.util.Pair;
import net.wandroid.md5.model.math.Quaternions;
import net.wandroid.md5.model.math.Vec3;

public class Frame {


	protected Joint[] joints;
	
	public Frame(float[] frameValues,int numJoints,Hierarchy[] hierarchy) {
		
		joints=new Joint[numJoints];
		
		for(int i=0;i<joints.length;i++){
			joints[i]=new Joint(hierarchy[i].name, hierarchy[i].parent, 
					new Vec3(frameValues[i*6+0], frameValues[i*6+1], frameValues[i*6+2]), 
					new Quaternions(frameValues[i*6+3],frameValues[i*6+4],frameValues[i*6+5]));
		}
		
	}
	
	
	public void calculateJointsPositionRelativeParent(){
		for(Joint j:joints){
			calculatePositionRelativeParent(joints,j);
		}
	}
	
	private void calculatePositionRelativeParent(Joint[] joints,Joint j){
		Pair<Quaternions, Vec3> pair=mulByParent(joints, j);
		j.q=pair.first;
		j.position=pair.second;
	
	}
	
	
	private Pair<Quaternions, Vec3> mulByParent(Joint[] joints, Joint j){
		if(j.parent==-1){
			//Log.v("rec",j.name+" has no parents");
			return new Pair<Quaternions, Vec3>(j.q, j.position); 
		}else{
			//Log.v("rec",""+j.name+" has parent "+joints[j.parent]);
			Vec3 rot_pos=joints[j.parent].q.rotate(j.position); //quat_rot(parent.orient,anim.posiiton)
			Vec3 newPos=joints[j.parent].position.add(rot_pos);
			Quaternions newQuat=joints[j.parent].q.mul(j.q);
			newQuat.normalize();
			return new Pair<Quaternions, Vec3>(newQuat, newPos);
		}
	}
	
}
