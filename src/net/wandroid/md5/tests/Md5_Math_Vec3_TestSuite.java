package net.wandroid.md5.tests;

import net.wandroid.md5.model.math.Vec3;
import junit.framework.TestCase;

/**
 * Unit tests for Vec3- class
 * @author Jungbeck
 *
 */
public class Md5_Math_Vec3_TestSuite extends TestCase{

    private static final float TEST_X=0;
    private static final float TEST_Y=1;
    private static final float TEST_Z=2;
    private static final float TEST_SCALE=0.5f;
    
    
    
    private Vec3 mVec;
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        
        mVec=new Vec3(TEST_X, TEST_Y, TEST_Z);
        
    }
    
	public void test_Vec3_Getters(){
		assertEquals(TEST_X, mVec.getX(), Vec3.VEC_DELTA);
		assertEquals(TEST_Y, mVec.getY(), Vec3.VEC_DELTA);
		assertEquals(TEST_Z, mVec.getZ(), Vec3.VEC_DELTA);
	}

	public void test_Vec3_Equals(){
	    assertEquals(mVec,mVec);
	    Vec3 v=new Vec3(TEST_Z, TEST_Y, TEST_X);
	    assertFalse(mVec.equals(v));
	}
	
	public void test_Vec3_Scale(){
	    Vec3 v=new Vec3(TEST_X*TEST_SCALE, TEST_Y*TEST_SCALE, TEST_Z*TEST_SCALE);
	    assertTrue(v.equals(mVec.scale(TEST_SCALE)));
	}
	
	
	public void test_Vec3_Add(){
	    Vec3 v=new Vec3(TEST_X+TEST_X, TEST_Y+TEST_Y, TEST_Z+TEST_Z);
	    assertTrue(mVec.add(mVec).equals(v));
	}
	
	public void test_Vec3_Normalize(){
	    mVec.normalize();
	    // the absolute value of a normalized vector should be 1.0
	    float absVal=(float)Math.sqrt(mVec.getX()*mVec.getX()+mVec.getY()*mVec.getY()+mVec.getZ()*mVec.getZ());
	    assertEquals(1.0f, absVal,Vec3.VEC_DELTA);
	}
	
}
