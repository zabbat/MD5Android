package net.wandroid.md5;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

/**
 * 
 * This class is used to be able to add android gui components on a gl surface.
 * To add a gl surface in a layout xml file, it need to be a custom class, and have the two following constructors.
 *
 */
public class Gl2SurfaceView extends GLSurfaceView{
    public Gl2SurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public Gl2SurfaceView(Context context) {
        super(context);
    }

}
