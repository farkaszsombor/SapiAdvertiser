package ro.sapientia.ms.sapiadvertiser.Activities;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import ro.sapientia.ms.sapiadvertiser.Fragments.CreateAdFragment;
import ro.sapientia.ms.sapiadvertiser.Fragments.DetailsFragment;
import ro.sapientia.ms.sapiadvertiser.Fragments.HomeFragment;
import ro.sapientia.ms.sapiadvertiser.Fragments.ProfileUpdateFragment;
import ro.sapientia.ms.sapiadvertiser.R;
import ro.sapientia.ms.sapiadvertiser.Utils.FragmentManager;

public class MainActivity extends AppCompatActivity implements ProfileUpdateFragment.OnFragmentInteractionListener
                                                    ,DetailsFragment.OnFragmentInteractionListener
                                                    ,CreateAdFragment.OnFragmentInteractionListener {

    private FragmentManager manager;
    private FirebaseAuth mAuth;
    private BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            Fragment selectedFragment;
            String name;
            switch (menuItem.getItemId()){
                case R.id.tud:
                    selectedFragment = new CreateAdFragment();
                    name = "create";
                    break;
                case R.id.home:
                    selectedFragment = HomeFragment.newInstance();
                    name = "home";
                    break;
                case R.id.profile:
                    selectedFragment = new ProfileUpdateFragment();
                    name = "profile";
                    break;
                default:
                    selectedFragment = new HomeFragment();
                    name = "home";
                    break;
            }
            if(!manager.isActive(name)){
                manager.executeTransaction(selectedFragment,R.id.frame_layout, name ,false);
            }
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        manager = new FragmentManager(MainActivity.this);

        BottomNavigationView bottomNavigationView = findViewById(R.id.navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);

        manager.executeTransaction(HomeFragment.newInstance(),R.id.frame_layout,"home",true);

        bottomNavigationView.getMenu().getItem(1).setChecked(true);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onBackPressed() {

        if(getSupportFragmentManager().getBackStackEntryCount() > 0){
            getSupportFragmentManager().popBackStackImmediate();
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null) {
            Intent authIntent= new Intent(MainActivity.this,SignUpActivity.class);
            startActivity(authIntent);
            finish();
        }
    }
}
