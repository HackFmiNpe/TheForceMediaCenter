package npe.hackfmi.mediacontrollerapp;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import npe.hackfmi.mediacontrollerapp.tasks.InitialiseAsyncTask;

public class MainActivity extends Activity {

    private InitialiseAsyncTask mAsyncTask;
    private int mInterval = 5000;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            Class.forName("android.os.AsyncTask");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        mHandler = new Handler();
    }

    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            sendFake();
            mHandler.postDelayed(mStatusChecker, mInterval);
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
    protected void onResume() {
        super.onResume();
        mAsyncTask = new InitialiseAsyncTask();
        mAsyncTask.execute();
        startRepeatingTask();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mAsyncTask.stop();
    }
}
