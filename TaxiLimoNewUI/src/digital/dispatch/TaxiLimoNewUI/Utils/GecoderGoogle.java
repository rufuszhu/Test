package digital.dispatch.TaxiLimoNewUI.Utils;

import android.content.Context;
import android.location.Address;
import android.util.Log;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Locale;

public class GecoderGoogle {
  private static final String TAG="GecoderGoogle";
  private Context mContext;
  private Locale mLocale;
  private boolean logEnabled;
	
  public GecoderGoogle(Context context, Locale locale, boolean enableLog)
  {
	  super();
	  mContext = context;
	  mLocale = locale;
	  logEnabled = enableLog;
  }
		
  public List<Address> getFromLocation(double latitude, double longitude, int maxResult) throws IOException
  {
		InputStream stream = null;
		GoogleMapXMLParser googleMapXmlParser = new GoogleMapXMLParser(maxResult, mLocale, mContext, logEnabled);
		String urlString;
		StringBuilder strBuilder = new StringBuilder(
				"https://maps.googleapis.com/maps/api/geocode/xml?");
		strBuilder.append("latlng=");
		strBuilder.append(latitude);
		strBuilder.append(",");
		strBuilder.append(longitude);
		strBuilder.append("&sensor=true");
		urlString = strBuilder.toString();
		urlString = urlString.replaceAll(" ", "%20");
		List<Address> results = null;
		if (logEnabled) {
			Log.v(TAG, "++getFromLocation");
			Log.v(TAG, "url=" + urlString);
		}
		try {
			stream = downloadUrl(urlString);
			if (logEnabled)
				Log.v(TAG, "get stream");
			try {
				results = googleMapXmlParser.parse(stream);
			} catch (XmlPullParserException e) {
				e.printStackTrace();
			}
		} finally {
			if (stream != null) {
				stream.close();
			}
		}
		if (logEnabled)
			Log.v(TAG, "entries number = " + results.size());
		return results;
	}
  
	public List<Address> getFromLocationName(String locationName, int maxResult)
			throws IOException {
		InputStream stream = null;
		GoogleMapXMLParser googleMapXmlParser = new GoogleMapXMLParser(maxResult, mLocale, mContext, logEnabled);
		String urlString;
		StringBuilder strBuilder = new StringBuilder(
				"https://maps.googleapis.com/maps/api/geocode/xml?");
		strBuilder.append("address=");
		strBuilder.append(locationName);
		strBuilder.append("&sensor=false");
		urlString = strBuilder.toString();
		urlString = urlString.replaceAll(" ", "%20");
		List<Address> results = null;
		if (logEnabled) {
			Log.v(TAG, "++getFromLocationName");
			Log.v(TAG, "url=" + urlString);
		}
		try {
			stream = downloadUrl(urlString);
			if (logEnabled)
				Log.v(TAG, "get stream");
			try {
				results = googleMapXmlParser.parse(stream);
			} catch (XmlPullParserException e) {
				e.printStackTrace();
			}
		} finally {
			if (stream != null) {
				stream.close();
			}
		}
		if (logEnabled)
		{
			Log.v(TAG, "entries number = " + results.size());
			for (Address adr : results) {
				Log.v(TAG, "---------------");
				Log.v(TAG, adr.toString());
			}
		}
		return results;
	}
  
  private InputStream downloadUrl(String urlString) throws IOException {
      URL url = new URL(urlString);
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      conn.setReadTimeout(10000 /* milliseconds */);
      conn.setConnectTimeout(15000 /* milliseconds */);
      conn.setRequestMethod("GET");
      conn.setDoInput(true);
      // Starts the query
      conn.connect();
      InputStream stream = conn.getInputStream();
      return stream;
  }
}
