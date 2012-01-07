package net.wandroid.md5.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.util.Log;

import net.wandroid.md5.model.math.Quaternions;
import net.wandroid.md5.model.math.Vec3;
import net.wandroid.md5.util.Tick;

public class Md5AnimReader extends Md5Reader{

	public Md5Anim load(BufferedReader reader) throws IOException {
		String animFile=loadFileToString(reader);
		
		
		Md5Anim md5Anim=new Md5Anim();
		Tick t = new Tick();
		t.start();
		loadHeader(animFile, md5Anim);
		t.tock("loaded header");
		loadHierarchy(animFile, md5Anim);
		t.tock("loaded hierarchy");
		loadBaseFrame(animFile, md5Anim);
		t.tock("loaded baseframe");
		loadFrames(animFile, md5Anim);
		t.tock("loaded frames");
		for(Frame f:md5Anim.frames){
			f.calculateJointsPositionRelativeParent();
		}
		t.tock("calculated relative joints");
		return md5Anim;
	}

	protected void loadHeader(String animFile,Md5Anim md5Anim){
		// check if version is 10
		Pattern labelIntPattern = Pattern.compile("[\\w\\d\\\"]+",
				Pattern.MULTILINE);
		Matcher match = labelIntPattern.matcher(animFile.substring(0, 200));//just check the first 200 chars

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

		//load number of frames
		value=labelValue(match, "numFrames");
		try{
			md5Anim.numFrames=Integer.parseInt(value);
		}catch (NumberFormatException ne){
			throw new ModelParseException("could not parse value:"+value);
		}
		md5Anim.frames=new Frame[md5Anim.numFrames];
		Log.d("loadHeader","numFrames:"+md5Anim.numFrames);
		
		//load number of joints
		value=labelValue(match, "numJoints");
		try{
			md5Anim.numJoints=Integer.parseInt(value);
		}catch (NumberFormatException ne){
			throw new ModelParseException("could not parse value:"+value);
		}
		md5Anim.hierachy=new Hierarchy[md5Anim.numJoints];
		Log.d("loadHeader","numJoints:"+md5Anim.numJoints);
		
		//load frame rate
		value=labelValue(match, "frameRate");
		try{
			md5Anim.frameRate=Integer.parseInt(value);
		}catch (NumberFormatException ne){
			throw new ModelParseException("could not parse value:"+value);
		}
		Log.d("loadHeader","frameRate:"+md5Anim.frameRate);

		//load number of animated components
		value=labelValue(match, "numAnimatedComponents");
		try{
			md5Anim.numAnimatedComponents=Integer.parseInt(value);
		}catch (NumberFormatException ne){
			throw new ModelParseException("could not parse value:"+value);
		}
		Log.d("loadHeader","numAnimatedComponents:"+md5Anim.numAnimatedComponents);
	}
	
	protected void loadHierarchy(String animFile,Md5Anim md5Anim){
		Pattern jointsPattern=Pattern.compile("hierarchy\\s+\\{[^\\}]+\\}", Pattern.MULTILINE);
		Matcher match=jointsPattern.matcher(animFile);
		if(!match.find()){
			throw new ModelParseException("could not find hierarchy");
		}
		String hierarchy=match.group();
		
		Pattern pattern=Pattern.compile("//[\\s\\w\\d.]+", Pattern.MULTILINE);
		match=pattern.matcher(hierarchy);
		hierarchy=match.replaceAll(""); //remove all comments
		pattern=Pattern.compile("[^\\s]+", Pattern.MULTILINE);
		match=pattern.matcher(hierarchy);
		int start=hierarchy.indexOf("{");
		match.find(start); //start from first '{' 
		
		String name=null;
		int parent;
		int flag;
		int startOffset;
		for(int i=0;i<md5Anim.numJoints;i++){
			try{
				match.find(); name=match.group();
				match.find(); parent=Integer.parseInt(match.group());
				match.find(); flag=Integer.parseInt(match.group());
				match.find(); startOffset=Integer.parseInt(match.group());
				md5Anim.hierachy[i]=new Hierarchy(name, parent, flag,startOffset);
			}catch(NumberFormatException ne){
				throw new ModelParseException("could not parse heirarchy["+i+"]("+name+")");
			}
			
		}

	}
	
	protected void loadBaseFrame(String animFile,Md5Anim md5Anim){
		md5Anim.baseFrame=new BaseFrame(md5Anim.numJoints);
		Pattern baseframePattern=Pattern.compile("baseframe\\s*\\{[^\\}]+\\}", Pattern.MULTILINE);
		Matcher match=baseframePattern.matcher(animFile);
		if(!match.find()){
				throw new ModelParseException("could not find baseframe ");
		}
		String baseframeSection=match.group();
		baseframePattern=Pattern.compile("[^\\s()]+", Pattern.MULTILINE);
		match=baseframePattern.matcher(baseframeSection);
		match.find();//baseframe
		match.find();//{
		for(int i=0;i<md5Anim.numJoints;i++){
			try{
	
			match.find();float px=Float.parseFloat(match.group());
			match.find();float py=Float.parseFloat(match.group());
			match.find();float pz=Float.parseFloat(match.group());
			match.find();float qx=Float.parseFloat(match.group());
			match.find();float qy=Float.parseFloat(match.group());
			match.find();float qz=Float.parseFloat(match.group());
			md5Anim.baseFrame.pos[i]=new Vec3(px,py,pz);
			md5Anim.baseFrame.q[i]=new Quaternions(qx,qy,qz);
			}catch(NumberFormatException ne){
				throw new ModelParseException("could not parse baseframe joint["+i+"]");
			}
		}
		
	}

	protected void loadFrames(String animFile,Md5Anim md5Anim){
		
		Pattern framesPattern=Pattern.compile("frame\\s\\d+\\s*\\{[^\\}]+\\}", Pattern.MULTILINE);
		Matcher match=framesPattern.matcher(animFile);
		for(int i=0;i<md5Anim.numFrames;i++){
			if(!match.find()){
					throw new ModelParseException("could not find frame "+i);
			}
			md5Anim.frames[i]=loadFrame(match.group(),md5Anim);
		}
		
	}
	
	private Frame loadFrame(String frameSection,Md5Anim md5Anim){
		Pattern patternNoSpace=Pattern.compile("[^\\s]+", Pattern.MULTILINE); //TODO: should not be recompiled each frame	
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
