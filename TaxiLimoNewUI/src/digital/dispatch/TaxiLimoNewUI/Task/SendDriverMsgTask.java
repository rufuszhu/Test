package digital.dispatch.TaxiLimoNewUI.Task;

import android.content.Context;
import android.os.AsyncTask;

import com.digital.dispatch.TaxiLimoSoap.requests.Request.IRequestTimerListener;
import com.digital.dispatch.TaxiLimoSoap.requests.SendDriverMsgRequest;
import com.digital.dispatch.TaxiLimoSoap.requests.SendDriverMsgRequest.ISendDriverMsgResponseListener;
import com.digital.dispatch.TaxiLimoSoap.responses.SendDriverMsgResponse;

import digital.dispatch.TaxiLimoNewUI.R;
import digital.dispatch.TaxiLimoNewUI.Utils.Logger;
import digital.dispatch.TaxiLimoNewUI.Utils.MBDefinition;
import digital.dispatch.TaxiLimoNewUI.Utils.Utils;



public class SendDriverMsgTask extends AsyncTask<Void, Integer, Void> implements ISendDriverMsgResponseListener, IRequestTimerListener {
	private static String  TAG = "SendDriverMsgTask"; 
	private SendDriverMsgRequest sdmReq;
	private Context _context;
	private String trID, msg, systemID, destID;
    
	public SendDriverMsgTask(String taxiRideID, String sysID, String dID, String message, Context c) {
		trID = taxiRideID;
		systemID = sysID;
		destID = dID;
		msg = message;
		_context = c;
	}
	
    //Before running code in separate thread  
    @Override  
    protected void onPreExecute()  
    {
		Logger.v(TAG, "sendDriverMSG Task: onPreExecute");

    }

    //The code to be executed in a background thread.  
    @Override  
    protected Void doInBackground(Void... params)  
    {  
        try {
        	sdmReq = new SendDriverMsgRequest(this, this);
        	sdmReq.setSysID(systemID);
        	sdmReq.setDestID(destID);
        	sdmReq.setMGVersion(MBDefinition.OSP_VERSION);
        	sdmReq.setDestination(trID); // trip ID
        	sdmReq.setDestinationTypeID("9"); // 9 for mobile booker, trip ID type
        	sdmReq.setMessage(msg);
        	sdmReq.setDeliveryTime(""); // blank will be assume as ASAP in DDS System
        	sdmReq.setPriority("H");
        	sdmReq.sendRequest(_context.getString(R.string.name_space), _context.getString(R.string.url));
        }  
        catch (Exception e) {  
        		Logger.e(TAG, e.toString());
        	
        	e.printStackTrace();  
        }
        
        return null;
    }
    
    @Override  
    protected void onProgressUpdate(Integer... values) {  

    }  
    
    @Override
    public void onResponseReady(SendDriverMsgResponse response) {
    	

    	Utils.showMessageDialog(_context.getString(R.string.message_track_send_msg_success), _context);
    }

	@Override
	public void onErrorResponse(String errorString) {


		
		Utils.showMessageDialog(_context.getString(R.string.err_msg_no_response), _context);
			Logger.v(TAG, "error response: " + errorString);
	}

	@Override
	public void onError() {

		
		Utils.showMessageDialog(_context.getString(R.string.err_msg_no_response), _context);
			Logger.v(TAG, "no response");
	}
	
	@Override
	public void onProgressUpdate(int progress) {

	}
}