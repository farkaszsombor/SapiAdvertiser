package ro.sapientia.ms.sapiadvertiser.Activities;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import ro.sapientia.ms.sapiadvertiser.Fragments.CreateAdFragment;
import ro.sapientia.ms.sapiadvertiser.Fragments.DetailsFragment;
import ro.sapientia.ms.sapiadvertiser.Fragments.HomeFragment;
import ro.sapientia.ms.sapiadvertiser.Fragments.ProfileFragment;
import ro.sapientia.ms.sapiadvertiser.R;
import ro.sapientia.ms.sapiadvertiser.Utils.FragmentManager;

public class MainActivity extends AppCompatActivity implements HomeFragment.OnFragmentInteractionListener
                                                    ,ProfileFragment.OnFragmentInteractionListener
                                                    ,DetailsFragment.OnFragmentInteractionListener
                                                    ,CreateAdFragment.OnFragmentInteractionListener {

    private FragmentManager manager;

    private BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            Fragment selectedFragment;
            switch (menuItem.getItemId()){
                case R.id.tud:
                    selectedFragment = new CreateAdFragment();
                    break;
                case R.id.home:
                    selectedFragment = new HomeFragment();
                    break;
                case R.id.profile:
                    selectedFragment = new ProfileFragment();
                    break;
                default:
                    selectedFragment = new HomeFragment();
                    break;
            }
            manager.executeTransaction(selectedFragment,R.id.frame_layout,false);
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        manager = new FragmentManager(MainActivity.this);

        BottomNavigationView bottomNavigationView = findViewById(R.id.navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);

        manager.executeTransaction(new HomeFragment(),R.id.frame_layout,true);

        bottomNavigationView.getMenu().getItem(1).setChecked(true);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onBackPressed() {

        if(getSupportFragmentManager().getBackStackEntryCount() == 1){
            getSupportFragmentManager().popBackStackImmediate();
        }
        else {
            super.onBackPressed();
        }
    }

}
