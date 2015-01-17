package com.digital.dispatch.TaxiLimoSoap.responses;

public class MGParam {
	private int useAccPW, tipButton1, baseRate, ratePerDistance, paymentTimeout;
	private boolean ccPaymentEnabled, msgToDriver, dropOffRequired, multiBookAllowed, sameLocBookAllowed;
    private String supportPhone, supportEmail;
	
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
        supportEmail = null;
        supportPhone = null;

	}
	
	public MGParam(String bRate, String rPDistance, String msgDriver, String doMand, String multiBookAllow, 
			String sameLocBookAllow, String useAPW, String ccPayEnabled, String tip1, String pTimeOut, String supportEmail, String supportPhone) {
		if (useAPW != null) {
			useAccPW = Integer.parseInt(useAPW);
		}
		
		if (bRate != null) {
			baseRate = Integer.parseInt(bRate);
		}
		
		if (rPDistance != null) {
			ratePerDistance = Integer.parseInt(rPDistance);
		}
		if(tip1!=null)
			tipButton1 = Integer.parseInt(tip1);
		if(pTimeOut!=null)
			paymentTimeout = Integer.parseInt(pTimeOut);
		if(ccPayEnabled!=null)
			ccPaymentEnabled = parseBool(ccPayEnabled);
		if(msgDriver!=null)
			msgToDriver = parseBool(msgDriver);
		if(doMand!=null)
			dropOffRequired = parseBool(doMand); 
		if(multiBookAllow!=null)
			multiBookAllowed = parseBool(multiBookAllow);
		if(sameLocBookAllow!=null)
			sameLocBookAllowed = parseBool(sameLocBookAllow);
        //TL-379
        if(supportEmail!= null && supportEmail.trim().length()>0)
            this.supportEmail = supportEmail;

        if(supportPhone != null && supportPhone.trim().length()>0)
            this.supportPhone = supportPhone;
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

    public String getSupportPhone() {return supportPhone;}

    public String getSupportEmail() {return supportEmail;}
	
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
