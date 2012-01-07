package net.wandroid.md5;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import static android.opengl.GLES20.*;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLU;
import android.opengl.Matrix;

import net.wandroid.md5.gles20lib.Gles20Lib;
import net.wandroid.md5.model.Md5;


public class Md5Renderer implements Renderer{
	
	
    private static final String vertexShader="" +
    		"attribute vec2 a_texCoord;" +
    		"varying vec2 v_texCoord;" +
            "uniform mat4 u_mvpMatrix;" +
            "attribute vec3 a_position;" +
    		"void main(){" +
    		" v_texCoord=a_texCoord;" +
    		" gl_Position=u_mvpMatrix*vec4(a_position,1.0);" +
    		"}";

    private static final String fragmentShader="" +
            "precision mediump float;" +
            "varying vec2 v_texCoord;" +
            "uniform sampler2D u_texture;" +
            "void main(){" +
            " " +
            " gl_FragColor=vec4(1.0,1.0,1.0,1.0);" +
            " gl_FragColor=texture2D(u_texture,v_texCoord);" +
            "}";
    
    private float[] mvpMatrix=new float[16];
    private float[] pMatrix=new float[16];
    private float[] mvMatrix=new float[16];
    
    private static final int MD_WIDTH = 100;
	private static final int MD_HEIGHT = 100;
    private static final float MD_NEAR = 0.001f;
    private static final float MD_FAR = 1000.0f;

    private Md5 model;
	
	int shaderProgram=0;
	
	
	
	
	public Md5Renderer(Md5 model) {
		this.model = model;
	}

	float a=0;
	
	@Override
	public void onDrawFrame(GL10 gl) {

	    
	    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	    glClearColor(0.0f, 0, 0, 1.0f);
		glUseProgram(shaderProgram);
		
		Matrix.setIdentityM(mvMatrix, 0);
		Matrix.translateM(mvMatrix, 0, 0, -30, -100);
		Matrix.rotateM(mvMatrix, 0, a, 0, 1, 0);
		Matrix.rotateM(mvMatrix, 0, -90, 1, 0, 0);
		a+=0.2;
		Matrix.multiplyMM(mvpMatrix, 0, pMatrix, 0, mvMatrix, 0);
		int u_mvpMatrix=Gles20Lib.location("u_mvpMatrix",shaderProgram);
		glUniformMatrix4fv(u_mvpMatrix, 1, false, mvpMatrix, 0);
		model.drawNextFrame(shaderProgram);
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {

	    glViewport(0, 0, width, height);
	    Matrix.orthoM(pMatrix, 0, -MD_WIDTH/2, MD_WIDTH/2, -MD_HEIGHT/2, MD_HEIGHT/2, MD_NEAR, MD_FAR);
	    
	    
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig egl) {

		glEnable(GL_DEPTH_TEST);
	    shaderProgram=Gles20Lib.compileAndLinkProgram(vertexShader,fragmentShader);
	    model.init();// init textures and other pre rendering
	}

}
