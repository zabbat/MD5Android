package net.wandroid.md5.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.res.AssetManager;

import net.wandroid.md5.IModelOpener;
import net.wandroid.md5.util.Tick;

public class Md5 {

	private Md5Mesh mesh;
	private Md5Anim anim;
	private int currentFrame=-1;
	 
	public void draw(int program){
		mesh.draw(program);
	}

	public void drawNextFrame(int program) {
		if(currentFrame>=anim.numFrames){
			currentFrame=0;
		}
		calcMeshRelativeFrame(currentFrame);
		mesh.draw(program);
		currentFrame++;
	}

	

	
	
	public void init() {
		
		mesh.loadTextures();
	}

	public void calcMeshRelativeFrame(int frame){

		currentFrame=frame;
		for(Mesh m:mesh.meshes){
			m.initVertexData(anim.frames[frame].joints);
		}
	}
	/**
	 * 
	 * @param path
	 * @param fileName
	 * @throws IOException
	 */
	public void loadFile(IModelOpener modelOpener,final String path,final String fileName) throws IOException{
		//TODO: send object, OpenFileHandler or similar, instead of asset/sdcard/raw
	    Tick t=new Tick();
		t.start();
		{
//			File file=new File(path+fileName+".md5mesh");
//			FileInputStream ins=new FileInputStream(file);
//			InputStreamReader reader=new InputStreamReader(ins);
//			BufferedReader br=new BufferedReader(reader);
//			Md5MeshReader meshReader=new Md5MeshReader();
//			mesh=meshReader.load(br);
//			mesh.setTexturePath(path);
		    
		    InputStream ins=modelOpener.open(path+fileName+".md5mesh");
            InputStreamReader reader=new InputStreamReader(ins);
            BufferedReader br=new BufferedReader(reader);
            Md5MeshReader meshReader=new Md5MeshReader();
            mesh=meshReader.load(br);
            mesh.setTexturePath(path,modelOpener);
		    
		}
		
		{
//			File file=new File(path+fileName+".md5anim");
//			FileInputStream ins=new FileInputStream(file);
//			InputStreamReader reader=new InputStreamReader(ins);
//			BufferedReader br=new BufferedReader(reader);
//			Md5AnimReader animReader=new Md5AnimReader();
//			anim=animReader.load(br);
			
	         
	         InputStream ins=modelOpener.open(path+fileName+".md5anim");
	         InputStreamReader reader=new InputStreamReader(ins);
	         BufferedReader br=new BufferedReader(reader);
	         Md5AnimReader animReader=new Md5AnimReader();
	         anim=animReader.load(br);
		}
		t.tock("completed load!");
		calcMeshRelativeFrame(0);
	}

	

	
}
