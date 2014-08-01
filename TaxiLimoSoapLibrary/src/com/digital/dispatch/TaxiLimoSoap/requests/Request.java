package com.digital.dispatch.TaxiLimoSoap.requests;

import java.util.Timer;
import java.util.TimerTask;
import android.util.Log;

import com.digital.dispatch.TaxiLimoSoap.GlobalVar;
import com.digital.dispatch.TaxiLimoSoap.transport.ServiceConnection;

public class Request {
	protected IRequestTimerListener iTimerListener = null;
	protected boolean isReqCancelled = false;
	private TimerTask timeChecker;
	
	public Request (IRequestTimerListener listener) {
		iTimerListener = listener;
	}
	
	public void sendRequest(String ns, String url) {
		// for child class to impelment
	}
	
	protected void startProgress() {
		Timer timer = new Timer();
	    timeChecker = new TimerTask() {
        	int progress = 0;
    		
        	@Override
        	public void run() {
        	    if (progress <= 100) {
        	    	iTimerListener.onProgressUpdate(progress);
        	    	progress ++;
        	    }
        	    else {
        	    	if (GlobalVar.logEnable) {
        	    		Log.v("Request", "time out");
        	    	}
        	    	timeChecker.cancel();
        	    }
        	}
        };
        
        if (!isReqCancelled) {
        	timer.scheduleAtFixedRate(timeChecker, 0L, (long)(ServiceConnection.DEFAULT_TIMEOUT / 100));
        }
	}
	
	protected void finishProgress() {
		if (GlobalVar.logEnable) {
			Log.v("Request", "stop");
		}
		
		iTimerListener.onProgressUpdate(100);
		timeChecker.cancel();
	}
	
	public void cancelProgress() {
		timeChecker.cancel();
		isReqCancelled = true;
	}
	
	public interface IRequestTimerListener {
		public void onProgressUpdate(int progress);
	}
}
