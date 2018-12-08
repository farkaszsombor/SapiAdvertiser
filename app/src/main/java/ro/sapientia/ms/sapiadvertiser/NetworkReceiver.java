package ro.sapientia.ms.sapiadvertiser;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NetworkReceiver extends BroadcastReceiver {
    private  InternetStateListener networkStateListener;
    public NetworkReceiver(InternetStateListener networkStateListener){
        this.networkStateListener=networkStateListener;
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        if(Utils.isNetworkAvailable(context)) {
            networkStateListener.onConnected();
        }
        else {
            networkStateListener.onDisconected();
        }
    }
}
