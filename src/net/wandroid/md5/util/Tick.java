package net.wandroid.md5.util;

import android.os.SystemClock;
import android.util.Log;

public class Tick {

	private long start_time;
	
	public void start(){
		start_time=SystemClock.currentThreadTimeMillis(); 
	}
	
	
	public void tock(String mess){
		//float elapsed=(SystemClock.currentThreadTimeMillis()-start_time)/1000.0f;
		
		Log.v("tock:"+mess,"elapsed : "+(SystemClock.currentThreadTimeMillis()-start_time));
		start();
	}

	public void time(String mess){
		float elapsed=(SystemClock.currentThreadTimeMillis()-start_time)/1000.0f;
		Log.v("tock:"+mess,"elapsed : "+elapsed);
	}

	
}
