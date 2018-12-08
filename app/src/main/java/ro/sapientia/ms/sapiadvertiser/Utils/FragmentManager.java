package ro.sapientia.ms.sapiadvertiser.Utils;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import ro.sapientia.ms.sapiadvertiser.Activities.MainActivity;

public class FragmentManager {

    private Context context;

    public FragmentManager(Context context){

        this.context = context;
    }

    public void executeTransaction(Fragment fragment,int resource,boolean isLast){

        if(fragment != null) {
            FragmentTransaction transaction = ((MainActivity) context).getSupportFragmentManager().beginTransaction();
            if(!isLast){
                transaction.replace(resource, fragment).addToBackStack(null);
            }
            else{
                transaction.replace(resource, fragment);
            }
            transaction.commit();
        }
    }
}
