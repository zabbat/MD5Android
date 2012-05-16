package net.wandroid.md5;


import java.io.IOException;

import net.wandroid.md5.ioutils.AssetModelFileOpener;
import net.wandroid.md5.model.Md5;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.ConfigurationInfo;
import android.content.res.AssetManager;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
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
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        
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
			
	        //GLSurfaceView view=new GLSurfaceView(this);
            setContentView(R.layout.glmain);
            GLSurfaceView view = (GLSurfaceView) findViewById(R.id.gl2view);
	        view.setEGLContextClientVersion(2);//enable gles 2
	        view.setRenderer(new Md5Renderer(md5));
	        //setContentView(view);
	        
	        
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
}