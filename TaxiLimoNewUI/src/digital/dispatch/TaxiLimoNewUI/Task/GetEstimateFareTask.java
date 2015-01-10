package digital.dispatch.TaxiLimoNewUI.Task;

import java.io.StringReader;
import java.net.URI;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;


import digital.dispatch.TaxiLimoNewUI.Utils.Logger;

import android.os.AsyncTask;
import android.widget.TextView;

//Get fare estimate and load onto screen
public class GetEstimateFareTask extends AsyncTask<String, Integer, Integer> {
	
	private static final String TAG = "GetEstimateFareTask";

	TextView fareView;
	private int baseRate;
	private int ratePerDistance;
	

	
	public GetEstimateFareTask(TextView fareView, int baseRate, int ratePerDistance) {

		this.fareView = fareView;
		this.baseRate = baseRate;
		this.ratePerDistance = ratePerDistance;
		
	}
	// The code to be executed in a background thread.
	@Override
	protected Integer doInBackground(String... params) {
		try {
			
				Logger.v(TAG, "from: " + params[0] + ", to: " + params[1]);
			

				/* Create a URL we want to load some xml-data from. */
				DefaultHttpClient hc = new DefaultHttpClient();
				ResponseHandler<String> res = new BasicResponseHandler();
				String url = "http://maps.googleapis.com/maps/api/directions/xml?" + "origin=" + params[0] + "&destination=" + params[1]
						+ "&sensor=false&units=metric&mode=" + params[2];
				URI uri = new URI(url.replace(' ', '+'));
				HttpPost postMethod = new HttpPost(uri);
				String response = hc.execute(postMethod, res);
	
				/* Parse the xml-data from our URL. */
				InputSource inputSource = new InputSource();
				inputSource.setEncoding("UTF-8");
				inputSource.setCharacterStream(new StringReader(response));
	
				XPathFactory factory = XPathFactory.newInstance();
				XPath xpath = factory.newXPath();
	
				// Because the evaluator may return multiple entries, we specify that the expression
				// return a NODESET and place the result in a NodeList.
				NodeList disNode = (NodeList) xpath.evaluate("/DirectionsResponse/route/leg//value", inputSource, XPathConstants.NODESET);
	
				return Integer.parseInt(disNode.item(disNode.getLength() - 1).getTextContent());
		} catch (XPathExpressionException ex) {
			
				Logger.e(TAG, "XPath Error: " + ex.toString());
			

			return null;
		} catch (Exception e) {
			
				Logger.e(TAG, e.toString());
			

			return null;
		}
	}
	
	@Override
	protected void onPostExecute(Integer distance) {
			
		if (distance != null) {
			double baseRateInCents = (double) baseRate;
			double dynamicRateCentPerKM = (double) ratePerDistance;
			double finalFare = (baseRateInCents + (dynamicRateCentPerKM * distance / 1000)) / 100;
			fareView.setText("$" + (int)Math.ceil(finalFare));
		}
		
	}

}
