package com.digital.dispatch.TaxiLimoSoap.responses;

public class MGParam {
	private int useAccPW, tipButton1, baseRate, ratePerDistance, paymentTimeout;
	private boolean ccPaymentEnabled, msgToDriver, dropOffRequired, multiBookAllowed, sameLocBookAllowed;
	
	private final static boolean DEFAULT_BOOL = false;
	
	public MGParam() {
		useAccPW = 3;
		tipButton1 = 10;
		baseRate = 0;
		ratePerDistance = 0;
		paymentTimeout = 15;
		ccPaymentEnabled = false;
		msgToDriver = true;
		dropOffRequired = false; 
		multiBookAllowed = true;
		sameLocBookAllowed = false;
	}
	
	public MGParam(String bRate, String rPDistance, String msgDriver, String doMand, String multiBookAllow, 
			String sameLocBookAllow, String useAPW, String ccPayEnabled, String tip1, String pTimeOut) {
		if (useAPW != null) {
			useAccPW = Integer.parseInt(useAPW);
		}
		
		if (bRate != null) {
			baseRate = Integer.parseInt(bRate);
		}
		
		if (rPDistance != null) {
			ratePerDistance = Integer.parseInt(rPDistance);
		}
		
		tipButton1 = Integer.parseInt(tip1);
		paymentTimeout = Integer.parseInt(pTimeOut);
		ccPaymentEnabled = parseBool(ccPayEnabled);
		msgToDriver = parseBool(msgDriver);
		dropOffRequired = parseBool(doMand); 
		multiBookAllowed = parseBool(multiBookAllow);
		sameLocBookAllowed = parseBool(sameLocBookAllow);
	}
	
	public int getUseAccPW() {
		return useAccPW;
	}
	
	public int getTip1Btn() {
		return tipButton1;
	}
	
	public int getBaseRate() {
		return baseRate;
	}
	
	public int getRatePerDistance() {
		return ratePerDistance;
	}
	
	public int getPaymentTimeOut() {
		return paymentTimeout;
	}
	
	public boolean getCCPaymentEnabled() {
		return ccPaymentEnabled;
	}
	
	public boolean getMsgToDriver() {
		return msgToDriver;
	}
	
	public boolean getDropOffMand() {
		return dropOffRequired;
	}
	
	public boolean getMultiBookAllowed() {
		return multiBookAllowed;
	}
	
	public boolean getSameLocBookAllowed() {
		return sameLocBookAllowed;
	}
	
	private boolean parseBool(String str) {
		if (str.equalsIgnoreCase("Y")) {
			return true;
		}
		else if (str.equalsIgnoreCase("N")) {
			return false;
		}
		
		return DEFAULT_BOOL;
	}
}
