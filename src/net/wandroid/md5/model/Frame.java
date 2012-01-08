package net.wandroid.md5.model;

import android.util.Pair;
import net.wandroid.md5.model.math.Quaternion;
import net.wandroid.md5.model.math.Vec3;

/**
 * Describes a frame in an animation. A frame will change the base frame
 * @author Jungbeck
 *
 */
public class Frame {
    private static final int FRAME_NR_FLOAT_VALUES=6;// number of float values in a joint

	protected Joint[] mJoints; // joints of the frame
	
	/**
	 * Constructor for a frame
	 * @param frameValues x,y,z of the position and x,y,z of the quaternion for each joint
	 * @param numJoints number of joint in the frame
	 * @param hierarchy the hierarchy of a frame
	 */
	public Frame(float[] frameValues,int numJoints,Hierarchy[] hierarchy) {
		
		mJoints=new Joint[numJoints];
		
		for(int i=0;i<mJoints.length;i++){// for every joint
			mJoints[i]=new Joint(hierarchy[i].mName, hierarchy[i].mParent, 
					new Vec3(frameValues[i*FRAME_NR_FLOAT_VALUES+0], 
					        frameValues[i*FRAME_NR_FLOAT_VALUES+1], 
					        frameValues[i*FRAME_NR_FLOAT_VALUES+2]), 
					new Quaternion(frameValues[i*FRAME_NR_FLOAT_VALUES+3],
					        frameValues[i*FRAME_NR_FLOAT_VALUES+4],
					        frameValues[i*FRAME_NR_FLOAT_VALUES+5]));
		}
		
	}
	
	/**
	 * modifies joints positions depending on the parents positions and rotations
	 */
	public void calculateJointsPositionRelativeParent(){
		for(Joint j:mJoints){//for each joint
			calculatePositionRelativeParent(mJoints,j);
		}
	}
	
	/**
	 * calculate one joints positions depending on its parents position and rotation
	 * @param joints the joints in the frame
	 * @param j the joint to be modified
	 */
	private void calculatePositionRelativeParent(Joint[] joints,Joint j){
		Pair<Quaternion, Vec3> pair=mulByParent(joints, j);
		j.mQ=pair.first;
		j.mPosition=pair.second;
	
	}
	
	/**
	 * Calculates a new position of a joint depending on its parents, and returns the 
	 * new position and rotation as a Pair
	 * @param joints the joints containing the parents
	 * @param j the joint that a new position and rotation will be calculated for
	 * @return a pair the rotation and position as a Quaternion and Vec3 Pair
	 */
	private Pair<Quaternion, Vec3> mulByParent(Joint[] joints, Joint j){
		if(j.mParent==-1){// -1 means no parent
			return new Pair<Quaternion, Vec3>(j.mQ, j.mPosition); // no parent can affect, return it as it is 
		}else{
			Vec3 rot_pos=joints[j.mParent].mQ.rotate(j.mPosition); // get parents position, after rotation
			Vec3 newPos=joints[j.mParent].mPosition.add(rot_pos); // add the parents position to this joints position
			Quaternion newQuat=joints[j.mParent].mQ.mul(j.mQ); // mul the rotations
			newQuat.normalize(); // make sure the quaternion is still unit, there can be rounding errors
			return new Pair<Quaternion, Vec3>(newQuat, newPos);
		}
	}
	
}
