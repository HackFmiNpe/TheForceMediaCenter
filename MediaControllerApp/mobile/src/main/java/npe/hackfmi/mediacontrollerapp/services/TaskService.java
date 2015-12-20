package npe.hackfmi.mediacontrollerapp.services;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import npe.hackfmi.mediacontrollerapp.tasks.InitialiseAsyncTask;

/**
 * By Antoan Angelov on 20-Dec-15.
 */
public class TaskService extends Service {

    private Handler mHandler;
    private InitialiseAsyncTask mAsyncTask;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.v("tag", "CREATING SERVICE HEY !!!!");
        try {
            Class.forName("android.os.AsyncTask");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        mHandler = new Handler();
        mAsyncTask = new InitialiseAsyncTask();
        mAsyncTask.execute();
        startRepeatingTask();
    }

    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            sendFake();
            mHandler.postDelayed(mStatusChecker, 2000);
        }
    };

    private void sendFake() {
        mAsyncTask.addRequest("open");
    }

    void startRepeatingTask() {
        mStatusChecker.run();
    }

    void stopRepeatingTask() {
        mHandler.removeCallbacks(mStatusChecker);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.v("tag", "ON START COMMAND HEY !!!!");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        Log.v("tag", "ON START SERVICE HEY !!!!");
    }
}
