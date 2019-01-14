package ro.sapientia.ms.sapiadvertiser.Activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import ro.sapientia.ms.sapiadvertiser.Utils.NetworkUtils;

public class NetworkReceiver extends BroadcastReceiver {
    private  InternetStateListener networkStateListener;
    public NetworkReceiver(InternetStateListener networkStateListener){
        this.networkStateListener=networkStateListener;
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        if(NetworkUtils.isNetworkAvailable(context)) {
            networkStateListener.onConnected();
        }
        else {
            networkStateListener.onDisconected();
        }
    }
}
