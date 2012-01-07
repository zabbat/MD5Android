package net.wandroid.md5.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.util.Log;

import net.wandroid.md5.Tick;
import net.wandroid.md5.model.math.Quaternion;
import net.wandroid.md5.model.math.Vec3;

/**
 * Class responsible for loading a .md5anim file, containing animation data
 * 
 * @author Jungbeck
 *
 */
public class Md5AnimFileLoader extends Md5Reader{

    private static final String ANIM_VERSION_LABEL = "md5version"; // version label
    private static final String ANIM_COMMANDLINE_LABEL = "commandline"; // command line label
    private static final String ANIM_NUMFRAMES_LABEL = "numFrames"; // number of frames label
    private static final String ANIM_NUMJOINTS_LABEL = "numJoints"; // number of joints label
    private static final String ANIM_FRAMERATE_LABEL = "frameRate"; // frame rate label
    private static final String ANIM_NUMANIMCOMP_LABEL = "numAnimatedComponents"; // number of animated components label
    private static final String ANIM_SUPPORTED_VERSION="10";
    private static final int ANIM_HEADER_MAX = 200;
    
    /**
     * Loads animation data from a file and returns it as a Md5Anim instance
     * @param reader a BufferedReader connected to the animation file
     * @return if successfully loaded , a Md5Anim instance is returned
     * @throws IOException if the file failed to load
     */
	public Md5Anim load(BufferedReader reader) throws IOException {
		String animFile=loadFileToString(reader); // read the file to a string
		Md5Anim md5Anim=new Md5Anim();
		Tick t = new Tick();
		t.start(); // start measure time
		loadHeader(animFile, md5Anim); // load the header
		t.tock("loaded header");
		loadHierarchy(animFile, md5Anim); // load the hierarchy data
		t.tock("loaded hierarchy"); 
		loadBaseFrame(animFile, md5Anim); // loads the base frame
		t.tock("loaded baseframe");
		loadFrames(animFile, md5Anim); // loads animation frames
		t.tock("loaded frames");
		for(Frame f:md5Anim.frames){ // for every frame
		 // calculate the position of the joints based on their parents position
			f.calculateJointsPositionRelativeParent();
		}
		t.tock("calculated relative joints");
		reader.close();
		return md5Anim;
	}


	/**
	 * 
	 * Loads the header information from the file. The header structure looks like the following :
	 * 
	 * MD5Version 10
	 * commandline "string"
	 * 
	 * numFrames int
	 * numJoints int
	 * frameRate int
	 * numAnimatedComponents int
	 * 
	 * 
	 * This loader supports Md5 version 10, it might be able to load other versions too, but for now it will jsut throw an exception
	 * if any other verison is found
	 * @param animFile reference to the file as a string
	 * @param md5Anim reference to the data file the header info should be loaded to
	 * @exception ModelParseException if the file could not be parsed, or version is not 10
	 */
	protected void loadHeader(String animFile,Md5Anim md5Anim) throws ModelParseException{
		// check for version
		Pattern labelIntPattern = Pattern.compile("[\\w\\d\\\"]+",Pattern.MULTILINE);
		Matcher match = labelIntPattern.matcher(animFile.substring(0, ANIM_HEADER_MAX));//just check for the header in the beginning of the file
		String value=labelValue(match, ANIM_VERSION_LABEL); // check that we found the version string, and return its value
		if (!value.equals(ANIM_SUPPORTED_VERSION)) {
			throw new ModelParseException(
					"Not correct md5 version, expected "+ANIM_SUPPORTED_VERSION+", but was " + value);
		}
		
		//load command line
		value=labelValue(match, ANIM_COMMANDLINE_LABEL);
		if(!value.equals("\"\"")){//command line options are ignored
			Log.d("loadHeader", "warning commandline is ignored: "+value);
		}

		//load number of frames
		value=labelValue(match, ANIM_NUMFRAMES_LABEL);
		try{
			md5Anim.numFrames=Integer.parseInt(value);
		}catch (NumberFormatException ne){
			throw new ModelParseException("could not parse value:"+value);
		}
		md5Anim.frames=new Frame[md5Anim.numFrames];// initiate frames in md5Anim
		Log.d("loadHeader","numFrames:"+md5Anim.numFrames);
		
		//load number of joints
		value=labelValue(match, ANIM_NUMJOINTS_LABEL);
		try{
			md5Anim.numJoints=Integer.parseInt(value);
		}catch (NumberFormatException ne){
			throw new ModelParseException("could not parse value:"+value);
		}
		md5Anim.hierachy=new Hierarchy[md5Anim.numJoints];// initiate hierarchy in md5Anim
		Log.d("loadHeader","numJoints:"+md5Anim.numJoints);
		
		//load frame rate
		value=labelValue(match, ANIM_FRAMERATE_LABEL);
		try{
			md5Anim.frameRate=Integer.parseInt(value);
		}catch (NumberFormatException ne){
			throw new ModelParseException("could not parse value:"+value);
		}
		Log.d("loadHeader","frameRate:"+md5Anim.frameRate);

		//load number of animated components
		value=labelValue(match, ANIM_NUMANIMCOMP_LABEL);
		try{
			md5Anim.numAnimatedComponents=Integer.parseInt(value);
		}catch (NumberFormatException ne){
			throw new ModelParseException("could not parse value:"+value);
		}
		Log.d("loadHeader","numAnimatedComponents:"+md5Anim.numAnimatedComponents);
	}
	
	/**
	 * Loads the hierarchy from the animation file
	 * The structure of hierarchy is:
	 * 
	 *  hierarchy {
	 *    "name"   parent flags startIndex
	 *    ...
	 *  }
	 * 
	 * there are numJoints entries in the hierarchy
	 * 
	 * @param animFile the animation file as a string
	 * @param md5Anim the Md5Anim reference to load the data to
	 * @exception ModelParseException if the file could not be parsed
	 */
	protected void loadHierarchy(String animFile,Md5Anim md5Anim)throws ModelParseException{
	    //find the start of the entries in the file
		Pattern jointsPattern=Pattern.compile("hierarchy\\s+\\{[^\\}]+\\}", Pattern.MULTILINE); 
		Matcher match=jointsPattern.matcher(animFile);
		if(!match.find()){// could not find the entries in the file
			throw new ModelParseException("could not find hierarchy");
		}
		String hierarchy=match.group();// the entries
		
		// find everything right of a comment '//' and the comment in the file
		Pattern pattern=Pattern.compile("//[\\s\\w\\d.]+", Pattern.MULTILINE); 
		match=pattern.matcher(hierarchy);
		hierarchy=match.replaceAll(""); //remove all comments
		pattern=Pattern.compile("[^\\s]+", Pattern.MULTILINE);// finally ,find groups that does not contain any whitespace
		match=pattern.matcher(hierarchy);
		int start=hierarchy.indexOf("{"); // do matching after the starting '{'
		match.find(start);
		
		String name=null; // the name of the joint
		int parent; // the parent of the joint
		int flag; // flag of the joint
		int startOffset; // start offset of the joint
		for(int i=0;i<md5Anim.numJoints;i++){
			try{
				match.find(); // find name 
				name=match.group();
				
				match.find(); // find parent
				parent=Integer.parseInt(match.group());
				
				match.find(); // find flag
				flag=Integer.parseInt(match.group());
				
				match.find();  // find startOffset
				startOffset=Integer.parseInt(match.group());
				
				md5Anim.hierachy[i]=new Hierarchy(name, parent, flag,startOffset);
			}catch(NumberFormatException ne){
				throw new ModelParseException("could not parse heirarchy["+i+"]("+name+")");
			}
			
		}

	}
	
	/**
	 * Loads the base frame from the animation file
	 * 
	 * the structure of the base frame is:
	 * 
	 * baseframe {
	 *    ( pos.x pos.y pos.z ) ( orient.x orient.y orient.z )
	 *    ...
	 * }
	 * 
	 * There are numJoints entries
	 * 
     * @param animFile the animation file as a string
     * @param md5Anim the Md5Anim reference to load the data to
     * @exception ModelParseException if the file could not be parsed
	 */
	protected void loadBaseFrame(String animFile,Md5Anim md5Anim)throws ModelParseException{
		md5Anim.baseFrame=new BaseFrame(md5Anim.numJoints);// create a new BaseFrame instance
		Pattern baseframePattern=Pattern.compile("baseframe\\s*\\{[^\\}]+\\}", Pattern.MULTILINE);
		Matcher match=baseframePattern.matcher(animFile);
		if(!match.find()){// could not find the entry
				throw new ModelParseException("could not find baseframe ");
		}
		//TODO: should comments be removes?
		String baseframeSection=match.group();
		baseframePattern=Pattern.compile("[^\\s()]+", Pattern.MULTILINE);// find groups not consisting of brackets or white spaces 
		match=baseframePattern.matcher(baseframeSection);
		match.find();//ignore first find - 'baseframe'
		match.find();//ignore second find - '{'
		for(int i=0;i<md5Anim.numJoints;i++){
			try{
	
			match.find();// find x position
			float px=Float.parseFloat(match.group());
			match.find();// find y position
			float py=Float.parseFloat(match.group());
			match.find();// find z position
			float pz=Float.parseFloat(match.group());
			match.find();// find x for quaternion rotation 
			float qx=Float.parseFloat(match.group());
			match.find();// find y for quaternion rotation
			float qy=Float.parseFloat(match.group());
			match.find();// find z for quaternion rotation
			float qz=Float.parseFloat(match.group());
			
			md5Anim.baseFrame.pos[i]=new Vec3(px,py,pz);
			md5Anim.baseFrame.q[i]=new Quaternion(qx,qy,qz);
			}catch(NumberFormatException ne){
				throw new ModelParseException("could not parse baseframe joint["+i+"]");
			}
		}
		
	}
	
   /**
    * Loads the frames from the animation file
    * 
    * the structure of the frames is:
    * 
    * frame frameIndex {
    *   float float float ...
    * }
    * 
    * @param animFile the animation file as a string
    * @param md5Anim the Md5Anim reference to load the data to
    * @exception ModelParseException if the file could not be parsed
    */
	protected void loadFrames(String animFile,Md5Anim md5Anim)throws ModelParseException{
		
		Pattern framesPattern=Pattern.compile("frame\\s\\d+\\s*\\{[^\\}]+\\}", Pattern.MULTILINE);
		Matcher match=framesPattern.matcher(animFile);
		
		//recompiling is costly and to avoid it for every frame, the pattern is compiled here
		Pattern patternNoSpace=Pattern.compile("[^\\s]+", Pattern.MULTILINE);  
		
		for(int i=0;i<md5Anim.numFrames;i++){
			if(!match.find()){// could not find the entry
					throw new ModelParseException("could not find frame "+i);
			}
			md5Anim.frames[i]=loadFrame(match.group(),md5Anim,patternNoSpace);
		}
		
	}
	
	/**
	 * loads a frame, containing numAnimatedComponents floats
	 * @param frameSection the frame section as a string
	 * @param md5Anim the Md5Anim reference to load the data to
	 * @param patternNoSpace the compiled pattern for finding the floats in frameSection
	 * @return the loaded Frame
	 */
	private Frame loadFrame(String frameSection,Md5Anim md5Anim,Pattern patternNoSpace){
		Matcher match=patternNoSpace.matcher(frameSection);
		int start =frameSection.indexOf('{');
		match.find(start); // start from '{'
		float[] vals=new float[md5Anim.numAnimatedComponents];
		String s=null;
		for(int i=0;i<vals.length;i++){
			try{
				match.find();
				s=match.group();	
				float f=Float.parseFloat(s);
				vals[i]=f;
			}catch(NumberFormatException ne){
				throw new ModelParseException("could not parse frame value["+i+"]:"+s);
			}
		}
		
		return new Frame(vals,md5Anim.numJoints,md5Anim.hierachy);
	}
	
}
