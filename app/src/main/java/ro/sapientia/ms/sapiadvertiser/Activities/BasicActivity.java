package ro.sapientia.ms.sapiadvertiser.Activities;

import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import ro.sapientia.ms.sapiadvertiser.R;

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

    protected void toggleViews(boolean enable)
    {
        if(mViews!=null) {
            for (View v : mViews) {
                v.setEnabled(enable);

                if (v instanceof Button) {
                    if (!enable) {
                        v.setBackgroundColor(Color.parseColor("#737373"));
                    } else {
                        v.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    }
                }
            }

            if (noInternetTextView != null) {
                noInternetTextView.setVisibility(enable ? View.INVISIBLE : View.VISIBLE);
            }
        }
    }
    @Override
    public void onDisconected() {
        Log.d(TAG, "onDisconected");
        toggleViews(false);
        Toast.makeText(this, "No internet connection", Toast.LENGTH_LONG).show();
    }
    @Override
    public void onConnected() {
        Log.d(TAG, "onConnected");
        toggleViews(true);
    }
}
