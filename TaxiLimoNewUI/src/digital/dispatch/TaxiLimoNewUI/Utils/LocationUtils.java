/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package digital.dispatch.TaxiLimoNewUI.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.android.gms.maps.model.LatLng;

import digital.dispatch.TaxiLimoNewUI.R;
import android.content.Context;
import android.location.Address;
import android.location.Location;

/**
 * Defines app-wide constants and utilities
 */
public final class LocationUtils {
	public static final String APPTAG = "LocationUtils";

	// Name of shared preferences repository that stores persistent state
	public static final String SHARED_PREFERENCES = "digital.dispatch.TaxiLimoNewUI.SHARED_PREFERENCES";

	// Key for storing the "updates requested" flag in shared preferences
	public static final String KEY_UPDATES_REQUESTED = "digital.dispatch.TaxiLimoNewUI.KEY_UPDATES_REQUESTED";

	/*
	 * Define a request code to send to Google Play services This code is returned in Activity.onActivityResult
	 */
	public final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

	/*
	 * Constants for location update parameters
	 */
	// Milliseconds per second
	public static final int MILLISECONDS_PER_SECOND = 1000;

	// The update interval
	public static final int UPDATE_INTERVAL_IN_SECONDS = 5;

	// A fast interval ceiling
	public static final int FAST_CEILING_IN_SECONDS = 1;

	// Update interval in milliseconds
	public static final long UPDATE_INTERVAL_IN_MILLISECONDS = MILLISECONDS_PER_SECOND * UPDATE_INTERVAL_IN_SECONDS;

	// A fast ceiling of update intervals, used when the app is visible
	public static final long FAST_INTERVAL_CEILING_IN_MILLISECONDS = MILLISECONDS_PER_SECOND * FAST_CEILING_IN_SECONDS;

	// Create an empty string for initializing strings
	public static final String EMPTY_STRING = new String();

	public static final Map<String, String> states = new HashMap<String, String>();
	static {
		states.put("Alabama", "AL");
		states.put("Alaska", "AK");
		states.put("Alberta", "AB");
		states.put("American Samoa", "AS");
		states.put("Arizona", "AZ");
		states.put("Arkansas", "AR");
		states.put("Armed Forces (AE)", "AE");
		states.put("Armed Forces Americas", "AA");
		states.put("Armed Forces Pacific", "AP");
		states.put("British Columbia", "BC");
		states.put("California", "CA");
		states.put("Colorado", "CO");
		states.put("Connecticut", "CT");
		states.put("Delaware", "DE");
		states.put("District Of Columbia", "DC");
		states.put("Florida", "FL");
		states.put("Georgia", "GA");
		states.put("Guam", "GU");
		states.put("Hawaii", "HI");
		states.put("Idaho", "ID");
		states.put("Illinois", "IL");
		states.put("Indiana", "IN");
		states.put("Iowa", "IA");
		states.put("Kansas", "KS");
		states.put("Kentucky", "KY");
		states.put("Louisiana", "LA");
		states.put("Maine", "ME");
		states.put("Manitoba", "MB");
		states.put("Maryland", "MD");
		states.put("Massachusetts", "MA");
		states.put("Michigan", "MI");
		states.put("Minnesota", "MN");
		states.put("Mississippi", "MS");
		states.put("Missouri", "MO");
		states.put("Montana", "MT");
		states.put("Nebraska", "NE");
		states.put("Nevada", "NV");
		states.put("New Brunswick", "NB");
		states.put("New Hampshire", "NH");
		states.put("New Jersey", "NJ");
		states.put("New Mexico", "NM");
		states.put("New York", "NY");
		states.put("Newfoundland", "NF");
		states.put("North Carolina", "NC");
		states.put("North Dakota", "ND");
		states.put("Northwest Territories", "NT");
		states.put("Nova Scotia", "NS");
		states.put("Nunavut", "NU");
		states.put("Ohio", "OH");
		states.put("Oklahoma", "OK");
		states.put("Ontario", "ON");
		states.put("Oregon", "OR");
		states.put("Pennsylvania", "PA");
		states.put("Prince Edward Island", "PE");
		states.put("Puerto Rico", "PR");
		states.put("Quebec", "PQ");
		states.put("Rhode Island", "RI");
		states.put("Saskatchewan", "SK");
		states.put("South Carolina", "SC");
		states.put("South Dakota", "SD");
		states.put("Tennessee", "TN");
		states.put("Texas", "TX");
		states.put("Utah", "UT");
		states.put("Vermont", "VT");
		states.put("Virgin Islands", "VI");
		states.put("Virginia", "VA");
		states.put("Washington", "WA");
		states.put("West Virginia", "WV");
		states.put("Wisconsin", "WI");
		states.put("Wyoming", "WY");
		states.put("Yukon Territory", "YT");
		states.put("Alberta", "AB");
		states.put("British Columbia", "BC");
		states.put("Manitoba", "MB");
		states.put("New Brunswick", "NB");
		states.put("Newfoundland and Labrador", "NL");
		states.put("Northwest Territories", "NT");
		states.put("Nova Scotia", "NS");
		states.put("Nunavut", "NU");
		states.put("Ontario", "ON");
		states.put("Prince Edward Island", "PE");
		states.put("Quebec", "QC");
		states.put("Saskatchewan", "SK");
		states.put("Yukon", "YT");
	}

	/**
	 * Get the latitude and longitude from the Location object returned by Location Services.
	 * 
	 * @param currentLocation
	 *            A Location object containing the current location
	 * @return The latitude and longitude of the current location, or null if no location is available.
	 */
	public static String getLatLng(Context context, Location currentLocation) {
		// If the location is valid
		if (currentLocation != null) {

			// Return the latitude and longitude as strings
			return context.getString(R.string.latitude_longitude, currentLocation.getLatitude(), currentLocation.getLongitude());
		} else {

			// Otherwise, return the empty string
			return EMPTY_STRING;
		}
	}

	public static String addressToString(Context context, Address address) {
		if (address != null && context != null) {
			// Format the first line of address
			String addressText = context.getString(R.string.address_output_string,

			// If there's a street address, add it
					address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "",

					// Locality is usually a city
					address.getLocality());
			// Return the text
			return addressText;
		} else
			return "";
	}

	public static Location addressToLocation(Address address) {
		Location loc = new Location("");
		loc.setLatitude(address.getLatitude());
		loc.setLongitude(address.getLongitude());
		return loc;
	}

	public static LatLng addressToLatLng(Address address) {
		return new LatLng(address.getLatitude(), address.getLongitude());
	}

	public static LatLng locationToLatLng(Location location) {
		return new LatLng(location.getLatitude(), location.getLongitude());
	}

	public static ArrayList<String> addressListToStringList(Context context, List<Address> addresses) {
		ArrayList<String> temp = new ArrayList<String>();
		for (int i = 0; i < addresses.size(); i++) {
			temp.add(addressToString(context, addresses.get(i)));
		}
		return temp;
	}
}
