package net.wandroid.md5.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.wandroid.md5.Tick;
import net.wandroid.md5.model.math.Quaternion;
import net.wandroid.md5.model.math.Vec3;

import android.util.Log;

/**
 * Class responsible for loading a .md5meh file, containing mesh data
 * 
 * @author Jungbeck
 *
 */
public class Md5MeshFileLoader extends Md5Reader{

    private static final String MESH_NUMMESH_LABEL = "numMeshes";
    private static final String MESH_SHADER_LABEL = "shader";
    private static final String MESH_NUMVERT_LABEL = "numverts";
    private static final String MESH_NUMTRIS_LABEL = "numtris";
    private static final String MESH_NUMWEIGHTS_LABEL = "numweights";

    /**
     * Loads mesh data from a file and returns it as a Md5Mesh instance
     * @param reader a BufferedReader connected to the mesh file
     * @return if successfully loaded , a Md5Mesh instance is returned
     * @throws IOException if the file failed to load
     */
	public Md5Mesh load(BufferedReader reader) throws IOException {
		String meshFile=loadFileToString(reader);
		Md5Mesh md5Mesh=new Md5Mesh();
		Tick t = new Tick();
		t.start();// start measure time
		loadHeader(meshFile,md5Mesh); // load header
		t.tock("loaded header");
		loadJoints(meshFile, md5Mesh); // load joints
		t.tock("loaded joints");
		loadMeshes(meshFile, md5Mesh); // load meshes
		t.tock("loaded mesh");
		reader.close();
		return md5Mesh;
	}

	/**
     * 
     * Loads the header information from the mesh file. The header structure looks like the following :
     * 
     * MD5Version 10
     * commandline "string"
     * 
     * numJoints int
     * numMeshes int
     * 
     * 
     * This loader supports Md5 version 10, it might be able to load other versions too, but for now it will just throw an exception
     * if any other version is found
     * @param meshFile reference to the mesh file as a string
     * @param md5Mesh reference to the data file the header info should be loaded to
     * @exception ModelParseException if the file could not be parsed, or version is not 10
     */
	protected void loadHeader(String meshFile,Md5Mesh md5Mesh)throws ModelParseException {
	    //TODO: Move to  super class?
		// check if version is 10
		Pattern labelIntPattern = Pattern.compile("[\\w\\d\\\"]+",
				Pattern.MULTILINE);
		Matcher match = labelIntPattern.matcher(meshFile.substring(0, 200));//just check for the header in the beginning of the file

		//load version
		String value=labelValue(match, "md5version");// check that we found the version label and return it
		if (!value.equals("10")) {
			throw new ModelParseException(
					"Not correct md5 version, expected 10, but was " + value);
		}
		
		//load command line
		value=labelValue(match, "commandline");
		if(!value.equals("\"\"")){//command line options are ignored
			Log.d("loadHeader", "warning commandline is ignored: "+value);
		}
		
		//load number of joints
		value=labelValue(match, "numJoints");
		try{
			md5Mesh.numJoints=Integer.parseInt(value);
		}catch (NumberFormatException ne){
			throw new ModelParseException("could not parse value:"+value);
		}
		md5Mesh.joints=new Joint[md5Mesh.numJoints];// initiates joints
		Log.d("loadHeader","numJoints:"+md5Mesh.numJoints);

		//load number of meshes
		value=labelValue(match, MESH_NUMMESH_LABEL);
		try{
			md5Mesh.numMeshes=Integer.parseInt(value);
		}catch (NumberFormatException ne){
			throw new ModelParseException("could not parse value:"+value);
		}
		md5Mesh.meshes=new Mesh[md5Mesh.numMeshes];// initiates meshes
		Log.d("loadHeader","numMeshes:"+md5Mesh.numMeshes);

		
	}
	
    /**
     * Loads the joints from the mesh file
     * The structure of joints is:
     * 
     * joints {
     *     "name" parent ( pos.x pos.y pos.z ) ( orient.x orient.y orient.z )
     *     ...
     * }     
     *
     * there are numJoints entries
     * 
     * @param meshFile the mesh file as a string
     * @param md5Mesh the Md5Mesh reference to load the data to
     * @exception ModelParseException if the file could not be parsed
     */
	protected void loadJoints(String meshFile,Md5Mesh md5Mesh)throws ModelParseException{
		Pattern jointsPattern=Pattern.compile("joints\\s+\\{[^\\}]+\\}", Pattern.MULTILINE);
		Matcher match=jointsPattern.matcher(meshFile);
		if(!match.find()){// could not find the entries of the joints
			throw new ModelParseException("could not find jonits");
		}

		String joints=match.group();
		// find everything right of a comment '//' and the comment in the file
		Pattern pattern=Pattern.compile("//[\\s\\w\\d.]+", Pattern.MULTILINE);
		match=pattern.matcher(joints);
		joints=match.replaceAll(""); //remove all comments
		
		pattern=Pattern.compile("[^\\s()]+", Pattern.MULTILINE);// find groups not consisting of brackets or white spaces
		match=pattern.matcher(joints);
		int start=joints.indexOf("{");
		match.find(start); //start from first '{' 
		
		String name=null;
		int parent;
		float px,py,pz;
		float qx,qy,qz;
		for(int i=0;i<md5Mesh.numJoints;i++){
			try{
				match.find();// find name 
				name=match.group();
				match.find(); // find parent
				parent=Integer.parseInt(match.group());
				
				match.find(); // find x position
				px=Float.parseFloat(match.group());
				match.find(); // find y position
				py=Float.parseFloat(match.group());
				match.find(); // find z position
				pz=Float.parseFloat(match.group());
				
				match.find(); // find x for quaternion rotation
				qx=Float.parseFloat(match.group());
				match.find(); // find y for quaternion rotation
				qy=Float.parseFloat(match.group());
				match.find(); // find z for quaternion rotation
				qz=Float.parseFloat(match.group());
	
				md5Mesh.joints[i]=new Joint(name, parent, new Vec3(px, py, pz), new Quaternion(qx,qy,qz));
			}catch(NumberFormatException ne){
				throw new ModelParseException("could not parse joint["+i+"]("+name+")");
			}
			
		}
		
	}
  
	/**
     * Loads the meshes from the mesh file
     * The structure of the meshes is:
     * 
     * mesh {
     *     shader "string" 
     * 
     *     numverts int
     *     vert vertIndex ( s t ) startWeight countWeight
     *     vert ...
     * 
     *     numtris int>
     *     tri triIndex vertIndex[0] vertIndex[1] vertIndex[2]
     *     tri ...
     * 
     *     numweights int
     *     weight weightIndex joint bias ( pos.x pos.y pos.z )
     *     weight ...
     * }  
     *
     * there are numMeshes mesh entries
     * 
     * @param meshFile the mesh file as a string
     * @param md5Mesh the Md5Mesh reference to load the data to
     * @exception ModelParseException if the file could not be parsed
     */	
	protected void loadMeshes(String meshFile,Md5Mesh md5Mesh)throws ModelParseException{
		Tick t=new Tick();
		t.start();
		Pattern jointsPattern=Pattern.compile("mesh\\s+\\{[^\\}]+\\}", Pattern.MULTILINE);
		Matcher match=jointsPattern.matcher(meshFile);
		for(int i=0;i<md5Mesh.numMeshes;i++){// for every mesh{..} structure in the file
			if(!match.find()){// could not find entry
				throw new ModelParseException("could not find mesh["+i+"]");
			}
			String meshSection=match.group();
			md5Mesh.meshes[i]=new Mesh();
			loadMesh(meshSection, md5Mesh.meshes[i]);// load mesh into the Md5Mesh object			
			t.tock("loaded mesh "+i);
			md5Mesh.meshes[i].initVertexData(md5Mesh.joints);
			t.tock("calculated mesh "+i+" vertex position");
		}
	}
	
	/**
	 * loads a mesh
	 * @param meshSection string with the mesh section
	 * @param mesh the Mesh reference that this mesh data should be loaded to
	 */
	private void loadMesh(String meshSection,Mesh mesh){
		
		Pattern pattern=Pattern.compile("[^\\s()]+");// find groups not consisting of brackets or white spaces
		Matcher match=pattern.matcher(meshSection);
		match.find();//skip 'mesh'
		match.find();//skip '{'
		mesh.folderPath=labelValue(match, MESH_SHADER_LABEL);// find shader label, the value contains the texture path
		Log.d("loadMesh", "texture folder:"+mesh.folderPath);
		
		mesh.numverts=Integer.parseInt(labelValue(match, MESH_NUMVERT_LABEL));// find number of verts label and save the value
		mesh.vertex=new Vertex[mesh.numverts];// initiate the vertex array
		Log.d("loadMesh", "number of vertices::"+mesh.numverts);
		
		loadVertex(match,mesh.vertex);// load the vertex data for this mesh
		Log.d("loadMesh", "loaded vertex");

		mesh.numtris=Integer.parseInt(labelValue(match, MESH_NUMTRIS_LABEL));// find number of triangles label and save the value
		mesh.tri=new Triangle[mesh.numtris]; //initiate the triangle array 
		Log.d("loadMesh", "number of triangles:"+mesh.numtris);
		
		loadTriangles(match,mesh.tri); // load the triangle data
		Log.d("loadMesh", "loaded triangles");

		mesh.numweights=Integer.parseInt(labelValue(match, MESH_NUMWEIGHTS_LABEL));// find number of weights label and save the value
		mesh.weights=new Weight[mesh.numweights];// initiate the weight array
		Log.d("loadMesh", "number of weights:"+mesh.numweights);
		
		loadWeights(match,mesh.weights); // load the weight data
		Log.d("loadMesh", "loaded weights");
		
	}
	
	/**
	 * loads weights of a Mesh
	 * @param match Matcher of the mesh
	 * @param weights the array that the result will be saved. Must been initiated before this call
	 */
	private void loadWeights(Matcher match, Weight[] weights) {
		int index=-1; // weight index
		int joint; // joint the weight depends on
		float bias; // bias of the weight
		float x,y,z; // weights position in space
		for(int i=0;i<weights.length;i++){// for every weight
			try{
			match.find();//'weight'
			
			match.find();// find index 
			index=Integer.parseInt(match.group()); 
			match.find(); // find joint
			joint=Integer.parseInt(match.group());
			match.find(); // find bias
			bias=Float.parseFloat(match.group());
			
			match.find(); // find x position
			x=Float.parseFloat(match.group());
			match.find(); // find y position
			y=Float.parseFloat(match.group());
			match.find(); // find z position
			z=Float.parseFloat(match.group());
			
			weights[i]=new Weight(index, joint, bias, new Vec3(x, y, z));
			}catch(NumberFormatException ne){
				throw new ModelParseException("could not parse weight["+i+"](index "+index+")");
			}
		}
		
	}

	/**
	 * load vertex data of a Mesh
	 * @param match the Matcher of the mesh
	 * @param vertex the array that the result will be saved. Must been initiated before this call
	 */
	private void loadVertex(Matcher match, Vertex[] vertex) {
		int index=-1;
		float s,t;
		int startWeight,countWeight;
		for(int i=0;i<vertex.length;i++){// for every vertex
			try{
			match.find();//vert
			
			match.find(); // find index 
			index=Integer.parseInt(match.group()); 
			match.find(); // find s tex coord
			s=Float.parseFloat(match.group());
			match.find(); // find t tex coord
			t=Float.parseFloat(match.group());
			match.find(); // find sart weight
			startWeight=Integer.parseInt(match.group());
			match.find(); // find number of weights
			countWeight=Integer.parseInt(match.group());
			
			vertex[i]=new Vertex(index, s, t, startWeight, countWeight);
			}catch(NumberFormatException ne){
				throw new ModelParseException("could not parse vertex["+i+"](index "+index+")");
			}
		}
	}
	
	/**
	 * load triangle data of a Mesh
	 * @param match the Matcher of the mesh 
	 * @param triangles the array that the result will be saved. Must been initiated before this call
	 */
	private void loadTriangles(Matcher match, Triangle[] triangles) {
		int index=-1; // triangle index
		int i0,i1,i2; // vertex index 0,1 and 2
		
		for(int i=0;i<triangles.length;i++){// for every triangle
			try{
			match.find();//tri
			
			match.find();// find triangle index 
			index=Integer.parseInt(match.group()); 
			match.find(); // find vertex index 0
			i0=Integer.parseInt(match.group());
			match.find(); // find vertex index 1
			i1=Integer.parseInt(match.group());
			match.find(); // find vertex index 2
			i2=Integer.parseInt(match.group());
			
			triangles[i]=new Triangle(index, i0, i1, i2);
			}catch(NumberFormatException ne){
				throw new ModelParseException("could not parse tri["+i+"](index "+index+")");
			}
		}
	}

}
