package net.wandroid.md5;

import java.io.File;
import java.io.IOException;

import net.wandroid.md5.ioutils.AssetModelFileOpener;
import net.wandroid.md5.model.Md5;
import android.app.Activity;
import android.content.res.AssetManager;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
/**
 * Activity that displays a MD5 model.
 * MD5 model is the file format for quake 4 that supports skeleton animation.
 * 
 * @author Jungbeck
 *
 */
public class Md5Activity extends Activity {
	
    private static final String MODEL_NAME = "model";
    private static final String MODEL_FOLDER = "boblampclean";

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);        
        Md5 md5=new Md5(); 
        try {
            AssetManager assetManager= getAssets(); // if models are stored in the assets folder, then we need an assetManager
            AssetModelFileOpener modelOpener=new AssetModelFileOpener(assetManager); // this object will handle how the files will be opened
            //SdCardOpener modelOpener=new SdCardOpener(Environment.getExternalStorageDirectory()+"/");
			md5.loadFile(modelOpener, MODEL_FOLDER,MODEL_NAME);// open MODEL_NAME files in the MODEL_FOLDER folder
		} catch (IOException e) {// in case files could not be found or parsed
			e.printStackTrace();//print stack trace
			finish(); // finish the application
			//TODO: toast error message?
		}
        GLSurfaceView view=new GLSurfaceView(this);
        view.setEGLContextClientVersion(2);//enable gl es 2
        view.setRenderer(new Md5Renderer(md5));
        setContentView(view);
    }
}