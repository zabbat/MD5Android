package net.wandroid.md5;

import java.io.File;
import java.io.IOException;

import net.wandroid.md5.model.Md5;
import android.app.Activity;
import android.content.res.AssetManager;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

public class Md5Activity extends Activity {
	
	private static final  String path=Environment.getExternalStorageDirectory()+"/model/";
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	Log.v("","start");
        super.onCreate(savedInstanceState);
        AssetManager assetManager= getAssets();
        
        Md5 md5=new Md5();
        try {
            AssetModelOpener modelOpener=new AssetModelOpener(assetManager);
			md5.loadFile(modelOpener, "model/","boblampclean");
		} catch (IOException e) {
			e.printStackTrace();
			finish();
			
		}
        GLSurfaceView view=new GLSurfaceView(this);
        view.setEGLContextClientVersion(2);//enable gl es 2
        view.setRenderer(new Md5Renderer(md5));
        setContentView(view);
    }
}