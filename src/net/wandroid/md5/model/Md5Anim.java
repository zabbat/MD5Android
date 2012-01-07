package net.wandroid.md5.model;

/**
 * Class containing data for animations for md5
 * @author Jungbeck
 *
 */
public class Md5Anim {

	protected int numFrames; // number of frame in the animation
	protected int numJoints; // number of joints in the animation
	protected int frameRate; // frame rate of the animiation
	protected int numAnimatedComponents; // the number of components that will change between the animations  
	protected Hierarchy[] hierachy; // holding information of the skelton hierarchy. one for ever joint.
	protected BaseFrame baseFrame; // base frame from the rest of the frames are derivered from
	protected Frame[] frames; // frames of the animation. consist of changes of the base frame

	
}
