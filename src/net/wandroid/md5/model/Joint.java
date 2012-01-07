package net.wandroid.md5.model;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import net.wandroid.md5.model.math.Quaternion;
import net.wandroid.md5.model.math.Vec3;
import android.util.Pair;

public class Joint {
	protected String name;
	protected int parent;
	protected Vec3 position;
	protected Quaternion q;
	
	public Joint(String name, int parent, Vec3 position, Quaternion q) {
		this.name = name;
		this.parent = parent;
		this.position = position;
		this.q = q;
	}

	
	
	@Override
	public String toString() {
		String s="";
		if(position!=null){
			s=name+":["+position.getX()+" "+position.getY()+" "+position.getZ()+"] "+parent;
		}else{
			throw new RuntimeException("check your joint parsing!");
		}
		return s;
	}
	
		
	
}
