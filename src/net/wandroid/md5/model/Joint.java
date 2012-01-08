package net.wandroid.md5.model;

import net.wandroid.md5.model.math.Quaternion;
import net.wandroid.md5.model.math.Vec3;

/**
 * Describes a Joint, that creates a skeleton together with other jonints
 * @author Jungbeck
 *
 */
public class Joint {
	protected String mName; // name of the joint
	protected int mParent; // parent of the joint
	protected Vec3 mPosition; // position of the joint
	protected Quaternion mQ; // quaternion rotation of the joint
	
	/**
	 * Constructor for a joint
	 * @param name the name of the joint
	 * @param parent the parent of the joint
	 * @param position the position of the joint
	 * @param q the rotation of the joint
	 */
	public Joint(String name, int parent, Vec3 position, Quaternion q) {
		this.mName = name;
		this.mParent = parent;
		this.mPosition = position;
		this.mQ = q;
	}

	
	
	@Override
	public String toString() {
		String s="";
		if(mPosition!=null){
			s=mName+":["+mPosition.getX()+" "+mPosition.getY()+" "+mPosition.getZ()+"] "+mParent;
		}else{
			throw new RuntimeException("check your joint parsing!");
		}
		return s;
	}
	
		
	
}
