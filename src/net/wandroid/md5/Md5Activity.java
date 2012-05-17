package net.wandroid.md5;


import java.io.IOException;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


import net.wandroid.md5.ioutils.AssetModelFileOpener;
import net.wandroid.md5.model.Md5;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.ConfigurationInfo;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;
/**
 * Activity that displays a MD5 model.
 * MD5 model is the file format for quake 4 that supports skeleton animation.
 * The model will be rendered by opengl es 2.0
 * @author Jungbeck
 *
 */
public class Md5Activity extends Activity {
	
    private static final String MODEL_FOLDER = "model/"; // folder path of the model
    private static final String MODEL_NAME = "boblampclean"; // model name, there should be a file <MODEL_NAME>.md5mesh
    private static final String ERROR_MSG="An error occured, please restart application"; 
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); 
        setContentView(R.layout.main);
        loadMainMenu();
    }
    
    /**
     * onClick method for loading the main menu
     * @param v the clicked view
     */
    public void loadMainMenu(View v){
        //don't forget to make screen orientation possible
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        loadMainMenu();
    }
    
    /**
     * the loading method for the main menu. It will start the animation of the sky and the falling leaf
     */
    private void loadMainMenu(){
        setContentView(R.layout.main);
        ImageView iv=(ImageView) findViewById(R.id.skyImage);
        Animation anim=AnimationUtils.loadAnimation(this, R.anim.skyalpha);
        iv.startAnimation(anim);
        
        iv=(ImageView) findViewById(R.id.leafImage);
        anim=AnimationUtils.loadAnimation(this, R.anim.leafanim);
        iv.startAnimation(anim);
    }
    
    /**
     * onClick method for showing info.
     * @param v
     */
    public void loadInfo(View v){
        setContentView(R.layout.info);
    }
    
    /**
     * onclick method for quitting
     * @param v
     */
    public void quit(View v){
        finish();
    }
    
    /**
     * onClick method for displaying the openGL content
     * @param v
     */
    public void loadGlContent(View v){
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        if(!deviceSupportOpenGl2()){// remember that some devices, such as the emulator does not support opengl es 2
            displayToast("Device does not support gles 2.0, cannot start");
            finish();
            return;
        }
        
        Md5 md5=new Md5(); 
        try {
            AssetManager assetManager= getAssets(); // if models are stored in the assets folder, then we need an assetManager
            AssetModelFileOpener modelOpener=new AssetModelFileOpener(assetManager); // this object will handle how the files will be opened
            
            // in case file should be loaded from sdcard, the next line should be used instead  
            //SdCardOpener modelOpener=new SdCardOpener(Environment.getExternalStorageDirectory()+"/");
            
            md5.loadFile(modelOpener, MODEL_FOLDER,MODEL_NAME);// open MODEL_NAME files in the MODEL_FOLDER folder
            
            
            setContentView(R.layout.glcontent);
            GLSurfaceView view = (GLSurfaceView) findViewById(R.id.gl2view);
            view.setEGLContextClientVersion(2);//enable gles 2
            view.setRenderer(new Md5Renderer(md5));
            
            
            
        } catch (IOException e) {
            displayToast(ERROR_MSG);
            finish();
        } catch(RuntimeException e){
            displayToast(ERROR_MSG);
            finish();
        }
 
        
    }
    
    /**
     * Displays a toast with length set to LENGTH_LONG
     * @param message the text to be displayed
     */
    private void displayToast(String message){
        Toast toast =Toast.makeText(this, message, Toast.LENGTH_LONG);
        toast.show();
     }
    
    /**
     * returns true if device supports opengl es 2+
     * @return true if device supports opengl es 2+, false otherwise 
     */
    private boolean deviceSupportOpenGl2(){
        ActivityManager activityManager= (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        ConfigurationInfo confInfo=activityManager.getDeviceConfigurationInfo();
        return confInfo.reqGlEsVersion>=0x20000;
    }
    
    /**
     * onClick method for reloading the gl content.
     * This method will reread the whole model again, and will disable rendering while doing so.
     * @param v
     */
    public void reloadModel(View v){
        // to disable rendering of current model we need to set a new render object, and to set a new render object, we need to create a new GlSurfaceView
        GLSurfaceView view = new GLSurfaceView(this);
        view.setRenderer(new Renderer() {
            
            @Override
            public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            }
            
            @Override
            public void onSurfaceChanged(GL10 gl, int width, int height) {
            }
            
            @Override
            public void onDrawFrame(GL10 gl) {
            }
        });
        setContentView(view);
        loadGlContent(v);
    }
    
}