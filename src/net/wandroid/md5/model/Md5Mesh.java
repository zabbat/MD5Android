package net.wandroid.md5.model;


public class Md5Mesh {

	protected int numJoints;
	protected int numMeshes;
	protected Joint[] joints;
	protected Mesh[] meshes;
	
	
	public void draw(int program){
		for(int i=0;i<meshes.length;i++){
			meshes[i].draw(program);
		}
	}


	public void loadTextures() {
		for(Mesh m:meshes){
			m.loadTexture();
		}
	}


	public void setTexturePath(String path) {
		
		for(Mesh m:meshes){
			m.setTexturePath(path);
		}
	}
}
