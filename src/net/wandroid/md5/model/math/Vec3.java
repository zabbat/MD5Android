package net.wandroid.md5.model.math;


/**
 * Class that describes a vector with 3 elements.
 * 
 * @author Jungbeck
 *
 */
public class Vec3 {
	public static final float VEC_DELTA = 0.0001f; //the divergence allowed when comparing the float components in compareTo
	//TODO: default or protected visibility?
	/** the elements have default package visibilty, since Quaternions need to access
	 * these elements, and it would both be slower and less readable if getters were used
	 * Outside the package, they are not meant to be accessed.*/
	float mX; 
	float mY;
	float mZ;

	/**
	 * Constructor that sets the elements in the vector
	 * @param x x value
	 * @param y y value
	 * @param z z value
	 */
	public Vec3(float x, float y, float z) {
	    this.mX = x;
		this.mY = y;
		this.mZ = z;
	}
	

	
	/**
	 * scales the elements with a scalar. This method does not change the value of the calling object.
	 * @param s scalar
	 * @return the vector scaled by s
	 */
	public Vec3 scale(float s){
		Vec3 v=new Vec3(mX, mY, mZ);
		v.mX = mX*s;
		v.mY = mY*s;
		v.mZ = mZ*s;
		return v;
	}
	
	@Override
	public String toString() {
		return mX+" "+mY+" "+mZ;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Vec3){
			Vec3 v=(Vec3) obj;
			return (aproxEqual(mX,v.mX) && aproxEqual(mX,v.mX) && aproxEqual(mX,v.mX));
		}
		return false;
	}

	private boolean aproxEqual(float f1, float f2) {
		return Math.abs(f1-f2)<VEC_DELTA;
	}

	/**
	 * adds two vectors. This method does not change the value of the calling object.
	 * @param v the vector to add with
	 * @return the result of the addition
	 */
	public Vec3 add(Vec3 v) {
		Vec3 res=new Vec3(mX+v.mX, mY+v.mY, mZ+v.mZ);
		return res;
	}
	
	/**
	 * Normalize this vector. Will change the values of this object
	 * If the sum of the squared elements is zero, then the vector will not be changed. 
	 */
	public void normalize(){
		float sum=(float)Math.sqrt(mX*mX+mY*mY+mZ*mZ);
		if(sum==0){// avoid division by zero
		    return;
		}
		mX/=sum;
		mY/=sum;
		mZ/=sum;	
	}

	public float getX() {
		return mX;
	}

	public float getY() {
		return mY;
	}

	public float getZ() {
		return mZ;
	}
	
	
	
}
