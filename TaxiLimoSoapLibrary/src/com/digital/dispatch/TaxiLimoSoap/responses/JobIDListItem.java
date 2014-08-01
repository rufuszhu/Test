package com.digital.dispatch.TaxiLimoSoap.responses;

public class JobIDListItem {
	public int taxi_ride_id;
	public int fareEstimate;
	public String eta1;
	public String eta2;
	
	public JobIDListItem() {
		taxi_ride_id = 0;
		fareEstimate = 0;
		eta1 = "";
		eta2 = "";
	}
	
	public JobIDListItem(int trID, int fareEst, String e1, String e2) {
		taxi_ride_id = trID;
		fareEstimate = fareEst;
		eta1 = e1;
		eta2 = e2;
	}
}