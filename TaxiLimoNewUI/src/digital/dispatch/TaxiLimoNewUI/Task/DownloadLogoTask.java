package digital.dispatch.TaxiLimoNewUI.Task;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;

import com.digital.dispatch.TaxiLimoSQLDatabase.MBCompanyDBHandling;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

public class DownloadLogoTask extends AsyncTask<Void, Integer, Bitmap> {
	private Context ctx;
	private ImageView logoIMV;
	private int destID, logoVersion;
	private String imgURL, destFile, companyName, attributes;
	private MBCompanyDBHandling mCDb;
    
	public DownloadLogoTask(String imageUrl, String dFile, int dID, int logoVer, ImageView imv, 
			String compName, String attrs, MBCompanyDBHandling mbCDB, Context context) {
		destID = dID;
		logoVersion = logoVer;
		imgURL = imageUrl;
		destFile = dFile;
		mCDb = mbCDB;
		logoIMV = imv;
		companyName = compName;
		attributes = attrs;
		ctx = context;
	}

    //The code to be executed in a background thread.  
    @Override  
    protected Bitmap doInBackground(Void... params)  
    {  
    	Bitmap logoBitmap = null;
    	
        try {                
        	// save image to phone
        	URL url = new URL(imgURL);
    		InputStream is = url.openStream();
    		FileOutputStream os = ctx.openFileOutput(destFile, Context.MODE_PRIVATE);

    		byte[] b = new byte[2048];
    		int length;

    		while ((length = is.read(b)) != -1) {
    			os.write(b, 0, length);
    		}
    		
    		is.close();
    		os.close();
    		
    		// get image from phone
    		InputStream fis = ctx.openFileInput(destFile);
    		logoBitmap = BitmapFactory.decodeStream(fis);
        }  
        catch (Exception e) {  
            e.printStackTrace();
        }  
        
        return logoBitmap;  
    }
    
    protected void onPostExecute(Bitmap result) {
    	// clear out destID after finish download
//    	SharedPreferences sharedPref = ctx.getSharedPreferences("mobile_booker", 0);
//		SharedPreferences.Editor prefEditor = sharedPref.edit();
		//String dIDList = sharedPref.getString(CompanyListAdapter.prefDestID, "");
		
//		String[] destIDArray = dIDList.split(",");
//		dIDList = "";
//		
//		for (int i = 0; i < destIDArray.length; i ++) {
//			if (!destIDArray[i].isEmpty() && Integer.parseInt(destIDArray[i]) != destID) {
//				dIDList += destIDArray[i] + ",";
//			}
//		}
//		
//		prefEditor.putString(CompanyListAdapter.prefDestID, dIDList);
//		prefEditor.commit();
//		
//		// save the location of image to database
//		MBCompany company = mCDb.getCompanyByDestId(destID);
//		boolean isNewComp = false;
//		
//		if (company == null) {
//			isNewComp = true;
//			company = new MBCompany();
//			company.setDestID(destID);
//		}
//		
//		company.setCompanyName(companyName);
//		company.setAttributes(attributes);
//		
//		// use default app icon for company that doesn't have logo set-up
//		// if the company already have logo but the new updated one doesn't work just use the original one
//		if (result == null) {
//			if (company.getLogoLocation().equalsIgnoreCase("")) {
//				result = BitmapFactory.decodeResource(ctx.getResources(), R.drawable.ic_launcher);
//			}
//			else {
//				try {
//					InputStream fis = ctx.openFileInput(company.getLogoLocation());
//					result = BitmapFactory.decodeStream(fis);
//				} 
//				catch (FileNotFoundException e) {
//					if (Integer.parseInt(ctx.getResources().getString(R.string.enable_log)) == 1) {
//						e.printStackTrace();
//						Log.e("DownloadTask", "can't open local saved logo: " + e.toString());
//					}
//				}
//			}
//		}
//		else {
//			company.setLogoLocation(destFile);
//			company.setLogoVersion(logoVersion);
//		}
//		
//        if (isNewComp) {
//        	mCDb.addCompany(company);
//        }
//        else {
//        	mCDb.updateCompany(company);
//        }
//        
//		mCDb.printAllCompanies();
//			
//	    logoIMV.setImageBitmap(result);
    }
}
