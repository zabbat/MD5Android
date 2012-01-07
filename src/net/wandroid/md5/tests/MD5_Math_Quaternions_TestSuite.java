package net.wandroid.md5.tests;

import junit.framework.TestCase;
import net.wandroid.md5.model.math.Quaternion;
import net.wandroid.md5.model.math.Vec3;

public class MD5_Math_Quaternions_TestSuite extends TestCase{

    private Quaternion quat;
    
    private static final float TEST_X=0.0f;
    private static final float TEST_Y=1.0f;
    private static final float TEST_Z=2.0f;
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        
        quat=new Quaternion(TEST_X, TEST_Y, TEST_Z);
        
    }
    
    
    public void test_Quaternions_ToVec3(){
        assertTrue(quat.toVec3().equals(new Vec3(TEST_X, TEST_Y, TEST_Z)));
    }
    
    public void test_Quaternions_Rotate(){
        Quaternion qRot90DegreesAroundZ=
                new Quaternion((float)Math.toRadians(90.0f),new Vec3(0, 0, 1) );
        Vec3 xUnit=new Vec3(1, 0, 0);
        Vec3 yUnit=new Vec3(0,1,0);
        assertTrue(qRot90DegreesAroundZ.rotate(xUnit).equals(yUnit));
    }

    
    public void test_Quaternions_Inverse(){
        assertTrue(quat.toVec3().equals(new Vec3(-TEST_X, -TEST_Y, -TEST_Z)));
    }
    
    public void test_Quaternions_Mul(){
        Quaternion qRot45DegreesAroundZ=
                new Quaternion((float)Math.toRadians(45.0f),new Vec3(0, 0, 1) );
        Quaternion qRot90DegreesAroundZ=qRot45DegreesAroundZ.mul(qRot45DegreesAroundZ);
        Vec3 xUnit=new Vec3(1, 0, 0);
        Vec3 yUnit=new Vec3(0,1,0);
        assertTrue(qRot90DegreesAroundZ.rotate(xUnit).equals(yUnit));
    }
    
}
