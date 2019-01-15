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

    /**
     *
     * @param fragment the fragment which will be placed on top
     * @param resource layout ID where we will inflate the fragment
     * @param name name of fragment as an unique ID
     * @param isLast controls the fragment replacement errors
     *
     *      function handles fragment transaction and manages back stack as well
     *
     */
    public void executeTransaction(Fragment fragment, int resource,String name, boolean isLast){


        for(int i = 0; i < ((MainActivity) context).getSupportFragmentManager().getBackStackEntryCount(); ++i){
            ((MainActivity) context).getSupportFragmentManager().popBackStack();
        }
        if(fragment != null) {
            FragmentTransaction transaction = ((MainActivity) context).getSupportFragmentManager().beginTransaction();
            if(!isLast){
                transaction.replace(resource, fragment, name).addToBackStack("name");
            }
            else{
                transaction.replace(resource, fragment, name);
            }
            transaction.commit();
        }
    }

    /**
     *
     * @param fragmentName name of fragment we use it to identify the fragment
     * @return true if fragment is currently active
     * otherwise false is returned
     */
    public boolean isActive(String fragmentName){

        Fragment fragment = ((MainActivity) context).getSupportFragmentManager().findFragmentByTag(fragmentName);
        return fragment != null && fragment.isVisible();
    }
}
