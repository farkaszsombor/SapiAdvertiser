package ro.sapientia.ms.sapiadvertiser;

import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public abstract class BasicActivity extends AppCompatActivity implements InternetStateListener{


    private static final String TAG = BasicActivity.class.getSimpleName();
    protected NetworkReceiver networkReceiver;
    protected ArrayList<View> mViews;
    protected TextView noInternetTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        networkReceiver= new NetworkReceiver(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkReceiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(networkReceiver);
    }

    protected void toggleViews()
    {
        for (View v:mViews) {
            v.setEnabled(!v.isEnabled());

            if(v instanceof Button)
            {
                if(v.isEnabled()) {
                    v.setBackgroundColor(Color.parseColor("#737373"));
                }
                else{
                    v.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                }
            }
        }

        if(noInternetTextView!=null)
        {
            if(noInternetTextView.getVisibility()==View.VISIBLE)
            {
                noInternetTextView.setVisibility(View.INVISIBLE);
            }
            else
            {
                noInternetTextView.setVisibility(View.VISIBLE);
            }
        }
    }
    @Override
    public void onDisconected() {
        Log.d(TAG, "onDisconected");
        toggleViews();
    }
    @Override
    public void onConnected() {
        Log.d(TAG, "onConnected");
        toggleViews();
    }
}
