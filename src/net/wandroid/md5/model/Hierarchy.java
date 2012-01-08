package net.wandroid.md5.model;

/**
 * Describes a Hierarchy for the joints
 * @author Jungbeck
 *
 */
public class Hierarchy {

	protected String mName; // name of the joint
	protected int mParent; // parent of the joint
	protected int mFlags; // flags of the joint
	protected int mStartOffset; // start offset of the joint

	/**
	 * Creates a hierarchy
	 * @param name name of the joint
	 * @param parent parent of the joint
	 * @param flags flags of the joint
	 * @param startOffset start offset for the joint
	 */
	public Hierarchy(String name,int parent, int flags, int startOffset) {
		this.mName = name;
		this.mParent=parent;
		this.mFlags = flags;
		this.mStartOffset = startOffset;
	}
	
	@Override
	public String toString() {
		return mName+" "+mParent+" "+mFlags+" "+mStartOffset;
	}
	
}
