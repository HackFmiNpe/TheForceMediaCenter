package npe.hackfmi.mediacontrollerapp.services;

import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import npe.hackfmi.mediacontrollerapp.MainActivity;
import npe.hackfmi.mediacontrollerapp.tasks.InitialiseAsyncTask;

/**
 * By Antoan Angelov on 19-Dec-15.
 */
public class ListenerService extends WearableListenerService {

    String nodeId;

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        nodeId = messageEvent.getSourceNodeId();
        MainActivity.sendMessage(messageEvent.getPath());
        showToast(messageEvent.getPath());
    }

    private void showToast(String message) {
        Log.v("tag", "RECEIVED MESSAGE! " + message);
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
