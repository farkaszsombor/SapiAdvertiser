package ro.sapientia.ms.sapiadvertiser;

import android.content.Context;
import android.net.ConnectivityManager;
import android.util.Log;


public class Utilss {

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connec = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
            android.net.NetworkInfo wifi = connec.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

            android.net.NetworkInfo mobile = connec.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            if (wifi.isConnected()||mobile.isConnected())
                return true;
            else if (wifi.isConnected() && mobile.isConnected())
                return true;
            else
                return false;

        } catch (NullPointerException e) {
            Log.d("ConStatus", "No Active Connection");
            return false;
        }


        //legnagyobb baj az az, hogy kezdetbe nullpointer exeptiont kapok akkor is ha van net
        //valamiert ugy erzekeli hogy nincs meg a prioritasom kezdetben
    }
}
