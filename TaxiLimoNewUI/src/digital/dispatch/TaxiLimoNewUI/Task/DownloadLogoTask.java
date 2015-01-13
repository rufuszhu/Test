package digital.dispatch.TaxiLimoNewUI.Task;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;
import digital.dispatch.TaxiLimoNewUI.Utils.Logger;

public class DownloadLogoTask extends AsyncTask<Void, Integer, Bitmap> {
	private Context ctx;
	private ImageView logoIMV;
	private String imgURL, destFile;
    
	public DownloadLogoTask(String imageUrl, String dFile, ImageView imv, Context context) {
		imgURL = imageUrl;
		logoIMV = imv;
		ctx = context;
		destFile = dFile;
	}

    //The code to be executed in a background thread.  
    @Override  
    protected Bitmap doInBackground(Void... params)  
    {  
    	Logger.d(imgURL);
    	Logger.d(destFile);
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
    	
	    logoIMV.setImageBitmap(result);
    }
}
