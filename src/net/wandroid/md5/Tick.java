package net.wandroid.md5;

import android.os.SystemClock;
import android.util.Log;

/**
 * A class handling performance messurement and writing to the log
 * @author Jungbeck
 *
 */
public class Tick {

	private long mStartTime; // the start time
	
	/**
	 * Starts recording the time
	 */
	public void start(){
		mStartTime=SystemClock.currentThreadTimeMillis(); 
	}
	
	/**
	 * takes the time since start and outputs it together with a message.Then it resets the time.
	 * @param mess message to the logcat
	 */
	public void tock(String mess){
		Log.d("tock:"+mess,"elapsed : "+(SystemClock.currentThreadTimeMillis()-mStartTime));
		start();
	}
	
	/**
	 * takes the time since start and outputs it together with a message.It does not reset the time.
	 * @param mess
	 */
	public void time(String mess){
		float elapsed=(SystemClock.currentThreadTimeMillis()-mStartTime)/1000.0f;
		Log.d("tock:"+mess,"elapsed : "+elapsed);
	}

	
}
