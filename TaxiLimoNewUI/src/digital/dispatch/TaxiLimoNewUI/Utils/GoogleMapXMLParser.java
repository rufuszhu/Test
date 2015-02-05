package digital.dispatch.TaxiLimoNewUI.Utils;

import android.content.Context;
import android.location.Address;
import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Locale;

import digital.dispatch.TaxiLimoNewUI.R;


public class GoogleMapXMLParser {
    private static final String ns = null;
    private static final String TAG = "GecoderGoogle";
    private int mMaxResult = 0;
    private Locale mLocale;
    private Context mContext;
    private boolean logEnabled;
    
    public GoogleMapXMLParser(int maxResult, Locale locale, Context context, boolean enableLog)
    {
    	super();
    	mMaxResult = maxResult;
    	mLocale = locale;
    	mContext = context;
    	logEnabled = enableLog;
    }
    
    public static String convertStreamToString ( InputStream in)
    {
    	InputStreamReader is = new InputStreamReader(in);
    	StringBuilder sb=new StringBuilder();
    	BufferedReader br = new BufferedReader(is);
    	String read = null;
		try {
			read = br.readLine();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

    	while(read != null) {
    	    //System.out.println(read);
    	    sb.append(read);
    	    try {
				read =br.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}

    	}
    	return sb.toString();   	
    }

    // This class represents a single result in the google map response.
    public class AddressInfo {
        public String formattedAddr;
        public ArrayList<String> typeList;
        public String latitude;
        public String longitude;
        public ArrayList<AddressComponent> addrComp;
        
        public AddressInfo()
        {
        	super();
        	latitude = "";
        	longitude = "";
        	addrComp = null;
        	formattedAddr = null;
        	typeList = new ArrayList<String>();
        }
        
        public void setLat (String lat)
        {
        	latitude = lat;
        }
        
        public void setLng (String lng)
        {
        	longitude = lng;
        }
    }
        
    public static class AddressComponent {
        public final String long_name;
        public final String short_name;
        public final ArrayList<String> typeList;

        private AddressComponent(String longName, String shortName, ArrayList<String> typeList) {
            this.long_name = longName;
            this.short_name = shortName;
            this.typeList = typeList;
        }
    }

    public ArrayList<Address> parse(InputStream in) throws XmlPullParserException, IOException {
        try {
 //       	String streamText = convertStreamToString (in);
 //       	Log.v(TAG,streamText);
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return readResultGroup(parser);
        } finally {
            in.close();
        }
    }

    private ArrayList<Address> readResultGroup(XmlPullParser parser) throws XmlPullParserException, IOException {
    	
        ArrayList<Address> addressList = new ArrayList<Address>();
        ArrayList<AddressInfo> addrInfoList = new ArrayList<AddressInfo>();
        AddressInfo addrInfo;
        Address addr;
 //       Log.v(TAG, "++readResultGroup");
        parser.require(XmlPullParser.START_TAG, ns, "GeocodeResponse");
        String statusCode;
        int evtType;
        while (parser.next() != XmlPullParser.END_TAG) {
        	evtType = parser.getEventType();
 //       	Log.v(TAG, "evtType = " + evtType);
            if (evtType != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("result")) {
            	addrInfo = readResult(parser);
				if (isStreetAddress(addrInfo)) {

					if (addrInfo != null) {
						addr = ResultToAddress(addrInfo);
						if (mMaxResult == 0) {
							addressList.add(addr);
						} else {
							if (addressList.size() < mMaxResult) {
								addressList.add(addr);
							} else {
								if (logEnabled)
									Logger.e(TAG,
										"abandon address for reaching the limit of "
												+ mMaxResult);
							}
						}
					}
				}
				else 
				{
					if ( addrInfo != null)
					{
						addrInfoList.add(addrInfo);
					}
				}
            }
            else if ( name.equals("status"))
            {
            	statusCode = readText(parser);
            	
            	if (logEnabled) {
	            	if ( statusCode.equals("OK")) {
	            		Log.v(TAG, "google map server return OK");
	            	}
	            	else {
	            		Log.v(TAG, "google map server return false");
	            	}
            	}
            }
            else {
                skip(parser);
            }
        }
        if ( addressList.size() == 0)
        {
        	if (addrInfoList.size() > 0)
        	{
        		addrInfo = addrInfoList.get(0);
				if (addrInfo != null) {
					addr = ResultToAddress(addrInfo);
					addressList.add(addr);					
				}
        	}
    
        }
        return addressList;
    }
    
    private AddressInfo readResult(XmlPullParser parser) throws XmlPullParserException, IOException {
        AddressInfo result = new AddressInfo();
        result.addrComp = new ArrayList<AddressComponent>();
        AddressComponent ac;
//        Log.v(TAG, "++readResult");
        parser.require(XmlPullParser.START_TAG, ns, "result");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if ( name.equals("type"))
            {
            	String type = readType(parser);
            	result.typeList.add(type);
            }
            else if ( name.equals("formatted_address"))
            {
            	result.formattedAddr = readText(parser);
            }
            else if ( name.equals("geometry"))
            {
            	readLocation(parser, result);
            }
            else if (name.equals("address_component"))
            {
            	ac = readAddressComponent(parser);
            	result.addrComp.add(ac);
            }
            else
            {
            	skip(parser);
            }
        }
 /*
        if ( !isStreetAddress(result))
        {
        	//abandon result if it is not street address
        	result = null;
        }
*/
        return result;
    }
    
    private void readLocation(XmlPullParser parser, AddressInfo addrInfo) throws XmlPullParserException, IOException
    {
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("location")) {
                while (parser.next() != XmlPullParser.END_TAG) {
                    if (parser.getEventType() != XmlPullParser.START_TAG) {
                        continue;
                    }
                    String subName = parser.getName();
                    if (subName.equals("lat")) 
                    {
                    	addrInfo.latitude = readLat(parser);
                    }
                    else if (subName.equals("lng")) 
                    {
                    	addrInfo.longitude =  readLng(parser);
                    } else 
                    {
                        skip(parser);
                    }
                }

            } else {
                skip(parser);
            }
        }
    }
    
    // Parses the address component
    private AddressComponent readAddressComponent(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "address_component");
        AddressComponent addrComp = null;
        String longName = null;
        String shortName = null;
        String type = null;
        ArrayList<String> typeList = new ArrayList<String>();
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("long_name")) {
                longName = readLongName(parser);
            } else if (name.equals("short_name")) {
                shortName = readShortName(parser);
            } else if (name.equals("type")) {
                type = readType(parser);
                typeList.add(type);
            } else {
                skip(parser);
            }
        }
        addrComp =new AddressComponent(longName, shortName, typeList);
        return addrComp;
    }

    // Processes title tags in the feed.
    private String readLongName(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "long_name");
        String longName = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "long_name");
        return longName;
    }
    
    private String readType(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "type");
        String type = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "type");
        return type;
    }

    // Processes summary tags in the feed.
    private String readShortName(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "short_name");
        String shortName = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "short_name");
        return shortName;
    }
    
    private String readLat(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "lat");
        String lat = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "lat");
        return lat;
    }
    
    private String readLng(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "lng");
        String lng = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "lng");
        return lng;
    }

    // For the tags title and summary, extracts their text values.
    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    // Skips tags the parser isn't interested in. Uses depth to handle nested tags. i.e.,
    // if the next tag after a START_TAG isn't a matching END_TAG, it keeps going until it
    // finds the matching END_TAG (as indicated by the value of "depth" being 0).
    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
            case XmlPullParser.END_TAG:
                    depth--;
                    break;
            case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }
    
    private Address ResultToAddress ( AddressInfo adrInfo)
    {
    	if ( adrInfo == null)
    	{
    		return null;
    	}
    	Address addr = new Address(getLocale());
  
    	double lat, lng;
    	
    	lat =Double.parseDouble(adrInfo.latitude);
    	lng =Double.parseDouble(adrInfo.longitude);
    	addr.setLatitude(lat);
    	addr.setLongitude(lng);
    	for ( AddressComponent addrCpt: adrInfo.addrComp)
    	{
			for (String type : addrCpt.typeList) {
				if (type.equals("route")) {
					addr.setThoroughfare(addrCpt.long_name);

				} else if (type.equals("locality")) {
					addr.setLocality(addrCpt.long_name);
				} else if (type.equals("administrative_area_level_1")) {
					addr.setAdminArea(addrCpt.long_name);
				} else if (type.equals("country")) {
					addr.setCountryCode(addrCpt.short_name);
					addr.setCountryName(addrCpt.long_name);
				} else if (type.equals("administrative_area_level_2")) {
					addr.setSubAdminArea(addrCpt.long_name);
				} else if (type.equals("postal_code")) {
					addr.setPostalCode(addrCpt.long_name);
				} else if (type.equals("political")) {
					continue;
				} else {

				}
			}
    	}
      	ResultToAddressConvertFmtAddr ( addr, adrInfo);
      	addr.setFeatureName(addr.getAddressLine(0));
    	return addr;
    }
    
    private void ResultToAddressConvertFmtAddr ( Address addr, AddressInfo adrInfo)
    {
    	String fmtAddrArray[] = adrInfo.formattedAddr.split(",");
    	int size = fmtAddrArray.length;
    	boolean streetNameFirst = false;
    	String tmpStr = null;
    	if (( size == 1) || ( size == 2))
    	{
        	for ( int i= 0; i<fmtAddrArray.length; i++)
        	{
        		addr.setAddressLine(i, fmtAddrArray[i]);
        	}  		
    	}
    	else if ( size ==3 )
    	{
    		streetNameFirst = checkAddressFormat ( addr.getCountryCode());
    		if ( streetNameFirst)
    		{
            	for ( int i= 0; i<fmtAddrArray.length; i++)
            	{
            		addr.setAddressLine(i, fmtAddrArray[i]);
            	}     			
    		}
    		else
    		{
    			tmpStr = fmtAddrArray[0] + "," + fmtAddrArray[1];
    			addr.setAddressLine(0, tmpStr);
    			addr.setAddressLine(1, fmtAddrArray[2]);
    		}
    	}
    	else 
    	{
    		addr.setAddressLine(0, fmtAddrArray[0]);
    		tmpStr = fmtAddrArray[1] + "," + fmtAddrArray[2];
    		addr.setAddressLine(1, tmpStr);
    		addr.setAddressLine(2, fmtAddrArray[3]);		
    	}
    		

    }
    
    private Locale getLocale()
    {
    	return mLocale;
    }
    
    private boolean isStreetAddress(AddressInfo adrInfo)
    {
    	boolean bResult = false;
    	if (adrInfo == null)
    	{
    		return bResult;
    	}
    	for ( String type :adrInfo.typeList)
    	{
    		if ( type.equals("street_address"))
    		{
    			bResult = true;
    			break;
    		}
    		else if ( type.equals("route"))
    		{
    			bResult = true;
    			break;
    		}
    	}
    	return bResult;  	
    }
    
	private boolean checkAddressFormat(String countryCode) {
		String[] strFirstCountries = mContext.getResources().getStringArray(R.array.street_name_first_country_code);
		boolean bResult = false;
		if ( countryCode == null)
		{
			
		}
		else {
			for (int i = 0; i < strFirstCountries.length; i++) {
				if (countryCode.equalsIgnoreCase(strFirstCountries[i])) {
					bResult = true;
					break;
				}
			}
		}
		
		return bResult;
	}
    
}
