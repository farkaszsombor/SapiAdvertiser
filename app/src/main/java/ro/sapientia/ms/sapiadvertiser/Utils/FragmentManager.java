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

    public void executeTransaction(Fragment fragment, int resource,String name, boolean isLast){


        for(int i = 0; i < ((MainActivity) context).getSupportFragmentManager().getBackStackEntryCount(); ++i){
            ((MainActivity) context).getSupportFragmentManager().popBackStack();
        }
        if(fragment != null) {
            FragmentTransaction transaction = ((MainActivity) context).getSupportFragmentManager().beginTransaction();
            if(!isLast){
                transaction.replace(resource, fragment, name).addToBackStack(null);
            }
            else{
                transaction.replace(resource, fragment, name);
            }
            transaction.commit();
        }
    }

    public boolean isActive(String fragmentName){

        Fragment fragment = ((MainActivity) context).getSupportFragmentManager().findFragmentByTag(fragmentName);
        return fragment != null && fragment.isVisible();
    }
}
