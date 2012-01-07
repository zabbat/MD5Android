package net.wandroid.md5.model.math;

public class Quaternions {

	private float x,y,z,w;

	/**
	 * Create quaternion. w is calculated automatic.
	 * @param x x component
	 * @param y y component
	 * @param z z component
	 */
	public Quaternions(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
		w=calcW();
	}
	
	/**
	 * creates a quaternion from a unit vector. sets w to zero.
	 * @param v
	 */
	private Quaternions(Vec3 v){
		this.x = v.x;
		this.y = v.y;
		this.z = v.z;
		w=0;
		
	}
	/**
	 * To rotate a vector around v with angle angle
	 * @param angle - angle to rotate
	 * @param v - unit vector to rotate around
	 */
	public Quaternions(float angle,Vec3 v){
		w=(float)Math.cos(angle/2.0f);
		float s=(float)Math.sin(angle/2.0f);
		x=v.x*s;
		y=v.y*s;
		z=v.z*s;
	}

	/**
	 * private default constructor, to be used internal.
	 */
	private Quaternions(){
		
	}

	
	/**
	 * rotates vector v
	 * @param v the vector to be rotated
	 * @return the rotated vector
	 */
	public Vec3 rotate(Vec3 v){
		Quaternions p=new Quaternions(v);
		Quaternions res=this.mul(p);
		res=res.mul(this.inverse());
		return res.toVec3();
	}
	
	/**
	 * Calculates the w components from x,y,z
	 * @return the w component
	 */
	private float calcW(){
		
		float t=1.0f-(x*x)-(y*y)-(z*z);
		if(t<0.0f){
			return 0.0f;
		}else{
			return -(float)Math.sqrt(t);
		}
	}


	/**
	 * inverse the quaternion. This presumes that the quaternion is a unit quaterion, since it returns the conjugate
	 * @return the inverse
	 */
	public Quaternions inverse(){
		Quaternions q=new Quaternions();
		q.x=-x;
		q.y=-y;
		q.z=-z;
		q.w=w;
		return q;
	}
	
	/**
	 * Multiplies with a quaternion
	 * @param q the quaternion to multiply with
	 * @return the product
	 */
	public Quaternions mul(Quaternions q){
		Quaternions r=new Quaternions();
		r.w=(w*q.w)-(x*q.x)-(y*q.y)-(z*q.z);
		r.x=(x*q.w)+(w*q.x)+(y*q.z)-(z*q.y);
		r.y=(y*q.w)+(w*q.y)+(z*q.x)-(x*q.z);
		r.z=(z*q.w)+(w*q.z)+(x*q.y)-(y*q.x);
		return r;
	}
	
	/**
	 * returns the x,y,z component as a vector
	 * @return the vector
	 */
	public Vec3 toVec3(){
		return new Vec3(x, y, z);
	}
	@Override
	public String toString() {
		return w+" "+x+" "+y+" "+z;
	}
	
	public void normalize(){
		float sum=(float)Math.sqrt(x*x+y*y+z*z+w*w);
		x/=sum;
		y/=sum;
		z/=sum;
		w/=sum;
	}
	
	public boolean isUnit(){
		return((float)Math.sqrt(x*x+y*y+z*z)<1.001f && (float)Math.sqrt(x*x+y*y+z*z)>0.999f);
	}
	
}
