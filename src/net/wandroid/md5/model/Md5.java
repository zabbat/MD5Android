package net.wandroid.md5.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.res.AssetManager;

import net.wandroid.md5.Tick;
import net.wandroid.md5.ioutils.IModelFileOpener;

/**
 * Class that describes a MD5 model.
 * MD5 are a file format used in quake 4 for skeleton animation.
 * @author Jungbeck
 *
 */
public class Md5 {
    
    private static final String MD5_MESH_EXT=".md5mesh";
    private static final String MD5_ANIM_EXT=".md5anim";
    
	private Md5Mesh mMesh; // the mesh of the md5
	private Md5Anim mAnim; // the animation of the md5
	private int mCurrentFrame=-1; // current frame to draw
	 
	/**
	 * Draws the model and does not change the frame.
	 * @param program gles 2 shader program to be used when drawing the frame
	 */
	public void draw(int program){
		mMesh.draw(program);
	}

    /**
     * Draws the model and increases the frame, looping to first frame if current frame exceeds the number of frames
     * @param program gles 2 shader program to be used when drawing the frame
     */
	public void drawNextFrame(int program) {
		if(mCurrentFrame>=mAnim.numFrames){// if current frame exceeds the number of frames of the model
			mCurrentFrame=0;
		}
		calcMeshRelativeFrame(mCurrentFrame);
		mMesh.draw(program);
		mCurrentFrame++;
	}

	

	
	/**
	 * initiates data that must be called from the render code, such as texture laoding.
	 * In android you cannot access a valid reference to GLES 1.0/2.0 unless called from Renderer- methods, therefor
	 * you need to call this method, preferable in the Renderer.onSurfaceCreated(GL10 gl, EGLConfig egl) method. 
	 */
	public void init() {
		mMesh.loadTextures();// inits textures
	}
	
	/**
	 * Updates the vertices to be adjusted after the skeleton of the frame.
	 * Also sets the current frame to passed frame.
	 * @param frame the frame that the skeleton data should be used from
	 */
	private void calcMeshRelativeFrame(int frame){

		mCurrentFrame=frame;
		for(Mesh m:mMesh.meshes){
			m.initVertexData(mAnim.frames[frame].joints);
		}
	}

	/**
	 * Loads the MD5 file to memory. It can be fetched from either the asset folder or from SD card 
	 * @param modelOpener The model opener.
	 * @param path folder path to the model
	 * @param fileName the name of the model (the file name without '.md5mesh' or '.md5anim')
	 * @throws IOException if the model failed to be loaded
	 */
	public void loadFile(IModelFileOpener modelOpener,final String path,final String fileName) throws IOException{
	    Tick t=new Tick();
		t.start();// Measure time to load model
		
		    InputStream isMesh=modelOpener.open(path+fileName+MD5_MESH_EXT);
            InputStreamReader isrMesh=new InputStreamReader(isMesh);
            BufferedReader brMesh=new BufferedReader(isrMesh);
            Md5MeshFileReader meshFileReader=new Md5MeshFileReader();
            mMesh=meshFileReader.load(brMesh); // read the mesh data to memory
            mMesh.setTexturePath(path,modelOpener); // fixes the texture path, that should be relative to the model folder
		    
            InputStream isAnim=modelOpener.open(path+fileName+MD5_ANIM_EXT);
            InputStreamReader isrAnim=new InputStreamReader(isAnim);
            BufferedReader brAnim=new BufferedReader(isrAnim);
		    Md5AnimFileLoader animFileReader=new Md5AnimFileLoader();
		    mAnim=animFileReader.load(brAnim);// read animation and skeleton data to memory
		
		t.tock("completed load!"); // display load time in logcat
		calcMeshRelativeFrame(0); // show mesh from first animation frame 
	}

	

	
}
