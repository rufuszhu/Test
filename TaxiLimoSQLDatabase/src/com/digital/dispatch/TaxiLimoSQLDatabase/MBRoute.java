package com.digital.dispatch.TaxiLimoSQLDatabase;

import android.util.Log;

public class MBRoute {
	private final String TAG = "MBDatabase";
	private final int FAVORITE_ROUTE = 1;
	
	private int id;
	private int favorite;
	private int routeBookingLink;
	private int pickupAddrLink;
	private int dropoffAddrLink;
	
	public MBRoute ()
	{
		id = 0;
		favorite = 0;
		routeBookingLink = 0;
		pickupAddrLink = 0;
		dropoffAddrLink = 0;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getFavorite() {
		return favorite;
	}

	public void setFavorite(int favorite) {
		this.favorite = favorite;
	}

	public int getRouteBookingLink() {
		return routeBookingLink;
	}

	public void setRouteBookingLink(int routeBookingLink) {
		this.routeBookingLink = routeBookingLink;
	}

	public int getPickupAddrLink() {
		return pickupAddrLink;
	}

	public void setPickupAddrLink(int pickupAddrLink) {
		this.pickupAddrLink = pickupAddrLink;
	}

	public int getDropoffAddrLink() {
		return dropoffAddrLink;
	}

	public void setDropoffAddrLink(int dropoffAddrLink) {
		this.dropoffAddrLink = dropoffAddrLink;
	}
	
	public void print()
	{
		if (DatabaseHandler.dbDebug) {
			Log.v(TAG, "----MBRout Object" + id + "----");
			if (favorite == FAVORITE_ROUTE) {
				Log.v(TAG, "Favorite route");
			} else {
				Log.v(TAG, "Regular route");
			}

			Log.v(TAG, "   Favorite = " + favorite);
			Log.v(TAG, "   routeBookingLink =" + routeBookingLink);
			Log.v(TAG, "   pickupAddrLink =" + pickupAddrLink);
			Log.v(TAG, "   dropoffAddrLink =" + dropoffAddrLink);
			Log.v(TAG, "--------------------");
			Log.v(TAG, " ");
		}
	}
}

