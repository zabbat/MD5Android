package net.wandroid.md5.model;

import java.io.FileNotFoundException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

import net.wandroid.md5.gles20lib.Gles20Exception;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES10;
import static android.opengl.GLES20.*;
import android.opengl.GLU;
import android.opengl.GLUtils;
import android.util.Log;

public class Texture {

	private FloatBuffer texCoordBuf;
	private float texCoords[]={0,1, 0,0, 1,0, 1,1};
	private int min=GL_LINEAR;
	private int mag=GL_LINEAR;
	private int clamp_t=GL_CLAMP_TO_EDGE;
	private int clamp_s=GL_CLAMP_TO_EDGE;
	private int id=0;

	private Bitmap bitmap;

	public Texture(Bitmap bmp) {
		bitmap=bmp;
		//initBuf();		
	}
	
	
	/**
	 * inits the texture.
	 * @param gl
	 */
	public void init(){

	   
	    initBuf();
	    int i[]=new int[1];
	    glGenTextures(1, i, 0);
	    id=i[0];
	    if(id==0){
	        throw new Gles20Exception("id was zero. id:"+id);
	    }

	    bind();

	    GLUtils.texImage2D(GL_TEXTURE_2D, 0, bitmap, 0);
	    bitmap=null;//clear ref.
	    
	}
	
	public void setMinMagFilters(int min,int mag){
		this.min=min;
		this.mag=mag;
	}
	
	public void setClamp(int s,int t){
		clamp_s=s;
		clamp_t=t;
	}
	
	public void setTexCoords(float[] coords){
		texCoords=coords;
		initBuf();
	}
	
	/**
	 * Reverses the Y-axis. Some imageformat are stored upside down, then this function can be handy.
	 */
	public void flip(){
		for(int i=0;i<texCoords.length;i++){
			if(i%2==1){//Y coords are on every odd number
				texCoords[i]=1-texCoords[i]; //inverse axis
			}
		}
		initBuf();
	}
	
	private void initBuf(){
		ByteBuffer bb=ByteBuffer.allocateDirect(texCoords.length*Float.SIZE/8);
		bb.order(ByteOrder.nativeOrder());
		texCoordBuf=bb.asFloatBuffer();
		texCoordBuf.put(texCoords);
		texCoordBuf.position(0);
	}

	public FloatBuffer getTexCoordPointer() {
		return texCoordBuf;
		
	}

	public void bind() {
	    
		
	    
        glBindTexture(GL_TEXTURE_2D, id);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, min);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, mag);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, clamp_s);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, clamp_t);
		
	}

	public static void unbind(GL10 gl){
		glBindTexture(GL_TEXTURE_2D, 0);
		
	}
	


}
