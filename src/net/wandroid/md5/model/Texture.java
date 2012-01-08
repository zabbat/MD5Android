package net.wandroid.md5.model;

import static android.opengl.GLES20.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import net.wandroid.md5.gles20lib.Gles20Exception;
import android.graphics.Bitmap;
import android.opengl.GLUtils;

/**
 * class describing an open gl 2 texture
 * @author Jungbeck
 *
 */
public class Texture {

	private FloatBuffer mTexCoordBuf; // buffer for texture coordinates
	private float mTexCoords[];// texture coords array
	private int mMin=GL_LINEAR; // min filter for texture, default linear
	private int mMag=GL_LINEAR; // max filter for texture, default linear
	private int mClamp_t=GL_CLAMP_TO_EDGE; // clamp_t parameter, default clamp to edge
	private int mClamp_s=GL_CLAMP_TO_EDGE; // clamp_s parameter, default clamp to edge
	private int mId=0; // texture id

	private Bitmap mBitmap; // bitmap to be used as texture

	public Texture(Bitmap bmp) {
		mBitmap=bmp;
	}
	
	
	/**
	 * Inits the texture. This funtion must be called when there's a valid GLES20 reference,
	 * example from the Render.onSurfaceCreate(...)
	 */
	public void init(){	   
	    initBuf();
	    int tmp[]=new int[1];// temp array for a int
	    glGenTextures(1, tmp, 0);
	    mId=tmp[0];
	    if(mId==0){
	        throw new Gles20Exception("id was zero. id:"+mId);
	    }

	    bind();

	    GLUtils.texImage2D(GL_TEXTURE_2D, 0, mBitmap, 0);
	    mBitmap=null;//clear ref, so that the texture object doesn't prevent the garbage collector to relase
	    
	}
	
	/**
	 * Sets the texture min and mag filter
	 * @param min the min filter, example GL_LINEAR
	 * @param mag the mag filter, example GL_LINEAR
	 */
	public void setMinMagFilters(int min,int mag){
		this.mMin=min;
		this.mMag=mag;
	}
	
	/**
	 * sets the clamp parameters for GL_TEXTURE_WRAP_S and GL_TEXTURE_WRAP_T
	 * @param s parameter for GL_TEXTURE_WRAP_S, example GL_CLAMP_TO_EDGE
	 * @param t parameter for GL_TEXTURE_WRAP_T, example GL_CLAMP_TO_EDGE
	 */
	public void setClamp(int s,int t){
		mClamp_s=s;
		mClamp_t=t;
	}
	
	/**
	 * sets the texture coordinates, and updates the texture coord buffer
	 * @param coords
	 */
	public void setTexCoords(float[] coords){
		mTexCoords=coords;
		initBuf();
	}
	
	/**
	 * Reverses the Y-axis. Some image formats are stored upside down, and needs to be flipped.
	 * The buffer will be updated.
	 */
	public void flip(){
		for(int i=0;i<mTexCoords.length;i++){
			if(i%2==1){//Y coords are on every odd number
				mTexCoords[i]=1-mTexCoords[i]; //invert axis
			}
		}
		initBuf();
	}
	
	/**
	 * initiates the texture coordinate buffer with values of texture coordinates.
	 */
	private void initBuf(){
		ByteBuffer bb=ByteBuffer.allocateDirect(mTexCoords.length*Float.SIZE/8);
		bb.order(ByteOrder.nativeOrder());
		mTexCoordBuf=bb.asFloatBuffer();
		mTexCoordBuf.put(mTexCoords);
		mTexCoordBuf.position(0);
	}

	/**
	 * Returns the texture coordinate buffer.
	 * @return The texture coordinate buffer
	 */
	public FloatBuffer getTexCoordPointer() {
		return mTexCoordBuf;
	}

	/**
	 * binds the texture and sets min/mag and clamp values
	 */
	public void bind() {
	    glBindTexture(GL_TEXTURE_2D, mId);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, mMin);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, mMag);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, mClamp_s);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, mClamp_t);
		
	}


}
