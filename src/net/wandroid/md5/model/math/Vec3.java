package net.wandroid.md5.model.math;

public class Vec3 {
	public static final float VEC_DELTA = 0.0001f; //the divergence allowed when cmparing the float components in compareTo
	protected float x;
	protected float y;
	protected float z;

	public Vec3(float x, float y, float z) {
		
		this.x = x;
		this.y = y;
		this.z = z;
	}
	

	
	/**
	 * scales the components with a scalare
	 * @param s scalare
	 * @return the vector scaled by s
	 */
	public Vec3 scale(float s){
		Vec3 v=new Vec3(x, y, z);
		v.x = x*s;
		v.y = y*s;
		v.z = z*s;
		return v;
	}
	
	@Override
	public String toString() {
		return x+" "+y+" "+z;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Vec3){
			Vec3 v=(Vec3) obj;
			return (aproxEqual(x,v.x) && aproxEqual(x,v.x) && aproxEqual(x,v.x));
		}
		return false;
	}

	private boolean aproxEqual(float f1, float f2) {
		
		return Math.abs(f1-f2)<VEC_DELTA;
	}

	/**
	 * adds two vectors
	 * @param v the vector to add with
	 * @return the result of the addition
	 */
	public Vec3 add(Vec3 v) {
		Vec3 res=new Vec3(x+v.x, y+v.y, z+v.z);
		return res;
	}
	
	public void normalize(){
		float sum=(float)Math.sqrt(x*x+y*y+z*z);
		x/=sum;
		y/=sum;
		z/=sum;	
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public float getZ() {
		return z;
	}
	
	
	
}
