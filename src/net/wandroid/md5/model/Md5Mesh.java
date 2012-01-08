package net.wandroid.md5.model;

import net.wandroid.md5.ioutils.IModelFileOpener;

/**
 * Class that describes the mesh of a Md5 model
 * @author Jungbeck
 *
 */
public class Md5Mesh {

	protected int mNumJoints;// number of joints in the mesh
	protected int mNumMeshes;// number of meshes in the mesh. The models mesh consists of several smaller meshes , such as arms, head and so on 
	protected Joint[] mJoints;// joints array
	protected Mesh[] mMeshes; // meshes array
	
	/**
	 * Draws the mesh with gles 2.0 shader program
	 * @param program the shader program to be used when rendering
	 */
	public void draw(int program){
		for(int i=0;i<mMeshes.length;i++){// render every mesh
			mMeshes[i].draw(program);
		}
	}

	/**
	 * Loads textures of the model, so that they can be used when the model is rendered.
	 * Should be called in the SurfaceCreated method of a gl Renderer
	 */
	public void loadTextures() {
		for(Mesh m:mMeshes){// load texture for each mesh
			m.loadTexture();
		}
	}

	/**
	 * Sets the correct texture path of the model. Some 3d programs saves texture path absolutley, and it 
	 * need to be made relative to the folder of the .md5mesh and .md5anim files
	 * @param path folder containing the model files 
	 * @param modelFileOpener the type of file opener to be used
	 */
	public void setTexturePath(String path,IModelFileOpener modelFileOpener) {
		
		for(Mesh m:mMeshes){
			m.setTexturePath(path,modelFileOpener);
		}
	}
}
