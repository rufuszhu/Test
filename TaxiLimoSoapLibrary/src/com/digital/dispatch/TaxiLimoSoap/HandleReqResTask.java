package com.digital.dispatch.TaxiLimoSoap;

import java.net.ConnectException;
import java.util.ArrayList;

import com.digital.dispatch.TaxiLimoSoap.serialization.PropertyInfo;
import com.digital.dispatch.TaxiLimoSoap.serialization.SoapObject;
import com.digital.dispatch.TaxiLimoSoap.serialization.SoapPrimitive;
import com.digital.dispatch.TaxiLimoSoap.serialization.SoapSerializationEnvelope;
import com.digital.dispatch.TaxiLimoSoap.transport.HttpTransportSE;

import android.os.AsyncTask;
import android.util.Log;

public class HandleReqResTask extends AsyncTask {
    private ICustomResponseListener iCustomResponseListener = null;
    private static String TAG = "SOAP";
    
    public HandleReqResTask(ICustomResponseListener listener) {
    	iCustomResponseListener = listener;
    }
    
	@Override
	protected Object doInBackground(Object... arg) {
		return sendRequest((MethodEnum)arg[0], (RequestEnum)arg[1], (ArrayList<DataParam>)arg[2], (String)arg[3], (String)arg[4]);
	}
    	
    @Override
    protected void onPostExecute(Object result) {
    	if (result.equals(GlobalVar.CONNECTION_ERROR) || result.equals(GlobalVar.GENERIC_ERROR)) {
    		iCustomResponseListener.onError();
    	}    	
    	else if (result instanceof SoapPrimitive) {
    		if (GlobalVar.logEnable) {
    			Log.v(TAG, "isPrimitive");
    		}
    		
    		iCustomResponseListener.onResponseReady(result);
    	}
    	else {
    		iCustomResponseListener.onResponseReady(result);
    	}
    }
    
    /*
     * Method for sending request and accepting response
     * 
     * Input: methodName = first level method name (i.e. GetAttributeList, RegisterDevice)
     * 		  reqName = second level method name (i.e. GetAttrListRequest, RegisterDeviceRequest)
     *        propertyList = the list of pairs of properties for this request
     * Output: responding SoapObject can be converted to specific request object for further use
     */
    private Object sendRequest(MethodEnum methodName, RequestEnum reqName, ArrayList<DataParam> propertyList, String ns, String url) {
    	SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		SoapObject req1 = new SoapObject(ns, methodName.toString());
		SoapObject req2 = new SoapObject(ns, reqName.toString());
		SoapObject response = null;
		SoapTypeWrapper soapWrap = null;

		for (int i = 0; i < propertyList.size(); i ++) {
			// parse attribute list for bookjob request only
			if (propertyList.get(i).name.equalsIgnoreCase("attributelist")) {
				if (GlobalVar.logEnable) {
					Log.v(TAG, propertyList.get(i).name + " :: " + propertyList.get(i).value);
				}
				
				String[] tokens = propertyList.get(i).value.split(",");
				
				for (int j = 0; j < tokens.length; j ++) {
					SoapObject attrSoap = new SoapObject("", "attributelist");
					attrSoap.addProperty("attrShortName", tokens[j]);
					req2.addSoapObject(attrSoap);
				}
			}
			else {
				req2.addProperty(propertyList.get(i).name, propertyList.get(i).value);
			}
		}
		
		/*PropertyInfo pi = new PropertyInfo();
		pi.setNamespace(ns);
		pi.setType(PropertyInfo.STRING_CLASS);
		pi.setName("destID");
		pi.setValue("1"); 
		req1.addProperty(pi);*/
		req1.addSoapObject(req2);
		envelope.setOutputSoapObject(req1);
		envelope.implicitTypes = true;
		envelope.setAddAdornments(false);
		
		try {
			HttpTransportSE ht = new HttpTransportSE(url);
			ht.debug = true;
			
			if (!methodName.equals(MethodEnum.RegDev)) { ht.call(ns + methodName, envelope);	}
			else { ht.call("", envelope); }
			
			if (GlobalVar.logEnable) {
				Log.v(TAG + "-request", ht.requestDump);
				
				int len = ht.responseDump.length();
				for ( int ix=0; ix<len; ix+=4000)
				{
					if ( ix+4000 < len)	{
						Log.v(TAG + "-response", ht.responseDump.substring(ix,ix+4000));
					}
					else
					{
						Log.v(TAG + "-response", ht.responseDump.substring(ix,len));
					}
				}
			}
			
			if (envelope.getResponse() instanceof SoapPrimitive) {
				return envelope.getResponse();
			}
			else {
				response = (SoapObject)envelope.getResponse();
				soapWrap = new SoapTypeWrapper(response, methodName);
				
				return soapWrap;
			}
		}
		catch (ConnectException ce) {
			if (GlobalVar.logEnable) {
				Log.v(TAG + "-ConnectError", ce.toString());
			}
			
			return GlobalVar.CONNECTION_ERROR;
		}
		catch (Exception e) {
			if (GlobalVar.logEnable) {
				Log.v(TAG + "-Error", e.toString());
			}
			
			return GlobalVar.GENERIC_ERROR;
		}
    }
    
    public interface ICustomResponseListener {
		public void onResponseReady(Object response);
		public void onError();
	}
}