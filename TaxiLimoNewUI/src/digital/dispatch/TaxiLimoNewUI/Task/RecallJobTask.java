package digital.dispatch.TaxiLimoNewUI.Task;

import android.content.Context;
import android.os.AsyncTask;

import com.digital.dispatch.TaxiLimoSoap.requests.RecallJobsRequest;
import com.digital.dispatch.TaxiLimoSoap.requests.RecallJobsRequest.IRecallJobsResponseListener;
import com.digital.dispatch.TaxiLimoSoap.requests.Request.IRequestTimerListener;
import com.digital.dispatch.TaxiLimoSoap.responses.JobItem;
import com.digital.dispatch.TaxiLimoSoap.responses.RecallJobsResponse;
import com.digital.dispatch.TaxiLimoSoap.responses.ResponseWrapper;
import com.google.android.gms.maps.model.LatLng;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

import digital.dispatch.TaxiLimoNewUI.DBBooking;
import digital.dispatch.TaxiLimoNewUI.DBBookingDao;
import digital.dispatch.TaxiLimoNewUI.DBBookingDao.Properties;
import digital.dispatch.TaxiLimoNewUI.DaoManager.DaoManager;
import digital.dispatch.TaxiLimoNewUI.MainActivity;
import digital.dispatch.TaxiLimoNewUI.R;
import digital.dispatch.TaxiLimoNewUI.Track.TrackDetailActivity;
import digital.dispatch.TaxiLimoNewUI.Utils.Logger;
import digital.dispatch.TaxiLimoNewUI.Utils.MBDefinition;
import digital.dispatch.TaxiLimoNewUI.Utils.Utils;

public class RecallJobTask extends AsyncTask<String, Integer, Boolean> implements IRecallJobsResponseListener, IRequestTimerListener {
    private static final String TAG = "RecallJobTask";
    private RecallJobsRequest rjReq;
    private Context _context;
    private String jobList;
    private int which;
    private boolean tridChanged;
    private List<String> reqJobs;

    public RecallJobTask(Context context, String jobs, int which) {
        _context = context;
        jobList = jobs;
        this.which = which;
        tridChanged = false;
    }

    // Before running code in separate thread
    @Override
    protected void onPreExecute() {

    }

    // The code to be executed in a background thread.
    @Override
    protected Boolean doInBackground(String... params) {
        try {
            rjReq = new RecallJobsRequest(this, this);
            rjReq.setSysID(params[1]);
            rjReq.setOspVersion(MBDefinition.OSP_VERSION);
            rjReq.setCriteria("10"); // recall by TRID

            rjReq.setTaxiCompanyID(params[0]);

            rjReq.setJobList(jobList); // can just concatenate into one string
            rjReq.sendRequest(_context.getResources().getString(R.string.name_space), _context.getResources().getString(R.string.url));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    @Override
    protected void onPostExecute(Boolean isReqSent) {

    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        // set the current progress of the progress dialog
        // progressDialog.setProgress(values[0]);
    }

    @Override
    public void onResponseReady(RecallJobsResponse response) {
        JobItem[] jobArr = response.GetList();
        for (int i = 0; i < jobArr.length; i++) {
            JobItem.printJobItem(jobArr[i]);
        }

        List<DBBooking> dbBook = updateDBJobs(jobArr);

        if (which == MBDefinition.IS_FOR_MAP || which == MBDefinition.IS_FOR_ONE_JOB) {
            LatLng carLatLng = new LatLng(Double.parseDouble(jobArr[0].carLatitude), Double.parseDouble(jobArr[0].carLongitude));
            ((TrackDetailActivity) _context).parseRecallJobResponse(dbBook, carLatLng);

            if (tridChanged) {
                ((TrackDetailActivity) _context).startRecallJobTask();
            }
        } else if (which == MBDefinition.IS_FOR_LIST) {
            ((MainActivity) _context).trackFragment.updateStatus(dbBook);
            if (tridChanged) {
                ((MainActivity) _context).trackFragment.startRecallJobTask();
            }
        }
    }

    private List<DBBooking> updateDBJobs(JobItem[] jobArr) {

        //parse jobList to array
        StringTokenizer st = new StringTokenizer(jobList, ",");
        reqJobs = new ArrayList<String>();
        while (st.hasMoreTokens()) {
            reqJobs.add(st.nextToken());
        }
        Logger.v("# of job requested: " + reqJobs.size());

        List<DBBooking> bookingList = new ArrayList<DBBooking>();
        DaoManager daoManager = DaoManager.getInstance(_context);
        DBBookingDao bookingDao = daoManager.getDBBookingDao(DaoManager.TYPE_WRITE);
        DBBooking dbBook = new DBBooking();
        for (int i = 0; i < jobArr.length; i++) {
            dbBook = bookingDao.queryBuilder().where(Properties.Taxi_ride_id.eq(jobArr[i].taxi_ride_id)).list().get(0);
            JobItem job = jobArr[i];
            if (!job.redispatchJobID.equals("")) {
                Logger.d(TAG, "job is redispatched!");
                dbBook.setTaxi_ride_id(Integer.parseInt(job.redispatchJobID));
                tridChanged = true;
                bookingDao.update(dbBook);
                bookingList.add(dbBook);
            } else {
                if (job.tripStatusUniformCode == null || job.tripStatusUniformCode.equals("")) {
                    //TL-344 Grey out Cancel Trip button for jobs that are in Unknown state.
                    dbBook.setShouldForceDisableCancel(true);
                } else {
                    switch (Integer.parseInt(job.tripStatusUniformCode)) {
                        case MBDefinition.TRIP_STATUS_BOOKED:
                        case MBDefinition.TRIP_STATUS_DISPATCHING:
                            dbBook.setShouldForceDisableCancel(false);
                            break;
                        case MBDefinition.TRIP_STATUS_ACCEPTED:
                            dbBook.setTripStatus(MBDefinition.MB_STATUS_ACCEPTED);
                            dbBook.setShouldForceDisableCancel(false);
                            break;
                        case MBDefinition.TRIP_STATUS_ARRIVED:
                            dbBook.setTripStatus(MBDefinition.MB_STATUS_ARRIVED);
                            dbBook.setShouldForceDisableCancel(false);
                            break;
                        case MBDefinition.TRIP_STATUS_COMPLETE:
                            if (job.detailTripStatusUniformCode == null || job.detailTripStatusUniformCode.length()==0) {
                                //dont do any update when status is off
                            } else {
                                switch (Integer.parseInt(job.detailTripStatusUniformCode)) {
                                    case MBDefinition.DETAIL_STATUS_IN_SERVICE:
                                        dbBook.setTripStatus(MBDefinition.MB_STATUS_IN_SERVICE);
                                        dbBook.setShouldForceDisableCancel(false);
                                        break;
                                    case MBDefinition.DETAIL_STATUS_COMPLETE:
                                        dbBook.setTripStatus(MBDefinition.MB_STATUS_COMPLETED);
                                        dbBook.setShouldForceDisableCancel(false);
                                        break;
                                    case MBDefinition.DETAIL_STATUS_CANCEL:
                                        dbBook.setTripStatus(MBDefinition.MB_STATUS_CANCELLED);
                                        dbBook.setShouldForceDisableCancel(false);
                                        break;
                                    // special complete: no show, force complete etc. set as "Cancelled" to user
                                    case MBDefinition.DETAIL_STATUS_NO_SHOW:
                                    case MBDefinition.DETAIL_STATUS_FORCE_COMPLETE:
                                        if (dbBook.getTripCompletionTime().length() == 0) {
                                            Calendar cal = Calendar.getInstance();
                                            SimpleDateFormat pickupTimeFormat = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss", Locale.US);
                                            dbBook.setTripCompletionTime(pickupTimeFormat.format(cal.getTime()));
                                        }
                                        dbBook.setTripStatus(MBDefinition.MB_STATUS_CANCELLED);
                                        dbBook.setShouldForceDisableCancel(false);
                                        break;
                                    // other unimportant intermediate status, just ignore
                                    case MBDefinition.DETAIL_OTHER_IGNORE:
                                    default:
                                        //TL-344 Grey out Cancel Trip button for jobs that are in Unknown state.
                                        dbBook.setShouldForceDisableCancel(true);
                                        break;
                                }
                            }
                            break;
                        default:
                            //TL-344 Grey out Cancel Trip button for jobs that are in Unknown state.
                            dbBook.setShouldForceDisableCancel(true);
                            break;
                    }
                }
                dbBook.setCarLatitude(Double.parseDouble(job.carLatitude));
                dbBook.setCarLongitude(Double.parseDouble(job.carLongitude));
                dbBook.setDispatchedCar(job.dispatchedCar);
                dbBook.setDispatchedDriver(job.dispatchedDriver);

                bookingDao.update(dbBook);

                bookingList.add(dbBook);
            }
            //remove this job from reqJobs if it's response is updated
            reqJobs.remove(jobArr[i].taxi_ride_id);
        }
        //TL-442
        if (!reqJobs.isEmpty()) {
            //set the rest of job status to UNKNOWN
            for (int i = 0; i < reqJobs.size(); i++) {
                Logger.d(TAG, "set unknow status for job " + reqJobs.get(i));
                dbBook = bookingDao.queryBuilder().where(Properties.Taxi_ride_id.eq(reqJobs.get(i))).list().get(0);
                dbBook.setShouldForceDisableCancel(true);
                //dbBook.setTripStatus(MBDefinition.MB_STATUS_UNKNOWN);
                bookingDao.update(dbBook);
                bookingList.add(dbBook);

            }

        }
        return bookingList;
    }

    @Override
    public void onErrorResponse(ResponseWrapper resWrapper) {
        if (which == MBDefinition.IS_FOR_ONE_JOB) {
            ((TrackDetailActivity) _context).stopUpdateAnimation();
        } else if (which == MBDefinition.IS_FOR_LIST) {
            ((MainActivity) _context).trackFragment.stopUpdateAnimation();
        }
        try {
            Utils.showErrorDialog(_context.getString(R.string.err_msg_no_response), _context);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Logger.v(TAG, "error response: " + resWrapper.getErrorString());
    }

    @Override
    public void onError() {
        if (which == MBDefinition.IS_FOR_ONE_JOB) {
            ((TrackDetailActivity) _context).stopUpdateAnimation();
        } else if (which == MBDefinition.IS_FOR_LIST) {
            ((MainActivity) _context).trackFragment.stopUpdateAnimation();
        } else {
            ((TrackDetailActivity) _context).stopUpdateAnimation();
        }
        try {
            Utils.showMessageDialog(_context.getString(R.string.err_msg_no_response), _context);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Logger.v(TAG, "no response");
    }

    @Override
    public void onProgressUpdate(int progress) {

    }
}
