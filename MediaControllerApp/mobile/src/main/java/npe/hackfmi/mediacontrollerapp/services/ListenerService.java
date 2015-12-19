package npe.hackfmi.mediacontrollerapp.services;

import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

/**
 * By Antoan Angelov on 19-Dec-15.
 */
public class ListenerService extends WearableListenerService {

    String nodeId;

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        nodeId = messageEvent.getSourceNodeId();
        showToast(messageEvent.getPath());
    }

    private void showToast(String message) {
        Log.v("tag", "RECEIVED MESSAGE! " + message);
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

}
