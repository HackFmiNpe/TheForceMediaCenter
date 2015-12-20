package npe.hackfmi.mediacontrollerapp.tasks;

import android.util.Log;

import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.TimerTask;

/**
 * By Antoan Angelov on 19-Dec-15.
 */
public class SendMessageAsyncTask extends TimerTask {

    private final Socket                    mSocket;
    private final LinkedList<JSONObject>    mQueue;

    public SendMessageAsyncTask(Socket s, LinkedList<JSONObject> queue) {
        mSocket = s;
        mQueue = queue;
    }

    @Override
    public void run() {
        if (!mQueue.isEmpty()) {
            OutputStreamWriter out;
            try {
                out = new OutputStreamWriter(mSocket.getOutputStream(), StandardCharsets.UTF_8);
                String message = mQueue.pop().toString();
                Log.v("tag", "IS THIS WORKING?? sending " + message);
                out.write(message + "\n");
                Log.v("tag", "MESSAGE = " + message);
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}