package digital.dispatch.TaxiLimoNewUI.Task;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.digital.dispatch.TaxiLimoSoap.requests.Request.IRequestTimerListener;
import com.digital.dispatch.TaxiLimoSoap.requests.TokenizationRequest;
import com.digital.dispatch.TaxiLimoSoap.requests.TokenizationRequest.ITokenizationResponseListener;
import com.digital.dispatch.TaxiLimoSoap.responses.TokenizationResponse;

import digital.dispatch.TaxiLimoNewUI.Installation;
import digital.dispatch.TaxiLimoNewUI.R;
import digital.dispatch.TaxiLimoNewUI.Utils.Logger;
import digital.dispatch.TaxiLimoNewUI.Utils.MBDefinition;
import digital.dispatch.TaxiLimoNewUI.Utils.Utils;

public class DeleteCreditCardTask extends AsyncTask<String, Integer, Void> implements ITokenizationResponseListener, IRequestTimerListener {
	private static final String TAG = "DeleteCreditCardTask";
	TokenizationRequest tokenReq;
	Context context;
	
	public DeleteCreditCardTask(Context context) {
		this.context = context;
	}

	// The code to be executed in a background thread.
	@Override
	protected Void doInBackground(String... params) {
		try {
			Integer sequenceNum = Integer.valueOf((int) (Math.random() * (MBDefinition.MDT_MAX_SEQUENCE_NUM + 1)));

			tokenReq = new TokenizationRequest(this, this);
			tokenReq.setDeviceID(Utils.getHardWareId(context));
			tokenReq.setSequenceNum(sequenceNum.toString());
			tokenReq.setReqType("3");
			tokenReq.setToken(params[0]);

			tokenReq.sendRequest(context.getString(R.string.name_space), context.getString(R.string.url));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public void onResponseReady(TokenizationResponse response) {
		// User doesn't need to know if the token is actually deleted at server
		// Do this at background, don't show msg
		Logger.v(TAG, "Deletion Successful: " + response.GetResponseCode() + " :: " + response.GetResponseMsg());
	}

	@Override
	public void onErrorResponse(TokenizationResponse res) {
		Logger.v(TAG, "response return error: " + res.GetResponseCode() + " - " + res.GetResponseMsg() + " :: " + res.getErrorString());
	}

	@Override
	public void onError() {
		Log.v(TAG, "no response");
	}

	@Override
	public void onProgressUpdate(int progress) {
	}

}
