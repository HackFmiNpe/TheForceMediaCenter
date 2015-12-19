package npe.hackfmi.mediacontrollerapp.tasks;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.Timer;

/**
 * By Antoan Angelov on 19-Dec-15.
 */
public class InitialiseAsyncTask extends AsyncTask<Void, Void, Void> {

    private static final String             DST_ADDRESS     = "192.168.0.123";
    private static final int                DST_PORT        = 49155;
    public static final int                 TIMEOUT         = 30 * 1000; // half a minute
    private static final long               PERIOD_MILLIS   = 33;

    private final LinkedList<JSONObject>    mQueue          = new LinkedList<>();
    private Timer                           mTimer;
    private Socket                          mSocket;

    @Override
    protected Void doInBackground(Void... params) {

        try {

            mSocket = new Socket(DST_ADDRESS, DST_PORT);

            OutputStreamWriter out;
            try {
                out = new OutputStreamWriter(mSocket.getOutputStream(), StandardCharsets.UTF_8);
                //Log.v("tag", "IS THIS WORKING?? sending " + message);
                out.write("{\"alabala\":12}");
                out.flush();
                Log.v("tag", "SENT!!");
            }
            catch (Exception e) {
                e.printStackTrace();
            }







            //mTimer = new Timer();
            //mTimer.schedule(new SendMessageAsyncTask(mSocket, mQueue), 0, PERIOD_MILLIS);
        } catch (IOException e) {
            e.printStackTrace();
            try {
                if (mSocket != null) {
                    mSocket.close();
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }

        return null;
    }

    public void stop() {
        if (mTimer != null) {
            mTimer.cancel();
        }

        if (mSocket != null) {
            try {
                mSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void addRequest(String gesture, String file_name) {
        try {
            mQueue.add(new JSONObject()
                    .put("gesture", gesture)
                    .put("file_name", file_name));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void addRequest(String gesture) {
        try {
            mQueue.add(new JSONObject()
                    .put("gesture", gesture));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
