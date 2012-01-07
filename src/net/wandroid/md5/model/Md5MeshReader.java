package net.wandroid.md5.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.wandroid.md5.model.math.Quaternions;
import net.wandroid.md5.model.math.Vec3;
import net.wandroid.md5.util.Tick;

import android.util.Log;

public class Md5MeshReader extends Md5Reader{

	/**
	 * Loads the mesh file
	 * 
	 * @param reader
	 *            BufferedReader that is connected to the mesh file
	 * @throws IOException
	 */
	public Md5Mesh load(BufferedReader reader) throws IOException {
		String meshFile=loadFileToString(reader);
		Md5Mesh md5Mesh=new Md5Mesh();
		Tick t = new Tick();
		t.start();
		loadHeader(meshFile,md5Mesh);
		t.tock("loaded header");
		loadJoints(meshFile, md5Mesh);
		t.tock("loaded joints");
		loadMeshes(meshFile, md5Mesh);
		t.tock("loaded mesh");
		reader.close();
		return md5Mesh;
	}

	protected void loadHeader(String meshFile,Md5Mesh md5Mesh) {
		// check if version is 10
		Pattern labelIntPattern = Pattern.compile("[\\w\\d\\\"]+",
				Pattern.MULTILINE);
		Matcher match = labelIntPattern.matcher(meshFile.substring(0, 200));//just check the first 200 chars

		//load version
		String value;
		value=labelValue(match, "md5version");
		if (!value.equals("10")) {
			throw new ModelParseException(
					"Not correct md5 version, expected 10, but was " + value);
		}
		
		//load commandline
		value=labelValue(match, "commandline");
		//commandline options are ignored
		if(!value.equals("\"\"")){
			Log.d("loadHeader", "warning commandline is ignored: "+value);
		}
		
		//load number of joints
		value=labelValue(match, "numJoints");
		try{
			md5Mesh.numJoints=Integer.parseInt(value);
		}catch (NumberFormatException ne){
			throw new ModelParseException("could not parse value:"+value);
		}
		md5Mesh.joints=new Joint[md5Mesh.numJoints];
		Log.d("loadHeader","numJoints:"+md5Mesh.numJoints);

		//load number of meshes
		value=labelValue(match, "numMeshes");
		try{
			md5Mesh.numMeshes=Integer.parseInt(value);
		}catch (NumberFormatException ne){
			throw new ModelParseException("could not parse value:"+value);
		}
		md5Mesh.meshes=new Mesh[md5Mesh.numMeshes];
		Log.d("loadHeader","numMeshes:"+md5Mesh.numMeshes);

		
	}
	

	protected void loadJoints(String meshFile,Md5Mesh md5Mesh){
		Pattern jointsPattern=Pattern.compile("joints\\s+\\{[^\\}]+\\}", Pattern.MULTILINE);
		Matcher match=jointsPattern.matcher(meshFile);
		if(!match.find()){
			throw new ModelParseException("could not find jonits");
		}
		String joints=match.group();
		
		Pattern pattern=Pattern.compile("//[\\s\\w\\d.]+", Pattern.MULTILINE);
		match=pattern.matcher(joints);
		joints=match.replaceAll(""); //remove all comments
		pattern=Pattern.compile("[^\\s()]+", Pattern.MULTILINE);
		match=pattern.matcher(joints);
		int start=joints.indexOf("{");
		match.find(start); //start from first '{' 
		
		String name=null;
		int parent;
		float px,py,pz;
		float qx,qy,qz;
		for(int i=0;i<md5Mesh.numJoints;i++){
			try{
				match.find(); name=match.group();
				match.find(); parent=Integer.parseInt(match.group());
				match.find(); px=Float.parseFloat(match.group());
				match.find(); py=Float.parseFloat(match.group());
				match.find(); pz=Float.parseFloat(match.group());
				match.find(); qx=Float.parseFloat(match.group());
				match.find(); qy=Float.parseFloat(match.group());
				match.find(); qz=Float.parseFloat(match.group());
	
				md5Mesh.joints[i]=new Joint(name, parent, new Vec3(px, py, pz), new Quaternions(qx,qy,qz));
			}catch(NumberFormatException ne){
				throw new ModelParseException("could not parse joint["+i+"]("+name+")");
			}
			
		}
		
	}
	
	protected void loadMeshes(String meshFile,Md5Mesh md5Mesh){
		Tick t=new Tick();
		t.start();
		Pattern jointsPattern=Pattern.compile("mesh\\s+\\{[^\\}]+\\}", Pattern.MULTILINE);
		Matcher match=jointsPattern.matcher(meshFile);
		for(int i=0;i<md5Mesh.numMeshes;i++){
			if(!match.find()){
				throw new ModelParseException("could not find mesh["+i+"]");
			}
			String meshSection=match.group();
			md5Mesh.meshes[i]=new Mesh();
			loadMesh(meshSection, md5Mesh.meshes[i]);			
			t.tock("loaded mesh "+i);
			md5Mesh.meshes[i].initVertexData(md5Mesh.joints);
			t.tock("calculated mesh "+i+" vertex position");
		}
	}
	
	private void loadMesh(String meshSection,Mesh mesh){
		
		Pattern pattern=Pattern.compile("[^\\s()]+");
		Matcher match=pattern.matcher(meshSection);
		match.find();//skip 'mesh'
		match.find();//skip '{'
		mesh.folderPath=labelValue(match, "shader");
		Log.d("loadMesh", "texture folder:"+mesh.folderPath);
		mesh.numverts=Integer.parseInt(labelValue(match, "numverts"));
		mesh.vertex=new Vertex[mesh.numverts];
		Log.d("loadMesh", "number of vertices::"+mesh.numverts);
		loadVertex(match,mesh.vertex);
		Log.d("loadMesh", "loaded vertex");

		mesh.numtris=Integer.parseInt(labelValue(match, "numtris"));
		mesh.tri=new Triangle[mesh.numtris];
		Log.d("loadMesh", "number of triangles:"+mesh.numtris);
		loadTriangles(match,mesh.tri);
		Log.d("loadMesh", "loaded triangles");

		mesh.numweights=Integer.parseInt(labelValue(match, "numweights"));
		mesh.weights=new Weight[mesh.numweights];
		Log.d("loadMesh", "number of weights:"+mesh.numweights);
		loadWeights(match,mesh.weights);
		Log.d("loadMesh", "loaded weights");
		
	}
	
	private void loadWeights(Matcher match, Weight[] weights) {
		int index=-1;
		int joint;
		float bias;
		float x,y,z;
		for(int i=0;i<weights.length;i++){
			try{
			match.find();//weight
			match.find(); index=Integer.parseInt(match.group()); 
			match.find(); joint=Integer.parseInt(match.group());
			match.find(); bias=Float.parseFloat(match.group());
			match.find(); x=Float.parseFloat(match.group());
			match.find(); y=Float.parseFloat(match.group());
			match.find(); z=Float.parseFloat(match.group());
			weights[i]=new Weight(index, joint, bias, new Vec3(x, y, z));
			}catch(NumberFormatException ne){
				throw new ModelParseException("could not parse weight["+i+"](index "+index+")");
			}
		}
		
	}

	private void loadVertex(Matcher match, Vertex[] vertex) {
		int index=-1;
		float s,t;
		int startWeight,countWeight;
		for(int i=0;i<vertex.length;i++){
			try{
			match.find();//vert
			match.find(); index=Integer.parseInt(match.group()); 
			match.find(); s=Float.parseFloat(match.group());
			match.find(); t=Float.parseFloat(match.group());
			match.find(); startWeight=Integer.parseInt(match.group());
			match.find(); countWeight=Integer.parseInt(match.group());
			vertex[i]=new Vertex(index, s, t, startWeight, countWeight);
			}catch(NumberFormatException ne){
				throw new ModelParseException("could not parse vertex["+i+"](index "+index+")");
			}
		}
	}
	private void loadTriangles(Matcher match, Triangle[] triangles) {
		int index=-1;
		int i0,i1,i2;
		
		for(int i=0;i<triangles.length;i++){
			try{
			match.find();//tri
			match.find(); index=Integer.parseInt(match.group()); 
			match.find(); i0=Integer.parseInt(match.group());
			match.find(); i1=Integer.parseInt(match.group());
			match.find(); i2=Integer.parseInt(match.group());
			triangles[i]=new Triangle(index, i0, i1, i2);
			}catch(NumberFormatException ne){
				throw new ModelParseException("could not parse tri["+i+"](index "+index+")");
			}
		}
	}

}
