package net.wandroid.md5.model.math;

/**
 * Describes a Quaternion
 * @author Jungbeck
 *
 */
public class Quaternion {

	private float x,y,z,w; // elements of the quaternion

	/**
	 * Creates quaternion. w is calculated automatic.
	 * @param x x component
	 * @param y y component
	 * @param z z component
	 */
	public Quaternion(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
		w=calcW();
	}
	
	/**
	 * creates a quaternion from a unit vector. sets w to zero.
	 * Private constructor that should only be called internal.
	 * @param v unit vector 
	 */
	private Quaternion(Vec3 v){
		this.x = v.mX;
		this.y = v.mY;
		this.z = v.mZ;
		w=0;
		
	}
	
	/**
	 * Creates a quaternion representing a rotation around an axis.
	 * To rotate a vector around v with angle angle
	 * @param angle - angle to rotate in radians
	 * @param v - unit vector to rotate around
	 */
	public Quaternion(float angle,Vec3 v){
		w=(float)Math.cos(angle/2.0f);
		float s=(float)Math.sin(angle/2.0f);
		x=v.mX*s;
		y=v.mY*s;
		z=v.mZ*s;
	}

	/**
	 * private default constructor, to be used internal.
	 */
	private Quaternion(){
	}

	
	/**
	 * rotates vector v. v will not be changed.
	 * To rotate a vector the formula is Q.p.Q*, where '.' means multiply operation, 
	 * 'Q' a quaternion, and 'Q*' its inverse,'p' a 3d vector.
	 * @param v the vector to be rotated
	 * @return the rotated vector
	 */
	public Vec3 rotate(final Vec3 v){
		Quaternion p=new Quaternion(v);
		Quaternion res=this.mul(p); //res= Q.p
		res=res.mul(this.inverse()); //res = res.Q*
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
	 * inverse the quaternion. This presumes that the quaternion is a unit quaterion, since it returns the conjugate.
	 * Will not affect the calling object.
	 * @return the inverse
	 */
	public Quaternion inverse(){
		Quaternion q=new Quaternion();
		q.x=-x;
		q.y=-y;
		q.z=-z;
		q.w=w;
		return q;
	}
	
	/**
	 * Multiplies with a quaternion.
	 * Will not affect the calling object.
	 * @param q the quaternion to multiply with
	 * @return the product
	 */
	public Quaternion mul(final Quaternion q){
		Quaternion r=new Quaternion();
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
	
	/**
	 * Normalizes the quaternion based on w,x,y,z. Will affect the calling object. 
	 */
	public void normalize(){
		float sum=(float)Math.sqrt(x*x+y*y+z*z+w*w);
		x/=sum;
		y/=sum;
		z/=sum;
		w/=sum;
	}
			
}
