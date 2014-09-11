package digital.dispatch.TaxiLimoNewUI.Utils;

import digital.dispatch.TaxiLimoNewUI.Task.GetMBParamTask;
import android.app.Application;
import android.os.AsyncTask;

public class AppController extends Application {

	public static final String TAG = AppController.class.getSimpleName();
	private static AppController mInstance;

	@Override
	public void onCreate() {
		super.onCreate();
		mInstance = this;
		new GetMBParamTask(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	public static synchronized AppController getInstance() {
		return mInstance;
	}


}