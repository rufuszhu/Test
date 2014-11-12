package digital.dispatch.TaxiLimoNewUI.Utils;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.android.volley.toolbox.ImageLoader;
import digital.dispatch.TaxiLimoNewUI.Task.GetMBParamTask;
import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v4.util.LruCache;

public class AppController extends Application {

	public static final String TAG = AppController.class.getSimpleName();
	private static AppController mInstance;
	private RequestQueue mRequestQueue;
	private ImageLoader mImageLoader;
	private static Context context;

	@Override
	public void onCreate() {
		super.onCreate();
		mInstance = this;
		new GetMBParamTask(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		mRequestQueue = Volley.newRequestQueue(getApplicationContext());
		mImageLoader = new ImageLoader(this.mRequestQueue, new ImageLoader.ImageCache() {
			private final LruCache<String, Bitmap> mCache = new LruCache<String, Bitmap>(10);

			public void putBitmap(String url, Bitmap bitmap) {
				mCache.put(url, bitmap);
			}

			public Bitmap getBitmap(String url) {
				return mCache.get(url);
			}
		});
		
	}
	
	public static Context getAppContext() {
        return AppController.context;
    }

	public static synchronized AppController getInstance() {
		return mInstance;
	}
	
	public RequestQueue getRequestQueue(){
        return this.mRequestQueue;
    }
 
    public ImageLoader getImageLoader(){
        return this.mImageLoader;
    }

}