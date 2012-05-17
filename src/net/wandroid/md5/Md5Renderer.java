package net.wandroid.md5;

import static android.opengl.GLES20.*;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import net.wandroid.md5.gles20lib.Gles20Lib;
import net.wandroid.md5.model.Md5;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.Matrix;

/**
 * The md5 Renderer. 
 * @author Jungbeck
 *
 */
public class Md5Renderer implements Renderer{
	
	// a simple vertex shader
    private static final String VERTEX_SHADER="" +
    		"attribute vec2 a_texCoord;" +
    		"varying vec2 v_texCoord;" +
            "uniform mat4 u_mvpMatrix;" +
            "attribute vec3 a_position;" +
    		"void main(){" +
    		" v_texCoord=a_texCoord;" +
    		" gl_Position=u_mvpMatrix*vec4(a_position,1.0);" +
    		"}";

    // a simple fragment shader
    private static final String FRAGMENT_SHADER="" +
            "precision mediump float;" +
            "varying vec2 v_texCoord;" +
            "uniform sampler2D u_texture;" +
            "void main(){" +
            " gl_FragColor=texture2D(u_texture,v_texCoord);" +
            "}";
    
    
    private static final int MD_WIDTH = 100; // width of ortho view
    private static final int MD_HEIGHT = 100; // height of ortho view
    private static final float MD_NEAR = 0.001f; // near clipping for ortho view
    private static final float MD_FAR = 1000.0f; // far clipping for ortho view
    private static final float MODEL_Y_TRANSLATE = -30; // the model should be lowered a bit
    private static final float MODEL_Z_TRANSLATE = -100; // the model should be placed a bit away from the screen

    private static final float RENDER_ROTATION_SPEED = 0.2f; // the amount of degrees the rotation should increase with for every frame
    
    private float[] mMvpMatrix=new float[16]; // projection * model view
    private float[] mPMatrix=new float[16]; // projection matrix
    private float[] mMvMatrix=new float[16];// model view matrix
    private float mRotationAngle=0; // the rotation for the model
    private int mShaderProgram=0; // shader program to be used for rendering

    private Md5 mModel; // the model to be rendered
    
    private boolean mPause; // true if same frame should be rendered, false if frames should increase

	/**
	 * Constructor for the renderer. 
	 * @param model the model to be rendered 
	 */
	public Md5Renderer(Md5 model) {
		this.mModel = model;
	}

	
	@Override
	public void onDrawFrame(GL10 gl) {

	    
	    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	    glClearColor(0.0f, 0, 0, 1.0f);
		glUseProgram(mShaderProgram);
		
		Matrix.setIdentityM(mMvMatrix, 0);
		Matrix.translateM(mMvMatrix, 0, 0, MODEL_Y_TRANSLATE, MODEL_Z_TRANSLATE);
		Matrix.rotateM(mMvMatrix, 0, mRotationAngle, 0, 1, 0);
        mRotationAngle+=RENDER_ROTATION_SPEED;
        Matrix.rotateM(mMvMatrix, 0, -90, 1, 0, 0);

		Matrix.multiplyMM(mMvpMatrix, 0, mPMatrix, 0, mMvMatrix, 0);
		int u_mvpMatrix=Gles20Lib.location("u_mvpMatrix",mShaderProgram);
		glUniformMatrix4fv(u_mvpMatrix, 1, false, mMvpMatrix, 0);
		mModel.drawNextFrame(mShaderProgram);
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {	    
	    glViewport(0, 0, width, height);
	    if(width<height){
	        Matrix.orthoM(mPMatrix, 0, -MD_WIDTH/2, MD_WIDTH/2, -MD_HEIGHT/2, MD_HEIGHT/2, MD_NEAR, MD_FAR);
	    }else{
	        Matrix.orthoM(mPMatrix, 0, -MD_HEIGHT/2, MD_HEIGHT/2, -MD_WIDTH/2, MD_WIDTH/2, MD_NEAR, MD_FAR);
	    }
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig egl) {

		glEnable(GL_DEPTH_TEST);
	    mShaderProgram=Gles20Lib.compileAndLinkProgram(VERTEX_SHADER,FRAGMENT_SHADER);
	    mModel.init();// init textures and other pre rendering
	}


}
