package npe.hackfmi.mediacontrollerapp;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import npe.hackfmi.mediacontrollerapp.tasks.InitialiseAsyncTask;

public class MainActivity extends Activity {

    private static InitialiseAsyncTask mAsyncTask;
    private static final int mInterval = 5000;
    private static Handler mHandler;

    public static void sendMessage(String message) {
        Log.v("tag", "RECEIVED MESSAGE! " + message);
        mAsyncTask.addRequest(message);
    }

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

    private static Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            sendFake();
            mHandler.postDelayed(mStatusChecker, mInterval);
        }
    };

    private static void sendFake() {
        mAsyncTask.addRequest("open");
    }

    private static void startRepeatingTask() {
        mStatusChecker.run();
    }

    private static void stopRepeatingTask() {
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
