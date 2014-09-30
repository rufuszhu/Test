package digital.dispatch.TaxiLimoNewUI.Task;

import java.io.InputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.widget.ImageView;
import digital.dispatch.TaxiLimoNewUI.R;
import digital.dispatch.TaxiLimoNewUI.Utils.Logger;

public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

	ImageView bmImage;
	public DownloadImageTask(ImageView bmImage) {
		this.bmImage = bmImage;
	}

	protected Bitmap doInBackground(String... urls) {
		String urldisplay = urls[0];
		Bitmap mIcon11 = null;
		try {
			InputStream in = new java.net.URL(urldisplay).openStream();
			mIcon11 = BitmapFactory.decodeStream(in);
		} catch (Exception e) {
			Logger.e("Error", e.getMessage());
			e.printStackTrace();
		
		}
		return mIcon11;
	}

	protected void onPostExecute(Bitmap result) {
		if(result==null)
			bmImage.setBackgroundResource(R.drawable.ic_launcher);
		else
			bmImage.setImageBitmap(result);
	}

}