package net.wandroid.md5.model;

/**
 * Class containing data for animations for md5
 * @author Jungbeck
 *
 */
public class Md5Anim {

	protected int mNumFrames; // number of frame in the animation
	protected int mNumJoints; // number of joints in the animation
	protected int mFrameRate; // frame rate of the animation
	protected int mNumAnimatedComponents; // the number of components that will change between the animations  
	protected Hierarchy[] mHierachy; // holding information of the skeleton hierarchy. one for ever joint.
	protected BaseFrame mBaseFrame; // base frame from the rest of the frames are derivered from
	protected Frame[] mFrames; // frames of the animation. consist of changes of the base frame

	
}
